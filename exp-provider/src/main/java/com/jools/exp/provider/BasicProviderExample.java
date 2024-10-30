package com.jools.exp.provider;

import com.jools.joolsrpc.server.HttpServer;
import com.jools.joolsrpc.server.impl.VertxHttpServer;
import com.jools.joolsrpc.registry.LocalRegistry;

/**
 * @author Jools He
 * @version 1.0
 * @description: 简易服务提供者示例
 */
public class BasicProviderExample {

    public static void main(String[] args) {

        //注册服务
        LocalRegistry.register("UserService", UserServiceImpl.class);

        //提供服务
        HttpServer vertxServer = new VertxHttpServer();
        vertxServer.doStart(8888);
    }
}
