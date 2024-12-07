package com.jools.rpc.fault.retry;

import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.utils.DateUtils;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class retryStrategyTest {

    RetryStrategy noRetryStrategy = new NoRetryStrategy();
    RetryStrategy fixIntervalRetryStrategy = new FixIntervalRetryStrategy();

    @Test
    public void testRetry() throws Exception {

        this.noRetryStrategy.doRetry(() -> {
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setMsg("Test no retry strategy");
            throw new RuntimeException();
        });

        /*
        输出:
        19:53:52.247 [main] DEBUG com.jools.rpc.fault.retry.NoRetryStrategy -- Retry strategy:NoRetryStrategy
        java.lang.RuntimeException
         */
    }

    @Test
    public void testRetryWithFixInterval() throws Exception {
        this.fixIntervalRetryStrategy.doRetry(() -> {
            System.out.println("Testing fix interval retry strategy....");
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setMsg("Test fix interval retry strategy");
            System.out.println(DateUtils.formatLocalTimeDate(LocalDateTime.now()));
            throw new RuntimeException();
        });


        /*
        输出:
        19:52:14.719 [main] DEBUG com.jools.rpc.fault.retry.FixIntervalRetryStrategy -- Retry strategy:FixIntervalRetryStrategy
        2024-12-06 19:52:14
        19:52:14.729 [main] INFO com.jools.rpc.fault.retry.FixIntervalRetryStrategy -- 重试次数:1
        2024-12-06 19:52:17
        19:52:17.739 [main] INFO com.jools.rpc.fault.retry.FixIntervalRetryStrategy -- 重试次数:2
        2024-12-06 19:52:20
        19:52:20.751 [main] INFO com.jools.rpc.fault.retry.FixIntervalRetryStrategy -- 重试次数:3

        com.github.rholder.retry.RetryException: Retrying failed to complete successfully after 3 attempts.
        */
    }

}