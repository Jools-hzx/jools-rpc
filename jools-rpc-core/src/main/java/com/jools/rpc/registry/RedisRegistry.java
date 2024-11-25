package com.jools.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.model.ServiceMetaInfo;
import com.jools.rpc.registry.strategy.RedisStrategy;
import com.jools.rpc.registry.strategy.WatchStrategy;
import com.jools.rpc.utils.DateUtils;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.KeyValue;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/24 19:09
 * @description: TODO
 */
@Slf4j
public class RedisRegistry implements Registry {

    /**
     * 服务注册节点信息 (ServiceNodeKey)
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * Jedis 客户端
     */
    private Jedis jedis;

    private static final String REDIS_ROOT_HEADER = "/rpc/";

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 注册中心被监听的服务
     */
    private final Set<String> watchServiceKeySet = new ConcurrentHashSet<>();

    private WatchStrategy watchStrategy;

    private boolean isHeartBeatScheduled = false;

    public void setWatchStrategy(WatchStrategy watchStrategy) {
        this.watchStrategy = watchStrategy;
    }

    @Override
    public void init(RegistryConfig registryConfig) {
        //默认是: http://localhost:2379
        String address = registryConfig.getAddress();
        String ip = address.substring(0, address.lastIndexOf(":"));
        Integer port = Integer.parseInt(address.substring(address.lastIndexOf(":") + 1));  //分割端口
        if (address.contains("://")) {  //分割ip
            ip = address.substring(address.lastIndexOf("/") + 1, address.lastIndexOf(":"));
        }

        this.jedis = new Jedis(ip, port);

        //设置密码
        String password = registryConfig.getPassword();
        if (!StrUtil.isBlank(password)) {
            jedis.auth(registryConfig.getPassword());
        }

        //启动监听
        heartBeat();

        //设置监听策略 - Redis
        if (ObjectUtil.isNull(watchStrategy)) {
            setWatchStrategy(new RedisStrategy());
            //设置 Jedis 并传入连接信息
            ((RedisStrategy) watchStrategy).setJedis(this.jedis, ip, port, password);
            //监听服务key集合
            ((RedisStrategy) watchStrategy).setWatchServiceKeySet(this.localRegisterNodeKeySet);
            //本地服务缓存
            ((RedisStrategy) watchStrategy).setRegistryServiceCache(this.registryServiceCache);
        }
    }

