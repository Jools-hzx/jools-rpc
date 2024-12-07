package com.jools.rpc.retry;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/6 15:19
 * @description: TODO
 */
public class CallableTest {

    //创建异步任务
    @Test
    public void testAsync() throws InterruptedException {
        CompletableFuture<String> supplyCF = CompletableFuture.supplyAsync(CallableTest::evenNumberSum);
        //执行成功回调
        supplyCF.thenAccept(System.out::println);
        //执行过程中出现异常回调
        supplyCF.exceptionally(e -> {
            e.printStackTrace();
            return "异步任务执行过程中出现异常";
        });

        for (int i = 1; i < 10; i++) {
            System.out.println("main线程 - 输出:" + i);
            Thread.sleep(50);
        }

        /*
         输出结果:
            main线程 - 输出:1
            main线程 - 输出:2
            ForkJoinPool.commonPool-worker-1线程 - 100 以内的偶数和为:2550
            main线程 - 输出:3
            main线程 - 输出:4
            main线程 - 输出:5
            main线程 - 输出:6
            main线程 - 输出:7
            main线程 - 输出:8
            main线程 - 输出:9
         */
    }

    private static String evenNumberSum() {
        int sum = 0;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i <= 100; i++) {
            if (i % 2 == 0) sum += i;
        }
        return Thread.currentThread().getName() + "线程 - 100 以内的偶数和为:" + sum;
    }

    //FutureTask 类
    @Test
    public void testFutureTask() throws ExecutionException, InterruptedException {
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();

        new Thread(() -> {
            System.out.println("异步任务......");
            completableFuture.complete(Thread.currentThread().getName());
        }).start();

        //主线程获取异步任务执行结果
        System.out.println("main 线程获取执行结果:" + completableFuture.get());

        /*
         输出:
            异步任务......
            main 线程获取执行结果:Thread-0
         */
    }

    @Test
    public void testThreadRunnable() {

        Runnable taskRunnable = () -> System.out.println("Test Runnable interface");

        Thread t1 = new Thread(taskRunnable);
        Thread t2 = new Thread(taskRunnable);
        t1.start();
        t2.start();
    }
}
