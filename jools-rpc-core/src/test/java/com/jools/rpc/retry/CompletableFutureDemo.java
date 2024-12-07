package com.jools.rpc.retry;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/6 19:11
 * @description: TODO
 */
public class CompletableFutureDemo {

    public static void main(String[] args) {
        //1. 创建一个异步任务，给定返回值
        CompletableFuture<String> c = CompletableFuture.completedFuture("HAHAH");
        c.thenApply(result -> {
            System.out.println("上一个任务的结果为:" + result);
            return result + "... 熊猫";
        });
        c.thenAccept(System.out::println);

        //2. 创建一个没有返回值的异步任务
        CompletableFuture<Void> noReturnCF = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " 没有返回值的异步任务");
        });

        //3. 创建一个有返回值的异步任务并且指定执行的线程池
        //创建单例的线程池
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        CompletableFuture<String> supplySingleAsyncCF = CompletableFuture.supplyAsync(CompletableFutureDemo::oddNumbersSum, executor);
//        // 执行过程中出现了异常的回调
//        supplySingleAsyncCF.thenAccept(System.out::println);
//
//        //执行过程中出现异常的回调问题
//        supplySingleAsyncCF.exceptionally(
//                (e) -> {
//                    e.printStackTrace();
//                    return "异步任务执行中出现异常....";
//                }
//        );
//        //关闭线程池
//        executor.shutdown();

        /*
         输出:
            上一个任务的结果为:
            HAHAH
            HAHAH
            ForkJoinPool.commonPool-worker-1 没有返回值的异步任务
            pool-1-thread-1线程 - 100内奇数之和: 2500
         */
    }

    @Test
    public void testAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(CompletableFutureDemo::evenNumbersSum);
        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(CompletableFutureDemo::oddNumbersSum);

        //防止 main 线程死亡
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        /*
         输出结果:
         ForkJoinPool.commonPool-worker-2线程...执行了求和奇数....
         ForkJoinPool.commonPool-worker-1线程...执行了求和偶数....
         */


        //测试 AND 类型汇聚方法
        CompletableFuture<Integer> cfThenCombine = cf1.thenCombine(cf2, (r1, r2) -> {
            System.out.println("cf1任务计算结果：" + r1);
            System.out.println("cf2任务计算结果：" + r2);
            return r1 + r2;
        });
        System.out.println("cf1, cf2 任务 ThenCombine 汇聚处理结果:" + cfThenCombine.get());

        /*
         输出:
            cf1 任务计算结果: 2550
            cf2 任务计算结果: 2500
            cf1, cf2 任务 ThenCombine 汇聚处理结果:5050
         */


        //使用 allOf 汇聚两个任务(可以汇聚多个)
        CompletableFuture<Void> cfAllOf = CompletableFuture.allOf(cf1, cf2);
        //配合 thenAccept 成功回调函数使用
        cfAllOf.thenAccept(o -> System.out.println("所有任务完成后进行回调"));


        /*--------------------测试OR类型汇聚方法------------------*/
        CompletableFuture<Integer> cfApplyToEither = cf1.applyToEither(cf2, r -> {
            System.out.println("最先执行完成的任务结果:" + r);
            return r * 10;
        });
        System.out.println("cf1,cf2 任务 applyToEither 汇聚处理结果:" + cfApplyToEither.get());

        /*
         输出:
            所有任务完成后进行回调
            最先执行完成的任务结果：2550
            cf1,cf2任务applyToEither汇聚处理结果：25500
         */

        // 使用anyOf汇聚两个任务，谁先执行完成就处理谁的执行结果
        CompletableFuture cfAnyOf = CompletableFuture.anyOf(cf1, cf2);
        // 配合thenAccept成功回调函数使用
        cfAnyOf.thenAccept(r -> {
            System.out.println("最先执行完成的任务结果：" + r);
            System.out.println("对先完成的任务结果进行后续处理....");
        });

        /*
         输出:
         最先执行完成的任务结果：2550
         对先完成的任务结果进行后续处理....
         */

    }

    // 求和100内的奇数
    private static int oddNumbersSum() {
        int sum = 0;
        System.out.println(Thread.currentThread().getName()
                + "线程...执行了求和奇数....");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 1; i <= 100; i++) {
            if (i % 2 != 0) sum += i;
        }
        return sum;
    }

    // 求和100内的偶数
    private static int evenNumbersSum() {
        int sum = 0;
        System.out.println(Thread.currentThread().getName()
                + "线程...执行了求和偶数....");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 1; i <= 100; i++) {
            if (i % 2 == 0) sum += i;
        }
        return sum;
    }

}
