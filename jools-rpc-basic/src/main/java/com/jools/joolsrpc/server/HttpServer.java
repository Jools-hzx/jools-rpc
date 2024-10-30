package com.jools.joolsrpc.server;


/**
 * Http 服务器接口
 */
public interface HttpServer {

    /**
     * 启动服务
     *
     * @param port 启动端口
     */
    void doStart(int port);
}
