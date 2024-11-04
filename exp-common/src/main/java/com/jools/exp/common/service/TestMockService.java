package com.jools.exp.common.service;


import com.jools.rpc.config.RpcConfig;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.server.HttpServer;

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
