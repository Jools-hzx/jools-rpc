package com.jools.rpc.fault.retry;

import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.registry.RegistryFactory;
import com.jools.rpc.spi.SpiLoader;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class RetryStrategyFactoryTest {

    private RetryStrategy retryStrategy = null;

    @Before
    public void setUp() throws Exception {
        Map<String, Class<?>> loaded = SpiLoader.load(RetryStrategy.class);
        for (String k : loaded.keySet()) {
            System.out.println("Spi config - {key:" + k + "\t" + "val:" + loaded.get(k) + "}");
        }
    }

    @Test
    public void getExponentRetryStrategy() throws Exception {

        retryStrategy = RetryStrategyFactory.getRetryStrategy(RetryStrategyKeys.exponent);

        //计数器: 控制重试次数
        AtomicInteger atomicInteger = new AtomicInteger(0);

        Callable<RpcResponse> callable = () -> {
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setMsg("Test Exponential Retry");
            rpcResponse.setException(new RuntimeException());
            atomicInteger.incrementAndGet();
            if (atomicInteger.get() == 4) {     //第三次重试的时候返回成功
                return rpcResponse;
            }
            throw new Exception();
        };

        System.out.println("------- Retry Succeed -------");
        RpcResponse rpcResponse = retryStrategy.doRetry(callable);
        System.out.println(rpcResponse);
    }

    @Test
    public void getRandomRetryStrategy() throws Exception {

        retryStrategy = RetryStrategyFactory.getRetryStrategy(RetryStrategyKeys.random);

        //计数器: 控制重试次数
        AtomicInteger atomicInteger = new AtomicInteger(0);

        Callable<RpcResponse> callable = () -> {
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setMsg("Test Random Retry");
            rpcResponse.setException(new RuntimeException());
            atomicInteger.incrementAndGet();
            if (atomicInteger.get() == 4) {     //第三次重试的时候返回成功
                return rpcResponse;
            }
            throw new RuntimeException("test");
        };

        System.out.println("------- Retry Succeed -------");
        RpcResponse rpcResponse = retryStrategy.doRetry(callable);
        System.out.println(rpcResponse);
    }

    @Test
    public void getFixIncrementRetryStrategy() throws Exception {

        retryStrategy = RetryStrategyFactory.getRetryStrategy(RetryStrategyKeys.fixIncrement);

        //计数器: 控制重试次数
        AtomicInteger atomicInteger = new AtomicInteger(0);

        Callable<RpcResponse> callable = () -> {
            System.out.println("Test Fix Increment Retry Strategy");
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setMsg("Test Fix Increment Retry");
            rpcResponse.setException(new RuntimeException());
            atomicInteger.incrementAndGet();
            if (atomicInteger.get() == 4) {     //第三次重试的时候返回成功
                return rpcResponse;
            }
            throw new RuntimeException("test");
        };

        System.out.println("------- Retry Succeed -------");
        RpcResponse rpcResponse = retryStrategy.doRetry(callable);
        System.out.println(rpcResponse);
    }


    @Test
    public void getFixIntervalRetryStrategy() throws Exception {

        retryStrategy = RetryStrategyFactory.getRetryStrategy(RetryStrategyKeys.fixInterval);

        //计数器: 控制重试次数
        AtomicInteger atomicInteger = new AtomicInteger(0);

        Callable<RpcResponse> callable = () -> {
            System.out.println("Test Fix Interval Retry Strategy");
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setMsg("Test Fix Interval Retry");
            rpcResponse.setException(new RuntimeException());
            atomicInteger.incrementAndGet();
            if (atomicInteger.get() == 3) {     //第三次重试的时候返回成功
                return rpcResponse;
            }
            throw new RuntimeException("test");
        };

        System.out.println("------- Retry Succeed -------");
        RpcResponse rpcResponse = retryStrategy.doRetry(callable);
        System.out.println(rpcResponse);
    }

    @Test
    public void getNoRetryStrategy() throws Exception {

        retryStrategy = RetryStrategyFactory.getRetryStrategy(RetryStrategyKeys.noRetry);

        //计数器: 控制重试次数
        AtomicInteger atomicInteger = new AtomicInteger(0);

        Callable<RpcResponse> callable = () -> {
            System.out.println("Test No Retry Strategy");
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setMsg("Test No Retry");
            rpcResponse.setException(new RuntimeException());
            atomicInteger.incrementAndGet();
            if (atomicInteger.get() == 1) {     //第三次重试的时候返回成功
                return rpcResponse;
            }
            throw new RuntimeException("test");
        };

        System.out.println("------- Retry Succeed -------");
        RpcResponse rpcResponse = retryStrategy.doRetry(callable);
        System.out.println(rpcResponse);
    }
}