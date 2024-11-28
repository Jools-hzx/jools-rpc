package com.jools.rpc.server.tcp;

import com.jools.rpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

/**
 * @version 1.0
 * @description: 基于 Vert.x 的 TCP 服务器
 */
public class VertxTcpServer implements HttpServer {

    //处理请求
    private byte[] doRequest(byte[] reqData) {
        //处理请求的逻辑，根据 requestData 构造响应数据并且返回
        return "Hello, Client!".getBytes();
    }


    @Override
    public void doStart(int port) {

        //通过 Vert.x 获取到 TCP 实例
        NetServer server = Vertx.vertx().createNetServer();

        // 处理请求 - 测试返回 "Hello, Client!"
//        server.connectHandler(socket -> {
//            //处理连接
//            socket.handler(buffer -> {
//                //处理接收到的字节数组
//                byte[] requestData = buffer.getBytes();
//                //在这里进行自定义的字节数组处理逻辑，如：解析请求、调用服务、构造
//                byte[] responseData = doRequest(requestData);
//                //发送响应
//                socket.write(Buffer.buffer(responseData));
//            });
//        });

        //重要 接入 TcpServerHandler Tcp请求处理器 !!!
        server.connectHandler(new TcpServerHandler());

        //启动 TCP 服务器并且监听端口
        server.listen(port, connected -> {
            if (connected.succeeded()) {
                System.out.println("TCP server started on port:" + port);
            } else {
                System.out.println("Failed to start TCP server:" + connected.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}