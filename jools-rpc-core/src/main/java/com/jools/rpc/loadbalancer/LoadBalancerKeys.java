package com.jools.rpc.loadbalancer;

/**
 * 负载均衡规则 Key
 * 支持算法:
 * 1. 轮询
 * 2. 随机
 * 3. 一致性 Hash
 */
public interface LoadBalancerKeys {

    /**
     * 负载规则 - 轮询
     */
    String ROUND_ROBIN = "round";

    /**
     * 负载规则 - 随机
     */
    String RANDOM = "random";

    /**
     * 负载规则 - 一致性 Hash
     */
    String CONSISTENT_HASH = "hash";

    /**
     * 负载规则 - 加权轮询 [优先选择权重高]
     */
    String WEIGHT = "weight";
}
