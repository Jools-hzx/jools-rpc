package com.jools.exp.common.service;

import com.jools.joolsrpc.model.RpcRequest;
import com.jools.joolsrpc.model.RpcResponse;
import com.jools.joolsrpc.server.HttpServer;
import com.jools.rpc.core.config.RpcConfig;

/**
 * 测试 Mock 服务
 *
 * @author Jools He
 */
public interface TestMockService {

    /**
     * 新方法 - 默认返回 1
     * @return 默认返回 1 (short 类型)
     */
    default RpcRequest getRpcRequest() {
        return null;
    }

    /**
     * 新方法 - 默认返回 1
     * @return 默认返回 1 (short 类型)
     */
    default RpcResponse getRpcResponse() {
        return null;
    }

    /**
     * 新方法 - 默认返回 1
     * @return 默认返回 1 (short 类型)
     */
    default RpcConfig getRpcConfig() {
        return null;
    }

    /**
     * 新方法 - 默认返回 1
     * @return 默认返回 1 (short 类型)
     */
    default HttpServer getHttpServer() {
        return null;
    }
}
