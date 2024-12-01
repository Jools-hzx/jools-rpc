package com.jools.rpc.registry;

import com.jools.rpc.spi.SpiLoader;

/**
 * @author Jools He
 * @version 1.0
 */
public class RegistryFactory {

    /**
     * 自动加载 SPI 自定义资源路径下所有注册中心配置
     * 配置格式: key=注册中心实现类全类名
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
