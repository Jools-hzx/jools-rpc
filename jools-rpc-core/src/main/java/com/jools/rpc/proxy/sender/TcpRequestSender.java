package com.jools.rpc.proxy.sender;

import cn.hutool.core.util.IdUtil;
import com.jools.rpc.RpcApplication;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/28 11:15
 * @description: 请求协议 - TCP
 * RpcRequest 携带自定义消息头被封装为 ProtocolMessage 编码成 Buffer 发送;
 * ProtocolMessage 响应解码得到请求体内的 RpcResponse
 */
@Slf4j
public class TcpRequestSender implements RequestSender {
    @Override
    public RpcResponse convertAndSend(String serviceAddr, RpcRequest rpcRequest) throws ExecutionException, InterruptedException {
        //接受 RpcResponse - 异步转同步
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        RpcResponse rpcResponse = null;

        //解析 ip  形参 serviceAddr 的格式: http://ip:port
        String ip = serviceAddr.substring(serviceAddr.lastIndexOf("/") + 1, serviceAddr.lastIndexOf(":"));
        //解析端口
        String port = serviceAddr.substring(serviceAddr.lastIndexOf(":") + 1);

        //基于 TCP 发送请求 + 配合自定义协议格式
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        try {
            netClient.connect(Integer.parseInt(port), ip, result -> {
                if (result.succeeded()) {
                    log.info("TCP request succeed !!!");
                    //构造 ProtocolMessage
                    NetSocket socket = result.result();
                    //构造 ProtocolMessage 请求体为 RpcRequest
                    ProtocolMessage<RpcRequest> requestProtocolMessage = new ProtocolMessage<>();
                    //构造请求头
                    ProtocolMessage.Header header = requestProtocolMessage.getHeader();
                    header.setMagic(ProtocolConstant.MAGIC);    //魔数
                    header.setVersion(ProtocolConstant.VERSION);    //版本
                    header.setMessageType(ProtocolMessageTypeEnum.REQUEST.getMessageType());    //消息类型
                    header.setSerializerType(       // 序列协议
                            Objects.requireNonNull(
                                    ProtocolSerializerTypeEnum.getSerializerTypeByKey(
                                            RpcApplication.getRpcConfig().getSerializer())).getType());
                    header.setMessageId(IdUtil.getSnowflakeNextId());   //消息唯一标识 id
                    requestProtocolMessage.setBody(rpcRequest);    //以 RpcRequest 作为消息体
                    requestProtocolMessage.setHeader(header);      //携带消息头

                    Buffer buf;
                    try {
                        //编码后发送
                        buf = ProtocolMessageEncoder.encode(requestProtocolMessage);
                        socket.write(buf);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    //接受响应
                    socket.handler(buffer -> {
                        //解码后得到响应
                        try {
                            //基于响应的 buffer 反编码
                            ProtocolMessage<RpcResponse> responseProtocolMessage =
                                    (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                            responseFuture.complete(responseProtocolMessage.getBody());
                        } catch (IOException e) {
                            log.error(e.getMessage());
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    log.error("Fail to build a tcp connection");
                }
            });

            //阻塞直到获取到响应
            rpcResponse = responseFuture.get();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        //关闭连接
        netClient.close();
        return rpcResponse;
    }
}
