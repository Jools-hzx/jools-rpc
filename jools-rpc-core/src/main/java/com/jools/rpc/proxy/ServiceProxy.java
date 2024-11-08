package com.jools.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jools.rpc.RpcApplication;
import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.config.RpcConfig;
import com.jools.rpc.constant.RpcConstant;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.model.ServiceMetaInfo;
import com.jools.rpc.registry.Registry;
import com.jools.rpc.registry.RegistryFactory;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.impl.JdkSerializer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/30 11:46
 * @description: 基于 JDK 动态代理服务
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //指定序列化器
        Serializer serializer = new JdkSerializer();

        //构建请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .params(args)
                .build();

        //序列化请求对象 - 发送请求
        RpcResponse rpcResponse = null;

        //调用服务名称: 接口全类名
        String serviceName = method.getDeclaringClass().getName();

        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            byte[] result;

            //版本 2.0 - 通过查询注册中心获取服务
            //获取 RpcConfig -> RegistryConfig -> RegistryType
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();

            //获取当前注册中心实例,
            Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistryType());

            //构建 ServiceMetaInfo 信息
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            log.info("RPC service name:{}", serviceName);

            //基于 ServiceMetaInfo 内的 ServiceKey 查询所有服务节点
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            log.info("ServiceMetaInfos size:{}", serviceMetaInfos.size());

            //默认: 当前仅使用第一个服务节点
            ServiceMetaInfo registeredService01 = serviceMetaInfos.get(0);

            //通过获取到的服务节点，将请求发送到该服务节点的 ServiceAddr (ip + port)
            try (HttpResponse httpResponse = HttpRequest
                    .post(registeredService01.getServiceAddr())
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
