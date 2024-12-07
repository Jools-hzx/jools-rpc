package com.jools.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.jools.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @date 2024/12/7 11:49
 * @description: 指数等待重试策略
 * 起始重试时间间隔: 1s
 * 指数间隔: 2 的幂次方
 * 最大间隔: 16s
 * 停止策略: 尝试总数为 4 次; 即重试次数超过 3 次
 */
@Slf4j
public class ExponentialWaitRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        log.debug("Retry strategy:{}", this.getClass().getSimpleName());
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.exponentialWait(1000, 16, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(4))
                .withRetryListener(
                        new RetryListener() {
                            @Override
                            public <V> void onRetry(Attempt<V> attempt) {
                                //忽略第一次尝试的日志
                                if (attempt.getAttemptNumber() > 1) {
                                    log.info("重试次数:{}", attempt.getAttemptNumber() - 1);
                                }
                            }
                        }
                ).build();
        return retryer.call(callable);
    }
}
