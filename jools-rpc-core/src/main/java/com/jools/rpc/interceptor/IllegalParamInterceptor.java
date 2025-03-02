package com.jools.rpc.interceptor;

import com.jools.rpc.model.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jools He
 * @description: 非法参数拦截器
 */
@Slf4j
public class IllegalParamInterceptor implements RpcHandlerInterceptor {

    private static final List<String> ILLEGAL_PARAMS = new ArrayList<>() {{
        add("FUVK");
        add("SHIT");
    }};

    @Override
    public boolean preHandle(RpcRequest req) throws Exception {
        log.info("Enter IllegalParamInterceptor preHandle method");
        for (Object param : req.getParams()) {
            if (ILLEGAL_PARAMS.contains(param)) {
                log.error("Illegal param: {}", param);
                return false;
            }
        }
        return true;
    }
}
