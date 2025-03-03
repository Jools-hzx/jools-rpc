package com.jools.rpc.registry;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jools.rpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/10 12:19
 * @description: 服务缓存类 - 支持基于 ServiceKey(serviceName:version) 缓存已经发现的服务节点
 */
@Slf4j
public class RegistryServiceCache {

    /**
     * 本地服务缓存，支持基于(serviceKey)多服务注册
     */
    Cache<String, List<ServiceMetaInfo>> serviceCache = Caffeine.newBuilder().
            expireAfterAccess(1, TimeUnit.MINUTES).
            maximumSize(100).
            build();

    /**
     * 写缓存
     *
     * @param serviceKey
     * @param list
     */
    void writeCache(String serviceKey, List<ServiceMetaInfo> list) {
        if (StrUtil.isBlank(serviceKey)) {
            throw new RuntimeException("Registry Service Key can not be Empty");
        }
        serviceCache.put(serviceKey, list);
    }

    /**
     * 读缓存 - 基于 serviceKey
     *
     * @param serviceKey 服务键名 (serviceName:serviceVersion)
     */
    List<ServiceMetaInfo> readCache(String serviceKey) {
        if (this.serviceCache.estimatedSize() == 0l) {
            throw new RuntimeException("Current local service cache `RegistryServiceCache` is Empty!");
        }
        try {
            return serviceCache.getIfPresent(serviceKey);
        } catch (Exception e) {
            throw new RuntimeException("Not register services contains service key:" + serviceKey);
        }
    }

    /**
     * 清空基于 serviceKey 的缓存
     *
     * @param serviceKey
     */
    public void clear(String serviceKey) {
        try {
            //防止空缓存
            if (!(this.serviceCache.estimatedSize() == 0l)) {
                return;
            }
            List<ServiceMetaInfo> serviceMetaInfos = this.serviceCache.getIfPresent(serviceKey);
            serviceMetaInfos.clear();
            log.info("ServiceKey:{} local registered services list is clear", serviceKey);
        } catch (Exception e) {
            log.error("Fail to clear local registered services list ServiceKey:{} ", serviceKey);
            throw new RuntimeException(e);
        }
    }
}
