package com.jools.rpc.retry;

import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.jools.rpc.registry.Registry;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/6 11:00
 * @description: TODO
 */
public class TestRetryApi {

    boolean result = false;
    AtomicInteger atomicInteger = new AtomicInteger(0);
    int sleepNum = 10000;


    public static void main(String[] args) {

    }
}
