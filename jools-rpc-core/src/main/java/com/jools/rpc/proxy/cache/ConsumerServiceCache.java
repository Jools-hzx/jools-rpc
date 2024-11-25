package com.jools.rpc.proxy.cache;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jools.rpc.model.ServiceMetaInfo;
import com.jools.rpc.model.registryInfo.Protocol;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @description: 消费者服务缓存
 */
@Slf4j
public class ConsumerServiceCache {

    /**
     * 默认服务信息 - 兜底
     */
    private static final List<ServiceMetaInfo> DEFAULT_SERVICES_LIST = new ArrayList<>();

    static {
        //TODO - Implement real default serviceMetaInfo
        ServiceMetaInfo defaultServiceInfo = new ServiceMetaInfo();
        defaultServiceInfo.setProtocol(Protocol.HTTP);
        defaultServiceInfo.setServiceName("default");
        defaultServiceInfo.setServiceVersion("0.0");
        defaultServiceInfo.setServicePort(8080);
        DEFAULT_SERVICES_LIST.add(defaultServiceInfo);
    }

    /**
     * 本地服务缓存，支持基于(serviceKey)多服务注册
     */
    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 写缓存
     *
     * @param serviceKey
     * @param list
     */
    public void writeCache(String serviceKey, List<ServiceMetaInfo> list) {
        if (StrUtil.isBlank(serviceKey)) {
            throw new RuntimeException("Registry Service Key must not be Empty");
        }
        serviceCache.put(serviceKey, list);
    }

    /**
     * 读缓存 - 基于 serviceKey
     *
     * @param serviceKey 服务键名 (serviceName:serviceVersion)
     */
    public List<ServiceMetaInfo> readCache(String serviceKey) {
        //判断是否非空
        if (ObjectUtil.isEmpty(serviceCache) || ObjectUtil.isEmpty(serviceCache.get(serviceKey))) {
            log.info("No service infos found in current cache:{}, serviceKey:{}",
                    this.getClass().getSimpleName(),
                    serviceKey);
            return DEFAULT_SERVICES_LIST;
        }
        try {
            return serviceCache.get(serviceKey);
        } catch (Exception e) {
            throw new RuntimeException("Fail to read from Cache, serviceKey:{}" + serviceKey);
        }
    }

    /**
     * 清空基于 serviceKey 的缓存
     */
    public void clear(String serviceKey) {
        try {
            //防止空缓存
            if (!this.serviceCache.containsKey(serviceKey)) {
                return;
            }
            List<ServiceMetaInfo> serviceMetaInfos = this.serviceCache.get(serviceKey);
            serviceMetaInfos.clear();
            log.info("ServiceKey:{} local registered services list is clear", serviceKey);
        } catch (Exception e) {
            log.error("Fail to clear local registered services list ServiceKey:{} ", serviceKey);
            throw new RuntimeException(e);
        }
    }
}
