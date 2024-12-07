package com.jools.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.jools.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @description: 随机等待间隔重试策略
 * 最小间隔: 4s
 * 最大间隔: 16s
 * 停止策略: 最多重试次数 4 次 [即重试次数大于 3 次]
 *
 */

@Slf4j
public class RandomWaitRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        log.debug("Retry strategy:{}", this.getClass().getSimpleName());
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.randomWait(4, TimeUnit.SECONDS, 16, TimeUnit.SECONDS))
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
