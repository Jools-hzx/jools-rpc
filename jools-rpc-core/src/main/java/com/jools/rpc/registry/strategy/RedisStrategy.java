package com.jools.rpc.registry.strategy;

import cn.hutool.core.util.StrUtil;
import com.google.gson.internal.LinkedHashTreeMap;
import com.jools.rpc.registry.RegistryServiceCache;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Set;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/24 20:10
 * @description: TODO
 */
@Slf4j
public class RedisStrategy implements WatchStrategy {

    private Jedis jedis;
    private Set<String> watchServiceKeySet;
    private RegistryServiceCache registryServiceCache;

    private String ip;
    private Integer port;
    private String password;

    @Override
    public void watch(String serviceNodeKey) {
        // 避免重复监听
        boolean watched = watchServiceKeySet.add(serviceNodeKey);
        if (watched) {
            log.info("Start watching service node key: {}", serviceNodeKey);

            // 创建一个独立线程进行订阅
            new Thread(() -> {
                try (Jedis subscriberJedis = new Jedis(ip, port)) {

                    // 如果 Redis 设置了密码，需认证
                    if (StrUtil.isNotBlank(password)) {
                        subscriberJedis.auth(password);
                    }

                    // 订阅键事件
                    subscriberJedis.psubscribe(new JedisPubSub() {
                        @Override
                        public void onPMessage(String pattern, String channel, String message) {
                            log.info("Received event: {} for key: {}", channel, message);

                            // 监听键的删除或过期事件
                            if (channel.contains(":expired") || channel.contains(":del")) {
                                try (Jedis operationJedis = new Jedis(ip, port)) {
                                    // 如果 Redis 设置了密码，需认证
                                    if (StrUtil.isNotEmpty(password)) {
                                        operationJedis.auth(password);
                                    }

                                    // 清理本地缓存
                                    String cacheKey = serviceNodeKey.substring(0, serviceNodeKey.lastIndexOf("/"));
                                    registryServiceCache.clear(cacheKey);
                                    log.info("Removed key from local cache: {}", message);
                                } catch (Exception e) {
                                    log.error("Error during Redis operation in subscription callback", e);
                                }
                            }
                        }
                    }, "__keyevent@0__:expired", "__keyevent@0__:del");
                } catch (Exception e) {
                    log.error("Error in Redis subscription thread", e);
                }
            }).start();
        }
    }

    public void setJedis(Jedis jedis, String ip, Integer port, String pwd) {
        this.jedis = jedis;
        this.ip = ip;
        this.port = port;
        this.password = pwd;
    }

    public void setWatchServiceKeySet(Set<String> watchServiceKeySet) {
        this.watchServiceKeySet = watchServiceKeySet;
    }

    public void setRegistryServiceCache(RegistryServiceCache registryServiceCache) {
        this.registryServiceCache = registryServiceCache;
    }
}
