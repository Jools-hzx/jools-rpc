package com.jools.rpc.fault.retry;

import com.jools.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/6 15:17
 * @description: 重试策略
 * 支持
 * 1. 不重试
 * 2. 固定时间间隔
 * 3. 固定增长时间间隔
 * 4. 指数增长间隔时间
 * 5. 随机时长间隔
 */
public interface RetryStrategy {

    /**
     * 执行重试策略
     *
     * @param callable
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
