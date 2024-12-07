package com.jools.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.jools.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @date 2024/12/7 11:49
 * @description: 递增等待时长策略
 * 初始重试: 3s
 * 递增间隔: 3s
 * 重试停止策略: 尝试次数 4 [重试次数超过 3]
 */
@Slf4j
public class FixIncrementRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        log.debug("Retry strategy:{}", this.getClass().getSimpleName());
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.incrementingWait(3, TimeUnit.SECONDS, 3, TimeUnit.SECONDS))
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
