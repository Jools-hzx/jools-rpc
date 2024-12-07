package com.jools.rpc.fault.retry;

import com.jools.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/6 15:17
 * @description: TODO
 */
public interface RetryStrategy {

    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
