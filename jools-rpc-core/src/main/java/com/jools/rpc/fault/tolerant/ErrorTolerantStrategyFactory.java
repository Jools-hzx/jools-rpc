package com.jools.rpc.fault.tolerant;

import com.jools.rpc.spi.SpiLoader;

/**
 * @author Jools He
 * @version 1.0
 * @description: 使用工厂模式加载容错机制
 */
public class ErrorTolerantStrategyFactory {

    /**
     * 自动加载 SPI 自定义资源路径下的容错机制配置
     */
    static {
        SpiLoader.load(ErrorTolerantStrategy.class);
    }


    public static final ErrorTolerantStrategy DEFAULT_ERROR_TOLERANT_STRATEGY = new FailFastTolerantStrategy();


    /**
     * 基于 key 匹配 SPI 机制内配置的 key -> ImplInstance 映射
     *
     * @param key 匹配的键值
     */
    public static ErrorTolerantStrategy getTolerantStrategy(String key) {
        return SpiLoader.getInstance(ErrorTolerantStrategy.class, key);
    }
}
