package com.jools.rpc.fault.retry;

import com.jools.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @author Jools He
 * @version 1.0
 * @description: 不重试策略，直接执行一次任务后返回
 */
@Slf4j
public class NoRetryStrategy implements RetryStrategy {

    /**
     * 重试策略 - 仅执行一次
     *
     * @param callable
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        log.debug("Retry strategy:{}", this.getClass().getSimpleName());
        return callable.call();
    }
}
