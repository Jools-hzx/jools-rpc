package com.jools.rpc.interceptor;

import com.jools.rpc.model.RpcRequest;

/**
 * JRpc 框架的拦截器接口
 */
public interface RpcHandlerInterceptor {

    /**
     * 前置处理
     *
     * @param req RpcRequest 请求
     */
    default boolean preHandle(RpcRequest req) throws Exception {
        return true;
    }

    /**
     *    后置处理
     * @param req RpcRequest 请求
     * @throws Exception
     */
    default void afterCompletion(RpcRequest req) throws Exception {
    }
}
