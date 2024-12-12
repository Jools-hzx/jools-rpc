package com.jools.rpc.fault.tolerant;

import com.jools.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 重试机制接口
 */
public interface ErrorTolerantStrategy {

    /**
     * 执行容错策略
     *
     * @param context 上下文数据
     * @param e 异常
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
