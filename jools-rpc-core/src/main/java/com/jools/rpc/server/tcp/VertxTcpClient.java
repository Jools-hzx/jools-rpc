package com.jools.rpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/27 10:08
 * @description: 测试用 - TCP 客户端
 */
@Slf4j
public class VertxTcpClient {

    public void start() {
        //基于 Vert.x 实例获取 TCP Client
        NetClient client = Vertx.vertx().createNetClient();

        client.connect(8888, "127.0.0.1", connected -> {
            if (connected.succeeded()) {
                System.out.println("Connected to TCP server");
                NetSocket socket = connected.result();

                for (int i = 0; i < 1000; i++) {
                    //发送数据
                    Buffer buffer = Buffer.buffer();
                    String str = "Hello, server! Hello, server ! Hello, server !";
                    buffer.appendInt(0);
                    buffer.appendInt(str.getBytes().length);
                    buffer.appendBytes(str.getBytes());
                    socket.write(buffer);
                }
                //发送数据
                //接收响应
                socket.handler(buffer -> {
                    //TODO: add handle response logic
                    System.out.println("Receive response from server: " + buffer.toString());
                });
            } else {
                log.error("Failed to connect to TCP server");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
