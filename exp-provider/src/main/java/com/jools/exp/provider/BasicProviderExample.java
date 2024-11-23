package com.jools.exp.provider;

import com.jools.exp.common.service.UserService;
import com.jools.rpc.RpcApplication;
import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.config.RpcConfig;
import com.jools.rpc.model.ServiceMetaInfo;
import com.jools.rpc.registry.LocalRegistry;
import com.jools.rpc.registry.Registry;
import com.jools.rpc.registry.RegistryFactory;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerFactory;
import com.jools.rpc.server.HttpServer;
import com.jools.rpc.server.impl.VertxHttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

/**
 * @author Jools He
 * @version 1.0
 * @description: 简易服务提供者示例
 */
@Slf4j
public class BasicProviderExample {

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        // RPC 框架初始化
        RpcApplication.init();

         /*
          序列化器版本 2.0 - 支持多种序列化器，基于配置切换; 默认基于 JDK 序列化器
         */
        Serializer instance = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        log.info("Provider Serializer type:{}", instance.getClass());


        //待请求的服务名称
        String serviceName = UserService.class.getName();

        /*
            1.0 版本 - 直接 call 本地服务注册中心
        */
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        /*
         版本 3.0 - 支持切换注册中心 [当前仅注册 UserService 服务]
         获取注册中心信息 - 配置项 registry.registryType 默认 Etcd
         基于RpcConfig -> RegistryConfig -> 获取到注册中心类型
         */
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        String registryType = registryConfig.getRegistryType();

        //获取注册中心客户端
        Registry registry = RegistryFactory.getRegistry(registryType);
        log.info("Provider registry type:{}", registryType);

        //构建 ServiceMetaInfo 将服务注册到中心
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);

        //注册的 ServiceAddr = ip + port
        //ip + port 为 Rpc服务配置类内配置
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(Integer.valueOf(rpcConfig.getServerPort()));

        //完成注册 - 默认为:
        //serviceKey 为 com.jools.exp.common.service.UserService:1.0
        //serviceNodeKey 为 com.jools.exp.common.service.UserService:1.0/localhost:888
        registry.registry(serviceMetaInfo);

        //提供服务
        HttpServer vertxServer = new VertxHttpServer();
        vertxServer.doStart(8888);
    }
}
