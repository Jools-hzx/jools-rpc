package com.jools.rpc.server;

import com.jools.rpc.RpcApplication;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.registry.LocalRegistry;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/30 10:36
 * @description: HTTP 请求处理
 */
@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {

        //动态基于 RpcConfig 配置获取序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        //异步处理 HTTP 请求
        request.bodyHandler(body -> {
            log.info("Receive Remote request, uri:{}, remote address:{}", request.uri(), request.remoteAddress());
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;

            try {
                //反序列化 RpcRequest 请求
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
                //记录日志
                log.info("Received RPC-Request -- request service:{}, request method:{}",
                        rpcRequest.getServiceName(), rpcRequest.getMethodName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //构造响应对象返回
            RpcResponse rpcResponse = new RpcResponse();

            //如果请求为 null, 直接返回
            if (rpcRequest == null) {
                rpcResponse.setMsg("rpc response is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                //获取调用到的服务实现类
                String serviceName = rpcRequest.getServiceName();
                Object instance = LocalRegistry.getService(serviceName);
                if (instance == null) {
                    throw new RuntimeException("Service instance not found: " + serviceName);
                }
                Method method = instance.getClass().getDeclaredMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
                log.info("Invoke service name:{}, method name:{}", rpcRequest.getServiceName(), rpcRequest.getMethodName());

                Object methodResult = method.invoke(
                        //JDK 9 之后不推荐 直接 newInstance()
                        instance,
                        rpcRequest.getParams()
                );

                //封装数据到返回
                rpcResponse.setData(methodResult);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMsg("OK - 2xx");
            } catch (Exception e) {
                //输出异常
                e.printStackTrace();
                rpcResponse.setMsg(e.getMessage());
                rpcResponse.setException(e);
            }
            //响应
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 响应
     *
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        log.info(
                "Enter HttpServer Handler doResponse, rpcResponse message: {}, data type:{}",
                rpcResponse.getMsg(),
                rpcResponse.getDataType()
        );

        //定义响应消息头
        HttpServerResponse httpServerResponse = request
                .response()
                .putHeader("content-type", "application/json");

        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
