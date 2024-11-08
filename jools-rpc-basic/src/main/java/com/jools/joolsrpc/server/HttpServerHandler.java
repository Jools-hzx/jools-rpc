package com.jools.joolsrpc.server;

import com.jools.joolsrpc.model.RpcRequest;
import com.jools.joolsrpc.model.RpcResponse;
import com.jools.joolsrpc.registry.LocalRegistry;
import com.jools.joolsrpc.serializer.Serializer;
import com.jools.joolsrpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/30 10:36
 * @description: HTTP 请求处理
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {

        //动态基于 RpcConfig 配置获取序列化器
//        final Serializer serializer = SerializerFactory.getInstance();

        //记录日志
        System.out.println("Received request: " + request.method() + " " + request.uri());

        //异步处理 HTTP 请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;

            try {
                //序列化构建请求
//                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //构造响应对象返回
            RpcResponse rpcResponse = new RpcResponse();

            //如果请求为 null, 直接返回
            if(request == null) {
                return;
            }

            try {
                //获取调用到的服务实现类
                Class<?> service = LocalRegistry.getService(rpcRequest.getServiceName());

                //通过反射调用方法，返回方法结果
                Method declaredMethod = service.getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
                Object methodResult = declaredMethod.invoke(
                        //JDK 9 之后不推荐 直接 newInstance()
                        service.getDeclaredConstructor().newInstance(),
                        rpcRequest.getParams()
                );

                //封装数据到返回
                rpcResponse.setData(methodResult);
                rpcResponse.setDataType(declaredMethod.getReturnType());
                rpcResponse.setMsg("OK - 2xx");
            } catch (Exception e) {
                //输出异常
                e.printStackTrace();
                rpcResponse.setMsg(e.getMessage());
                rpcResponse.setException(e);
            }
            //响应
//            doResponse(request, rpcResponse, serializer);
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
