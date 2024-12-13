package com.jools.exp.provider;

import com.jools.exp.common.service.UserService;
import com.jools.rpc.RpcApplication;
import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.config.RpcConfig;
import com.jools.rpc.model.ServiceMetaInfo;
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

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/1 11:29
 * @description: 负载均衡测试 - 新提供者，注册同一个 UserService 服务，但是绑定在不同端口
 */
@Slf4j
public class ProviderExample2 {

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        // RPC 框架初始化
        RpcApplication.init();

        Serializer instance = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        log.info("Provider Serializer type:{}", instance.getClass());


        //注册服务名一致
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        String registryType = registryConfig.getRegistryType();
        Registry registry = RegistryFactory.getRegistry(registryType);
        log.info("Provider registry type:{}", registryType);

        //构建 ServiceMetaInfo 将服务注册到中心 - 注册服务名一致，端口不一致
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(10000);
        //默认权重 1
        serviceMetaInfo.setServiceWeight(ServiceWeight.ONE);
        serviceMetaInfo.setCurrentWeight(serviceMetaInfo.getServiceWeight());
        serviceMetaInfo.setStartTime(DateUtils.formatLocalTimeDate(LocalDateTime.now()));

        serviceMetaInfo.setProtocol(Protocol.TCP);
        serviceMetaInfo.setMetadata(new HashMap<>());

        registry.registry(serviceMetaInfo);

        //和注册服务端口一致，处理负载均衡转发的请求(基于 TCP)
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(10000);
    }
}
