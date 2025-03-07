package com.jools.rpc.registry;

import com.jools.rpc.RpcApplication;
import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.config.RpcConfig;
import com.jools.rpc.constant.RpcConstant;
import com.jools.rpc.model.ServiceMetaInfo;
import com.jools.rpc.registry.strategy.EtcdWatchStrategy;
import com.jools.rpc.registry.strategy.WatchStrategy;
import com.jools.rpc.utils.DateUtils;

import javax.crypto.DecapsulateException;
import java.time.LocalDateTime;
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
     * 心跳检测
     */
    void heartBeat();

    /**
     * 监听服务
     */
    void watch(String serviceKey);

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


    /**
     * 设置服务注册时间
     *
     * @param serviceMetaInfo
     */
    default void setRegistryTimeDate(ServiceMetaInfo serviceMetaInfo) {
        serviceMetaInfo.setRegisterTime(DateUtils.formatLocalTimeDate(LocalDateTime.now()));
    }
}
