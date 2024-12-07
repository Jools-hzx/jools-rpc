package com.jools.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.jools.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/6 19:38
 * @description: TODO
 */
@Slf4j
public class FixIntervalRetryStrategy implements RetryStrategy {

    /**
     * 固定间隔重试
     * 重试间隔: 3s
     * 重试次数: 3
     * 重试监听：输出当前重试的次数
     *
     * @param callable
     * @return
     * @throws Exception
     */
    @SuppressWarnings("all")
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        log.debug("Retry strategy:{}", this.getClass().getSimpleName());
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
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
