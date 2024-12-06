package com.jools.rpc.registry;

import cn.hutool.core.util.ObjectUtil;
import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.model.ServiceMetaInfo;
import com.jools.rpc.registry.strategy.WatchStrategy;
import com.jools.rpc.registry.strategy.ZooKeeperStrategy;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/11 10:59
 * @description: TODO
 */
@Slf4j
public class ZooKeeperRegistry implements Registry {

    /**
     * ZK Client 端
     */
    private CuratorFramework client;

    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;

    /**
     * 注册的服务节点key - serviceKey/ip:port
     */
    private final Set<String> localRegisterNodeKeySet = new ConcurrentHashSet<>();

    /**
     * 服务信息缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchServiceKeySet = new ConcurrentHashSet<>();

    /**
     * 根节点
     */
    public static final String ZK_ROOT_PATH = "/rpc/zk";

    /**
     * 监听策略
     */
    private WatchStrategy watchStrategy;

    public void setWatchStrategy(WatchStrategy watchStrategy) {
        this.watchStrategy = watchStrategy;
    }

    @Override
    public void init(RegistryConfig registryConfig) {
        //构建 client
        client = CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();


        //构建 serviceDiscovery 实例
        serviceDiscovery = ServiceDiscoveryBuilder
                .builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();

        try {
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //设置监听机制
        if (ObjectUtil.isNull(watchStrategy)) {
            setWatchStrategy(new ZooKeeperStrategy());
            ((ZooKeeperStrategy) watchStrategy).setClient(this.client);
            ((ZooKeeperStrategy) watchStrategy).setRegistryServiceCache(this.registryServiceCache);
            ((ZooKeeperStrategy) watchStrategy).setWatchServiceKeySet(this.watchServiceKeySet);
        }
    }

    @Override
    public void heartBeat() {
        //无心跳机制
    }

    @Override
    public void watch(String serviceNodeKey) {
        if (!watchServiceKeySet.add(serviceNodeKey)) {
            log.info("Service node key {} is already being watched.", serviceNodeKey);
            return;
        }
        log.info("Start watching service node key:{}, using strategy:{}",
                serviceNodeKey,
                watchStrategy.getClass().getSimpleName());
        watchStrategy.watch(serviceNodeKey);
    }

    @Override
    public boolean registry(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        //注册到 ZK
        String serviceNodeKey = serviceMetaInfo.getServiceNodeKey();
        String localRegisterNodeKey = ZK_ROOT_PATH + "/" + serviceNodeKey;
        try {
            //记录注册时间
            setRegistryTimeDate(serviceMetaInfo);
            serviceDiscovery.registerService(createServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            log.error("Fail to register serviceNode Key:{}", localRegisterNodeKey);
            return false;
        }
        return localRegisterNodeKeySet.add(localRegisterNodeKey);
    }

    @Override
    public boolean unRegistry(ServiceMetaInfo serviceMetaInfo) {
        String localRegisterNodeKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        try {
            serviceDiscovery.unregisterService(createServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            log.error("Fail to unregister Service Node Key:{}", localRegisterNodeKey);
            return false;
        }
        return localRegisterNodeKeySet.remove(localRegisterNodeKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        String searchKey = ZK_ROOT_PATH + "/" + serviceKey;

        //先查询缓存
        Map<String, List<ServiceMetaInfo>> serviceCache = this.registryServiceCache.serviceCache;
        if (serviceCache.containsKey(searchKey) && !serviceCache.get(searchKey).isEmpty()) {
            log.info("ServiceKey:{} hit Registry Service Cache, read data from Cache", searchKey);
            return registryServiceCache.readCache(searchKey);
        }

        try {
            // 查询服务信息
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstanceList = serviceDiscovery.queryForInstances(serviceKey);

            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = serviceInstanceList.stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());

            // 写入服务缓存
            registryServiceCache.writeCache(searchKey, serviceMetaInfoList);
            for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
                String keyNode = serviceMetaInfo.getServiceNodeKey();
                watch(keyNode);
            }
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("Fail to query services list", e);
        }
    }

    @Override
    public void destory() {
        for (String key : localRegisterNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(key);
                registryServiceCache.clear(key);
            } catch (Exception e) {
                log.error("Fail to destroy ZooKeeper Client");
                throw new RuntimeException("Service key node: " + key + " destroy fail!");
            }
        }
        this.localRegisterNodeKeySet.clear();
        this.watchServiceKeySet.clear();
        if (serviceDiscovery != null) {
            try {
                serviceDiscovery.close();
            } catch (IOException e) {
                log.error("Fail to close ZooKeeper ServiceDiscovery");
                throw new RuntimeException(e);
            }
        }
        if (client != null) {
            client.close();
        }
    }

    private ServiceInstance<ServiceMetaInfo> createServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        //拼接 ServiceAddr, ip:port
        String serviceAddress = serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort();
        try {
            return ServiceInstance
                    .<ServiceMetaInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMetaInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
