package com.jools.joolsrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jools.joolsrpc.model.RpcRequest;
import com.jools.joolsrpc.model.RpcResponse;
import com.jools.joolsrpc.serializer.Serializer;
import com.jools.joolsrpc.serializer.impl.JdkSerializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/30 11:46
 * @description: 基于 JDK 动态代理服务
 */
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //指定序列化器
        Serializer serializer = new JdkSerializer();

        //构建请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getSimpleName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .params(args)
                .build();

        //序列化请求对象 - 发送请求
        RpcResponse rpcResponse = null;
        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            byte[] result;

            //这里的地址被硬编码了 (需要使用注册中心和服务发现)
            //将序列化后的 RpcRequest 使用 post 请求发送
            try (HttpResponse httpResponse = HttpRequest
                    .post("http://localhost:8888")
                    .body(bytes)
                    .execute()) {
                //获取响应结果
                result = httpResponse.bodyBytes();
            }

            //反序列化响应结果
            rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
