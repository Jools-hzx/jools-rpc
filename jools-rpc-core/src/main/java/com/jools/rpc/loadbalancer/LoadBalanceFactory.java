package com.jools.rpc.loadbalancer;

import com.jools.rpc.spi.SpiLoader;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/30 20:15
 * @description: 负载均衡工厂 - 基于 SPI 机制配合配置key动态加载
 */
public class LoadBalanceFactory {

    /**
     * 自动加载 SPI 自定义资源路径下所有配置的负载均衡器
     * 配置格式: key=负载均衡器实现类全类名
     */
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 默认负载均衡器 - 轮询
     */
    public static LoadBalancer defaultLoadBalancer = new RoundRobinLoadBalancer();

    /**
     * 基于配置 loadBalancer key 获取相应的实现类
     *
     * @param key 负载均衡 key
     * @return
     */
    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }

}
