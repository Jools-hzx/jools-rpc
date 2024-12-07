package com.jools.rpc.retry;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/6 12:38
 * @description: TODO
 */
public class MyRetryListener implements RetryListener {

    @Override
    public <V> void onRetry(Attempt<V> attempt) {

        // 第几次重试(注意:第一次重试起始是第一次调用)
        System.out.println("[retry] time = " + attempt.getAttemptNumber());

        // 距离第一次重试的延迟
        System.out.println(", delay = " + attempt.getDelaySinceFirstAttempt());

        // 重试结果
        System.out.println("If has exception:" + attempt.hasException());
        System.out.println("If has result:" + attempt.hasResult());

        // 是什么原因导致的
        if (attempt.hasException()) {
            System.out.println("Exception caused by:" + attempt.getExceptionCause());
        } else {
            // 正常返回时的结果
            System.out.println("Result:" + attempt.getResult());
        }
    }
}
