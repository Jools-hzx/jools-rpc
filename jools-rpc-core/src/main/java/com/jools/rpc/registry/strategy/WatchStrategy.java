package com.jools.rpc.registry.strategy;

/**
 * 注册中心监听策略
 * @author Jools
 */
public interface WatchStrategy {

    void watch(String serviceNodeKey);
}
