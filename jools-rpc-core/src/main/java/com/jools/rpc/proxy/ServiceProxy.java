package com.jools.rpc.proxy;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jools.rpc.RpcApplication;
import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.config.RpcConfig;
import com.jools.rpc.constant.RpcConstant;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.model.ServiceMetaInfo;
import com.jools.rpc.proxy.sender.RequestSender;
import com.jools.rpc.proxy.sender.RequestSenderFactory;
import com.jools.rpc.proxy.cache.ConsumerServiceCache;
import com.jools.rpc.registry.Registry;
import com.jools.rpc.registry.RegistryFactory;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.impl.JdkSerializer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/30 11:46
 * @description: 基于 JDK 动态代理服务
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    /**
     * 消费端缓存
     */
    private final ConsumerServiceCache consumerServiceCache = new ConsumerServiceCache();

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

            /*
                版本 2.0 - 通过查询注册中心获取服务
                获取 RpcConfig -> RegistryConfig -> RegistryType
             */
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();

            //获取当前注册中心实例,
            Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistryType());
            log.info("Consumer registry type:{}", registryConfig.getRegistryType());

            //构建 ServiceMetaInfo 信息
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            log.info("Consumer request RPC service name:{}", serviceName);

            //基于 ServiceMetaInfo 内的 ServiceKey 查询所有服务节点
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            log.info("Discovery ServiceMetaInfos size:{}", serviceMetaInfos.size());

            ServiceMetaInfo registeredService01;

            //获取服务注册信息或者查询缓存
            registeredService01 = readServiceMetaInfo(serviceMetaInfos, serviceName);
            if ("default".equals(registeredService01.getServiceName())) {
                return "";
                //TODO: Handle if receive default service
            }

            //元数据处理
            Map<String, String> metadata = serviceMetaInfo.getMetadata();
            if (metadata != null && metadata.isEmpty()) {
                //TODO: Handle MetaServiceInfo if exist
            }

            String protocol = serviceMetaInfo.getProtocol();

            if (!StrUtil.isBlank(protocol)) {
                log.info("Current request protocol is: {}", protocol);
            }

            //发送请求:基于 protocol + addr
            RequestSender sender = RequestSenderFactory.getSender(protocol);
            result = sender.convertAndSend(registeredService01.getServiceAddr(), bytes);

            //如果响应数据为空
            if (result.length == 0) {
                log.error("Not Data receive from RPC request in :{}", this.getClass().getSimpleName());
                //TODO: handle if data is empty
            }

            //反序列化响应结果
            rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ServiceMetaInfo readServiceMetaInfo(List<ServiceMetaInfo> serviceMetaInfos, String serviceName) {
        if (ObjectUtil.isNotEmpty(serviceMetaInfos)) {
            // 缓存服务信息
            consumerServiceCache.writeCache(serviceName, serviceMetaInfos);
            log.info("Saved service info list to cache, serviceKey: {}", serviceName);
            // 默认使用第一个服务节点
            return serviceMetaInfos.get(0);
        }

        // 如果服务信息为空，从本地缓存读取
        log.info("No ServiceMetaInfo found by RPC request, attempting to read from cache, serviceKey: {}", serviceName);
        List<ServiceMetaInfo> cachedServiceMetaInfos = consumerServiceCache.readCache(serviceName);

        if (ObjectUtil.isEmpty(cachedServiceMetaInfos)) {
            throw new RuntimeException("No ServiceMetaInfo available for serviceKey: " + serviceName);
        }

        return cachedServiceMetaInfos.get(0);
    }
}
