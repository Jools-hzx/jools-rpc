package com.jools.rpc.fault.tolerant.queue;

import com.jools.rpc.model.RpcRequest;

import java.util.List;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/12 10:12
 * @description: Fail Back 机制消息队列
 */
public interface FailTolerantMessageQueue {

    /**
     * 取消息
     *
     * @return
     */
    RpcRequest poll();

    /**
     * 推送消息
     *
     * @param request
     * @return
     */
    boolean offer(RpcRequest request);

    /**
     * 队列为空
     *
     * @return
     */
    boolean isEmpty();

    /**
     * 获取所有消息
     *
     * @return
     */
    List<RpcRequest> listAll();
}
