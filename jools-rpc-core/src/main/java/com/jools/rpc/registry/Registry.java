package com.jools.rpc.registry;

import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Jools He
 */
public interface Registry {

    /**
     * 初始化注册中心服务
     *
     * @param registryConfig 注册中心配置
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务
     *
     * @param serviceMetaInfo 注册服务信息
     * @return 注册成功返回 true
     */
    boolean registry(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException;

    /**
     * 下线服务
     *
     * @param serviceMetaInfo 注册服务信息
     * @return 下线成功返回 true
     */
    boolean unRegistry(ServiceMetaInfo serviceMetaInfo);

    /**
     * 列举所有服务
     *
     * @param serviceKey 服务键值
     * @return 该键值下的所有服务
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 销毁服务
     */
    void destory();
}
