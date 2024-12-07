package com.jools.rpc.fault.retry;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/7 9:57
 * @description: TODO
 */
public interface RetryStrategyKeys {

    /**
     * 策略 - 不重试
     */
    String noRetry = "no";

    /**
     * 策略 - 固定间隔重试
     */
    String fixInterval = "fixInterval";

    /**
     * 策略 - 随机间隔重试
     */
    String random = "random";

    /**
     * 策略 - 指数递增
     */
    String exponent = "exponent";

    /**
     * 策略 - 指数递增
     */
    String fixIncrement = "fixIncrement";
}
