package com.jools.rpc.registry;

import com.jools.rpc.spi.SpiLoader;

/**
 * @author Jools He
 * @version 1.0
 */
public class RegistryFactory {

    /**
     * 自动加载所有 Registry 类配置的 key -> 实现类全类名映射
     */
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心配置 - ETCD
     */
    public static Registry defaultRegistry = new EtcdRegistry();

    /**
     * 基于 key 获取 Registry 实例
     */
    public static Registry getRegistry(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }
}
