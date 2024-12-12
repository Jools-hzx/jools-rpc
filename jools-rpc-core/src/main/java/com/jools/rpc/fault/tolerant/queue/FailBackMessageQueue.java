package com.jools.rpc.fault.tolerant.queue;

import cn.hutool.core.util.ObjectUtil;
import com.jools.rpc.model.RpcRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/12 10:16
 * @description: Fail Back 策略 —— 容错消息队列
 */
public class FailBackMessageQueue implements FailTolerantMessageQueue {

    private static volatile Queue queue;

    private FailBackMessageQueue() {
        synchronized (this) {
            if (queue == null) {
                this.queue = new ArrayBlockingQueue(10);
            }
        }
    }

    public static FailTolerantMessageQueue getInstance() {
        return new FailBackMessageQueue();
    }


    @Override
    public RpcRequest poll() {
        if (!isEmpty()) {
            return (RpcRequest) queue.poll();
        }
        throw new RuntimeException("Fail to poll queue");
    }

    @Override
    public boolean offer(RpcRequest request) {
        if (ObjectUtil.isNotNull(request)) {
            return queue.offer(request);
        }
        throw new RuntimeException("Enqueue element can not be null");
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public List<RpcRequest> listAll() {
        if (isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return queue.stream().toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
