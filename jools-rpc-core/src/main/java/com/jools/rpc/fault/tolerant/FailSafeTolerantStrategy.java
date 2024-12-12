package com.jools.rpc.fault.tolerant;

import com.jools.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/11 10:54
 * @description: 容错 —— 静默处理
 */
@Slf4j
public class FailSafeTolerantStrategy implements ErrorTolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.error("Service Error !! Use {} as tolerant strategy", this.getClass().getSimpleName());
        return new RpcResponse();
    }
}