    @Override
    public void heartBeat() {
        if (isHeartBeatScheduled) {
            log.warn("HeartBeat task is already scheduled.");
            return;
        }

        isHeartBeatScheduled = true;
        //10s 续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                for (String nodeKey : localRegisterNodeKeySet) {
                    String serviceMetaInfoStr;
                    try {
                        //查询该服务节点
                        serviceMetaInfoStr = jedis.get(nodeKey);

                        // 节点已经过期，需要重启节点才能重新注册
                        if (StrUtil.isEmpty(serviceMetaInfoStr)) {
                            continue;
                        }

                        //续签节点
                        //基于服务节点查询，仅得到唯一结果；
                        //注册时基于 JSON 格式，转化为 JSON 格式后封装
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(serviceMetaInfoStr, ServiceMetaInfo.class);

                        //续约
                        registry(serviceMetaInfo);

                    } catch (Exception e) {
                        throw new RuntimeException("Service renewal fail! --- Service key node:" + nodeKey);
                    }
                }
            }
        });

        /*
         * 设置是否支持秒匹配<br>
            public static void setMatchSecond(boolean isMatchSecond) {
                scheduler.setMatchSecond(isMatchSecond);
            }
         */
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        if (!watchServiceKeySet.add(serviceNodeKey)) {
            log.info("Service node key {} is already being watched.", serviceNodeKey);
            return;
        }
        watchStrategy.watch(serviceNodeKey);
    }

    @Override
    public boolean registry(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {

        //构建注册 key -> /rpc/ + serviceName:version/ip:port
        String key = REDIS_ROOT_HEADER + serviceMetaInfo.getServiceNodeKey();
        try {
            //注册时间
            serviceMetaInfo.setRegisterTime(DateUtils.formatLocalTimeDate(LocalDateTime.now()));
            //设置过期时间为 30s
            jedis.setex(key, 30L, JSONUtil.toJsonStr(serviceMetaInfo));
        } catch (Exception e) {
            log.error("Fail register serviceKey:{}, reason:{}", key, e.getMessage());
            return false;
        }

        //存储该节点的 key 到本地服务key 集合
        log.info("Register Service Node Key:{}", key);
        this.localRegisterNodeKeySet.add(key);

        return true;
    }

    @Override
    public boolean unRegistry(ServiceMetaInfo serviceMetaInfo) {
        String serviceNodeKey = serviceMetaInfo.getServiceNodeKey();
        String delKey = "";
        try {
            delKey = REDIS_ROOT_HEADER + serviceNodeKey;
            this.jedis.del(delKey);
            //删除本地服务节点信息
            this.localRegisterNodeKeySet.remove(delKey);
            this.registryServiceCache.clear(delKey);
        } catch (Exception e) {
            log.error("Fail to unRegister service:{}", delKey);
            e.printStackTrace();
            return false;
        }
        //更新缓存
        return true;
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //构建查询的 key 基于 /rpc/serviceName:version
        String searchKey = REDIS_ROOT_HEADER + serviceKey;
        //先查询缓存
        Map<String, List<ServiceMetaInfo>> serviceCache = registryServiceCache.serviceCache;
        if (serviceCache.containsKey(searchKey) && !serviceCache.get(searchKey).isEmpty()) {
            log.info("ServiceKey:{} hit Registry Service Cache, read data from Cache", searchKey);
            return registryServiceCache.readCache(searchKey);
        }
        //基于前缀搜索所有匹配的服务注册信息
        String searchPrefix = searchKey + "/";
        try {
            Set<String> keys = new HashSet<>();
            String cursor = "0";

            do {
                // 使用 SCAN 命令逐步获取匹配的键
                ScanParams scanParams = new ScanParams();
                // 设置匹配模式，这里使用前缀加通配符的形式来匹配以searchPrefix开头的key
                scanParams.match(searchPrefix + "*");
                // 可以设置每次迭代返回元素的数量上限，这里设置为1000，可根据实际情况调整
                scanParams.count(100);
                ScanResult<String> result = jedis.scan(cursor, scanParams);
                List<String> currentKeys = result.getResult();
                // 将本次扫描得到的keys添加到总的keys列表中
                keys.addAll(currentKeys);
                // 获取下一次扫描的游标
                cursor = result.getCursor();
            } while (!cursor.equals("0"));

            List<ServiceMetaInfo> serviceMetaInfos = keys.stream().map(
                    (k) -> {
                        //发现服务，开始监控
                        watch(k);
                        //基于 key 查询该服务注册信息
                        String serviceInfos = jedis.get(k);
                        //映射成为 ServiceMetaInfo
                        return JSONUtil.toBean(serviceInfos, ServiceMetaInfo.class);
                    }
            ).toList();
            //更新缓存
            this.registryServiceCache.writeCache(searchKey, serviceMetaInfos);
            return serviceMetaInfos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destory() {
        //下线本地保存的注册节点信息
        for (String key : localRegisterNodeKeySet) {
            jedis.del(key);
            log.info("Service key :{} has been remove from register set", key);
            registryServiceCache.clear(key);
        }
        localRegisterNodeKeySet.clear();
        watchServiceKeySet.clear();

        if (jedis != null) {
            try {
                jedis.close();
            } catch (Exception e) {
                log.error("Fail to close Jedis", e);
            }
        }

        // 停止心跳定时任务
        CronUtil.stop();
    }
}
