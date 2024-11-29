package com.jools.rpc.server.tcp;

import com.jools.rpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;

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
////            String targetMessage = "Hello, server!! Hello, server!! Hello, server !!";
////            int messageLen = targetMessage.getBytes().length;   //正确的消息体长度
//
//            //基于 RecordParser  实现
//            RecordParser parser = RecordParser.newFixed(8);
//            parser.setOutput(new Handler<Buffer>() {
//
//                //初始化
//                int size = -1;
//                //一次完整的读取(头 + 体)
//                Buffer resultBuffer = Buffer.buffer();
//
//                @Override
//                public void handle(Buffer buffer) {
////                    String str = new String(buffer.getBytes());
////                    System.out.println(str);
////                    if (targetMessage.equals(str)) {
////                        System.out.println("Successful recevie message");
////                    }
//
//                    if (-1 == size) {
//                        //读取消息体的长度
//                        size = buffer.getInt(4);
//                        //设置此轮读取消息体的长度
//                        parser.fixedSizeMode(size);
//                        //写入头信息到结果
//                        resultBuffer.appendBuffer(buffer);
//                    } else {
//                        //写入消息体到结果
//                        resultBuffer.appendBuffer(buffer);
//                        System.out.println(resultBuffer.toString());
//                        //重置一轮
//                        parser.fixedSizeMode(8);
//                        size = -1;
//                        resultBuffer = Buffer.buffer();
//                    }
//                }
//            });
//            //处理连接
//            socket.handler(parser);
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