package com.jools.rpc.registry.strategy;

import com.jools.rpc.registry.RegistryServiceCache;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/24 13:21
 * @description: TODO
 */
public class EtcdWatchStrategy implements WatchStrategy {

    private Watch watchClient;
    private Set<String> watchServiceKeySet;
    private RegistryServiceCache registryServiceCache;

    public EtcdWatchStrategy() {
    }

    public EtcdWatchStrategy(Watch watchClient) {
        this.watchClient = watchClient;
    }

    @Override
    public void watch(String serviceNodeKey) {
        //之前未被监听，开启监听
        boolean watched = watchServiceKeySet.add(serviceNodeKey);
        if (watched) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        // key 删除时触发
                        case DELETE:
                            // 清理注册服务缓存
                            registryServiceCache.clear(serviceNodeKey.substring(0, serviceNodeKey.lastIndexOf("/")));
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }

    public void setWatchClient(Watch watchClient) {
        this.watchClient = watchClient;
    }

    public void setWatchServiceKeySet(Set<String> watchServiceKeySet) {
        this.watchServiceKeySet = watchServiceKeySet;
    }

    public void setRegistryServiceCache(RegistryServiceCache registryServiceCache) {
        this.registryServiceCache = registryServiceCache;
    }
}
