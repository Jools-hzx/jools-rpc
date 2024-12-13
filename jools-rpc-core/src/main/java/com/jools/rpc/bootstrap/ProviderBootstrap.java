package com.jools.rpc.bootstrap;

import com.jools.rpc.RpcApplication;
import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.config.RpcConfig;
import com.jools.rpc.model.ServiceMetaInfo;
import com.jools.rpc.model.ServiceRegisterInfo;
import com.jools.rpc.model.registryInfo.Protocol;
import com.jools.rpc.model.registryInfo.ServiceWeight;
import com.jools.rpc.registry.LocalRegistry;
import com.jools.rpc.registry.Registry;
import com.jools.rpc.registry.RegistryFactory;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerFactory;
import com.jools.rpc.server.tcp.VertxTcpServer;
import com.jools.rpc.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Jools He
 * @description: 服务提供者启动类
 */
@Slf4j
public class ProviderBootstrap {

    public static void init(List<ServiceRegisterInfo<?>> registerInfos) throws ExecutionException, InterruptedException {
        // RPC 框架初始化
        RpcApplication.init();

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        String registryType = registryConfig.getRegistryType();

        Serializer instance = SerializerFactory.getInstance(rpcConfig.getSerializer());
        log.info("Provider Serializer type:{}", instance.getClass());

        //获取注册中心客户端
        Registry registry = RegistryFactory.getRegistry(registryType);
        log.info("Provider registry type:{}", registryType);

        //注册服务
        for (ServiceRegisterInfo<?> registerInfo : registerInfos) {
            //注册到本地服务中心
            String serviceName = registerInfo.getServiceName();
            LocalRegistry.register(serviceName, registerInfo.getImplClass());
            //构建 ServiceMetaInfo 注册到服务中心
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);

            //注册的 ServiceAddr = ip + port
            //ip + port 为 Rpc服务配置类内配置
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(Integer.valueOf(rpcConfig.getServerPort()));

            //填充字段，默认权重为 1; 协议类型基于 TCP 的 ProtocolMessage
            serviceMetaInfo.setServiceWeight(ServiceWeight.ONE);
            serviceMetaInfo.setCurrentWeight(serviceMetaInfo.getServiceWeight());
            serviceMetaInfo.setStartTime(DateUtils.formatLocalTimeDate(LocalDateTime.now()));
            serviceMetaInfo.setProtocol(Protocol.TCP);
            serviceMetaInfo.setMetadata(new HashMap<>());

            //完成注册 - 默认为: 为 com.jools.exp.common.service.UserService:1.0/localhost:8888
            registry.registry(serviceMetaInfo);
        }
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8888);
    }
}
