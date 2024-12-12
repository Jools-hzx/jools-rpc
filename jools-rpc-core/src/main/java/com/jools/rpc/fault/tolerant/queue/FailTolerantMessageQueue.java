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

    RpcRequest poll();

    boolean offer(RpcRequest request);

    boolean isEmpty();

    List<RpcRequest> listAll();
}
