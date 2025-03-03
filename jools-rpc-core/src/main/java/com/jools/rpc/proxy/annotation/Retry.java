package com.jools.rpc.proxy.annotation;


import com.jools.rpc.fault.retry.RetryStrategyKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 重试注解
 * 用于方法上，标识该方法需要进行重试处理
 * 目前支持的重试策略：
 * 1. noRetry 不进行重试
 * 2. fixedRetry 固定次数重试
 * 3. fixInterval 固定时间间隔重试
 * 4. random 随机时间间隔重试
 * 5. exponent 指数递增重试
 * 6. fixIncrement 固定递增重试
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {


    /**
     * 重试策略; 默认不进行重试
     *
     * @return
     */
    String strategy() default RetryStrategyKeys.noRetry;

}
