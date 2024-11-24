package com.jools.rpc.registry.strategy;

import com.jools.rpc.registry.RegistryServiceCache;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

import java.util.Set;

import static com.jools.rpc.registry.ZooKeeperRegistry.ZK_ROOT_PATH;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/24 13:53
 * @description: TODO
 */
public class ZooKeeperStrategy implements WatchStrategy {

    private CuratorFramework client;
    private Set<String> watchServiceKeySet;
    private RegistryServiceCache registryServiceCache;

    @Override
    public void watch(String serviceNodeKey) {
        String key = ZK_ROOT_PATH + "/" + serviceNodeKey;
        String serviceKey = key.substring(0, key.lastIndexOf("/"));
        boolean newAdd = watchServiceKeySet.add(key);
        if (newAdd) {
            CuratorCache curatorCache = CuratorCache.build(client, key);
            curatorCache.start();
            //基于 ServiceNodeKey 实现多服务缓存，要基于 NodeKey 分割得到 ServiceKey
            curatorCache.listenable()
                    .addListener(
                            CuratorCacheListener
                                    .builder()
                                    .forDeletes(childData -> registryServiceCache.clear(serviceKey))
                                    .forChanges((oldNode, newNode) -> registryServiceCache.clear(serviceKey))
                                    .build());
        }
    }

    public void setClient(CuratorFramework client) {
        this.client = client;
    }

    public void setWatchServiceKeySet(Set<String> watchServiceKeySet) {
        this.watchServiceKeySet = watchServiceKeySet;
    }

    public void setRegistryServiceCache(RegistryServiceCache registryServiceCache) {
        this.registryServiceCache = registryServiceCache;
    }
}
