package com.jools.joolsrpc.server.impl;

import com.jools.joolsrpc.server.HttpServer;
import com.jools.joolsrpc.server.HttpServerHandler;
import io.vertx.core.Vertx;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/29 23:31
 * @description: 基于 Vert.x 实现的 web 服务器 VertxHttpServer
 */
public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        //创建一个 Vertx 实例
        Vertx vertx = Vertx.vertx();

        //创建 HTTP 服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        //监听端口并请求
        /*
         * Set the request handler for the server to {@code requestHandler}.
         * As HTTP requests are received by the server,
         * instances of {@link HttpServerRequest} will be created and passed to this handler.
         * @return a reference to this, so the API can be used fluently
         *
            @Fluent
            io.vertx.core.http.HttpServer requestHandler(Handler< HttpServerRequest > handler);
         */
        server.requestHandler(new HttpServerHandler());

        //启动 Http 服务器并且监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port: " + port);
            } else {
                System.out.println("Failed to start server:" + result.cause());
            }
        });
    }
}
