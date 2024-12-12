package com.jools.rpc.fault.tolerant;

import cn.hutool.core.util.ObjectUtil;
import com.jools.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/11 10:51
 * @description: 容错 —— 快速失败
 */
@Slf4j
public class FailFastTolerantStrategy implements ErrorTolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        if (!ObjectUtil.isNull(context) && !context.isEmpty()) {
            log.error("Fail Fast, context:{}", context);
        }
        throw new RuntimeException("Service Error!!", e);
    }
}
