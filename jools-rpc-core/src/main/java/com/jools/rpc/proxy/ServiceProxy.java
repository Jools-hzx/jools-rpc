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

        //构建请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .params(args)
                .serviceVersion(RpcConstant.DEFAULT_SERVICE_VERSION)
                .build();

        //序列化请求对象 - 发送请求
        RpcResponse rpcResponse = null;

        //调用服务名称: 接口全类名
        String serviceName = method.getDeclaringClass().getName();

        try {
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
                log.info("No serviceMetaInfos found, return default serviceMetaInfo");
            }

            //元数据处理
            Map<String, String> metadata = serviceMetaInfo.getMetadata();
            if (metadata != null && metadata.isEmpty()) {
                //TODO: Handle MetaServiceInfo if exist
            }

            //从查询到的服务注册信息得到通信协议
            String protocol = registeredService01.getProtocol();
            log.info("Framework using protocol:{}", protocol);
            if (StrUtil.isBlank(protocol)) {
                log.error("Protocol unknown");
            }

            //基于协议调用对应于的发送者
            RequestSender sender = RequestSenderFactory.getSender(protocol);
            rpcResponse = sender.convertAndSend(registeredService01.getServiceAddr(), rpcRequest);

            //如果响应数据为空
            if (rpcResponse.getData() == null) {
                log.error("Not Data receive from RPC request in :{}", this.getClass().getSimpleName());
                //TODO: handle if data is empty
            }

            //反序列化响应结果
//            rpcResponse = serializer.deserialize(result, RpcResponse.class);

            //切换 - 基于 TCP 和 自定义协议 传输数据; 基于协议编码解码器
            return rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析最新服务注册信息 或者 读取缓存
     *
     * @param serviceMetaInfos 注册服务信息
     * @param serviceName      注册服务名
     * @return
     */
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
