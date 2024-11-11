package com.jools.rpc.registry;

import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.model.ServiceMetaInfo;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
    private static final String ZK_ROOT_PATH = "/rpc/zk";

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
    }

    @Override
    public void heartBeat() {
        //无心跳机制
    }

    @Override
    public void watch(String serviceNodeKey) {
        String key = ZK_ROOT_PATH + "/" + serviceNodeKey;
        boolean newAdd = watchServiceKeySet.add(key);
        if (newAdd) {
            CuratorCache curatorCache = CuratorCache.build(client, key);
            curatorCache.start();
            //基于 ServiceKey 实现多服务缓存，要基于 NodeKey 分割得到 ServiceKey
            curatorCache.listenable()
                    .addListener(
                            CuratorCacheListener
                                    .builder()
                                    .forDeletes(childData -> registryServiceCache
                                            .clear(key.substring(0, key.lastIndexOf("/")))
                                    ).forChanges((oldNode, newNode) -> registryServiceCache
                                            .clear(key.substring(0, key.lastIndexOf("/")))
                                    ).build());
        }
    }

    @Override
    public boolean registry(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        //注册到 ZK
        String serviceNodeKey = serviceMetaInfo.getServiceNodeKey();
        String localRegisterNodeKey = ZK_ROOT_PATH + "/" + serviceNodeKey;
        try {
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
            } catch (Exception e) {
                throw new RuntimeException("Destroy Service Node Key:" + key + e);
            }
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
