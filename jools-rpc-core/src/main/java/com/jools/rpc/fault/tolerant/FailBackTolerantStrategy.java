package com.jools.rpc.fault.tolerant;

import com.jools.rpc.fault.tolerant.queue.FailBackMessageQueue;
import com.jools.rpc.fault.tolerant.queue.FailBackMessageQueueFactory;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/11 10:56
 * @description: 容错 —— 故障恢复(降级 + 限流)
 */

@Slf4j
public class FailBackTolerantStrategy implements ErrorTolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setException(e);
        //恢复启用 Fail Back 机制; 本地伪装服务
        rpcResponse.setMsg(ErrorTolerantKeys.FAIL_BACK);
        rpcResponse.setData(context);
        //消息入队
        FailBackMessageQueueFactory.
                getMessageQueue(ErrorTolerantKeys.FAIL_BACK).
                offer((RpcRequest) context.get("RpcRequest"));
        return rpcResponse;
    }
}
