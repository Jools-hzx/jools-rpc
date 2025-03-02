package com.jools.rpc.interceptor;

import com.esotericsoftware.minlog.Log;
import com.jools.rpc.model.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jools He
 * @version 1.0
 */
@Slf4j
public class InvalidServiceNameInterceptor implements RpcHandlerInterceptor {

    private static final List<String> INVALID_SERVICE_NAME = new ArrayList<>() {{
        add("serviceName01");
        add("serviceName01");
    }};

    @Override
    public boolean preHandle(RpcRequest req) throws Exception {
        Log.info("Enter InvalidServiceNameInterceptor preHandle method");
        if (INVALID_SERVICE_NAME.contains(req.getServiceName())) {
            log.error("Invalid service name: {}", req.getServiceName());
            return false;
        }
        return true;
    }
}
