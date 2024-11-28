package com.jools.rpc.server.tcp;

import com.jools.rpc.RpcApplication;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.protocol.ProtocolMessage;
import com.jools.rpc.protocol.ProtocolMessageDecoder;
import com.jools.rpc.protocol.ProtocolMessageEncoder;
import com.jools.rpc.protocol.ProtocolMessageTypeEnum;
import com.jools.rpc.registry.LocalRegistry;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Jools He
 * @description: Tcp 请求处理器 - 服务提供者用于处理消费者的请求和响应回复
 */

@Slf4j
public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket socket) {

        //处理连接
        socket.handler(buffer -> {

            //解码反序列化，由 Buffer 获取 ProtocolMessage
            ProtocolMessage<RpcRequest> requestProtocolMessage;
            try {
                requestProtocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                log.error("Decode error --- TcpServerHandler --- handle()");
                throw new RuntimeException(e);
            }

            //获取 ProtocolMessage 请求; 请求的消息体为 RpcRequest
            RpcRequest rpcRequest = requestProtocolMessage.getBody();
            String serviceName = rpcRequest.getServiceName();   //调用全类名
            String methodName = rpcRequest.getMethodName();     //调用方法名
            Object[] params = rpcRequest.getParams();           //调用实参
            Class<?>[] paramTypes = rpcRequest.getParamTypes(); //调用形参

            //反射调用
            Object result;
            Class<?> cls = null;
            RpcResponse rpcResponse = new RpcResponse();
            try {
                //查询本地注册服务，反射调用
                cls = LocalRegistry.getService(serviceName);
                Method method = cls.getDeclaredMethod(methodName, paramTypes);
                Object instance = cls.getDeclaredConstructor().newInstance();
                result = method.invoke(instance, params);

                //构建 RpcResponse - 成功
                rpcResponse.setMsg("2xx - Request Succeed");
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setData(result);
            } catch (Exception e) {
                log.error("Error when reflecting instance:{}", cls.getSimpleName());
                //构建 RcpResponse - 失败
                rpcResponse.setMsg("5xx - Server Handle error");
                rpcResponse.setException(e);
            }

            //基于消费者发送的 ProtocolMessage 获取消息头，复用字段保证: 魔数 + 版本号 一致
            ProtocolMessage.Header header = requestProtocolMessage.getHeader();
            header.setMessageType(ProtocolMessageTypeEnum.RESPONSE.getMessageType());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);

            try {
                //编码序列化 - 由 ProtocolMessage 获取 Buffer
                Buffer buf = ProtocolMessageEncoder.encode(responseProtocolMessage);
                socket.write(buf);
            } catch (Exception e) {
                throw new RuntimeException("Encode fail at TcpServerHandler --- handle()");
            }
        });
    }
}
