package com.jools.rpc.fault.retry;

import com.jools.rpc.spi.SpiLoader;

/**
 * @author Jools He
 * @version 1.0
 * @description: 重试策略工厂
 */
public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认策略
     */
    public static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    /**
     * 基于 key 获取策略实例
     */
    public static RetryStrategy getRetryStrategy(String retryStrategyKey) {
        return SpiLoader.getInstance(RetryStrategy.class, retryStrategyKey);
    }
}
