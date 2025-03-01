package com.jools.rpc.proxy.sender;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jools.rpc.RpcApplication;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerFactory;
import com.jools.rpc.serializer.impl.JdkSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Jools He
 * @version 1.0
 * @description: 请求协议 - HTTP
 * RpcRequest 基于配置指定的序列化器序列化为字节数组
 * 接收到请求后基于配置指定的序列化器反序列化为 RpcResponse
 */
@Slf4j
public class HttpRequestSender implements RequestSender {

    private static final int RPC_RESP_WAIT_DURATION = RpcApplication.getRpcConfig().getRpcRespWaitDuration();

    @Override
    public RpcResponse convertAndSend(String serviceAddr, RpcRequest request) throws IOException {
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        byte[] bytes = serializer.serialize(request);
        try (HttpResponse httpResponse = HttpRequest
                .post(serviceAddr)
                .body(bytes)
                .execute()) {
            byte[] resp = httpResponse.bodyBytes();
            return serializer.deserialize(resp, RpcResponse.class);
        } catch (Exception e) {
            log.error("HttpRequestSender - convertAndSend - fail to send and get response:{}", e.getMessage());
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setException(e);
            rpcResponse.setMsg(e.getMessage());
            return rpcResponse;
        }
    }
}
