package com.jools.rpc.proxy.annotation;

import com.jools.rpc.loadbalancer.LoadBalancerKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jools He
 * @description: 负载均衡注解
 * 用于方法上，标识该方法需要进行负载均衡处理
 * 目前支持的负载均衡策略：
 * 1. 轮询 Round Robin
 * 2. 随机 Random
 * 3. 一致性Hash CONSISTENT_HASH
 * 4. 加权轮询 WEIGHT
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoadBalance {

    /**
     * 负载均衡策略; 默认轮询·
     */
    String strategy() default LoadBalancerKeys.ROUND_ROBIN;
}
