package com.jools.rpc.proxy;

import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.otp.TOTP;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.jools.rpc.RpcApplication;
import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.config.RpcConfig;
import com.jools.rpc.constant.RpcConstant;
import com.jools.rpc.fault.retry.RetryStrategy;
import com.jools.rpc.fault.retry.RetryStrategyFactory;
import com.jools.rpc.fault.tolerant.ErrorTolerantKeys;
import com.jools.rpc.fault.tolerant.ErrorTolerantStrategy;
import com.jools.rpc.fault.tolerant.ErrorTolerantStrategyFactory;
import com.jools.rpc.loadbalancer.LoadBalanceFactory;
import com.jools.rpc.loadbalancer.LoadBalancer;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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

        //构建请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .params(args)
                .serviceVersion(RpcConstant.DEFAULT_SERVICE_VERSION)
                .build();

        //构造请求参数 - 用于负载均衡
        Map<String, Object> requestParams = getRequestParams(method);

        //序列化请求对象 - 发送请求
        RpcResponse rpcResponse = null;

        //调用服务名称: 接口全类名
        String serviceName = method.getDeclaringClass().getName();

        //容错机制
        RpcResponse failTolerantResp = null;

        //查询到的所有服务节点
        List<ServiceMetaInfo> discoveryServiceMetaInfos = new CopyOnWriteArrayList<>();

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
            log.debug("Consumer registry type:{}", registryConfig.getRegistryType());

            //构建 ServiceMetaInfo 信息
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            log.debug("Consumer request RPC service name:{}", serviceName);

            //基于 ServiceMetaInfo 内的 ServiceKey 查询所有服务节点
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            log.debug("Discovery ServiceMetaInfos size:{}", serviceMetaInfos.size());
            if (ObjectUtil.isNotNull(serviceMetaInfos) && ObjectUtil.isNotEmpty(serviceMetaInfos)) {
                discoveryServiceMetaInfos.addAll(serviceMetaInfos);
            }

            ServiceMetaInfo selectedServiceMetaInfo;

            //获取服务注册信息或者查询缓存
            selectedServiceMetaInfo = readServiceMetaInfo(
                    serviceMetaInfos,
                    serviceMetaInfo.getServiceName(),
                    requestParams);
            log.info("Selected ServiceMetaInfo{}", JSONUtil.formatJsonStr(selectedServiceMetaInfo.toString()));

            //无注册信息 & 无合法缓存节点信息
            if ("default".equals(selectedServiceMetaInfo.getServiceName())) {
                log.warn("No serviceMetaInfos found, return default serviceMetaInfo");
            }

            //元数据处理
            Map<String, String> metadata = serviceMetaInfo.getMetadata();
            if (metadata != null && metadata.isEmpty()) {
                //TODO: Handle MetaServiceInfo if exist
            }

            //从查询到的服务注册信息得到通信协议
            String protocol = selectedServiceMetaInfo.getProtocol();
            log.info("Framework using protocol:{}", protocol);
            if (StrUtil.isBlank(protocol)) {
                log.error("Protocol unknown");
            }

            //基于协议调用对应于的发送者
            //优化: 针对 TCP 通信协议引入半包粘包处理器
            RequestSender sender = RequestSenderFactory.getSender(protocol);

            //重试机制
            RetryStrategy retryStrategy = RetryStrategyFactory.getRetryStrategy(RpcApplication.getRpcConfig().getRetryStrategyKey());

            //lambda 表达式封装成 Callable
            try {
                rpcResponse = retryStrategy.doRetry(() -> {
                    return sender.convertAndSend(selectedServiceMetaInfo.getServiceAddr(), rpcRequest);
                });
            } catch (Exception e) {
                String tolerantStrategyKeys = RpcApplication.getRpcConfig().getErrorTolerantStrategyKeys();
                ErrorTolerantStrategy tolerantStrategy = ErrorTolerantStrategyFactory.getTolerantStrategy(tolerantStrategyKeys);
                Map<String, Object> respContext = null;
                //Fail Back 策略
                if (tolerantStrategyKeys.equals(ErrorTolerantKeys.FAIL_BACK)) {
                    respContext = new HashMap<>() {{
                        put("RpcRequest", rpcRequest);
                    }};
                }
                //Fail Over 策略 - 已经访问的服务 + 所有服务信息
                if (tolerantStrategyKeys.equals(ErrorTolerantKeys.FAIL_OVER)) {
                    respContext = new HashMap<>() {{
                        put("rpcRequest", rpcRequest);
                        put("visited", selectedServiceMetaInfo);
                        put("serviceInfos", discoveryServiceMetaInfos);
                        put("retryStrategy", retryStrategy);
                        put("sender", sender);
                    }};
                }
                failTolerantResp = tolerantStrategy.doTolerant(respContext, e);
                if (failTolerantResp == null || failTolerantResp.getData() == null) {
                    log.error("Tolerant strategy returned null for request: {}", rpcRequest);
                    throw new RuntimeException("Tolerant strategy failed to provide a fallback result");
                }
                log.warn("Invok Fail Back Strategy for serviceInfos:{}", rpcRequest);
                return failTolerantResp;
            }

            //如果响应数据为空
            if (rpcResponse.getData() == null) {
                log.error("Not Data receive from RPC request in :{}", this.getClass().getSimpleName());
                //TODO: handle if data is empty
            }

            //反序列化响应结果 - 基于 HTTP 反序列化
//            rpcResponse = serializer.deserialize(result, RpcResponse.class);

            //优化，基于传输协议直接反编码得到结果
            return rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rpcResponse;
    }

    /**
     * 解析最新服务注册信息 或者 读取缓存
     *
     * @param serviceMetaInfos 注册服务信息
     * @param serviceName      注册服务名
     * @return
     */
    private ServiceMetaInfo readServiceMetaInfo(List<ServiceMetaInfo> serviceMetaInfos,
                                                String serviceName,
                                                Map<String, Object> reqParams) {
        if (ObjectUtil.isNotEmpty(serviceMetaInfos)) {
            // 缓存服务信息
            consumerServiceCache.writeCache(serviceName, serviceMetaInfos);
            log.info("Updated service info list to cache, serviceKey: {}", serviceName);
            // 优化 - 基于负载均衡算法选取
            LoadBalancer loadBalancer = LoadBalanceFactory.getInstance(RpcApplication.getRpcConfig().getLoadBalance());
            log.info("Using loadBalance, rule:{}", loadBalancer.getClass().getSimpleName());
            return loadBalancer.selectService(reqParams, serviceMetaInfos);
        }

        // 如果服务信息为空，尝试本地缓存读取
        log.info("No ServiceMetaInfo found by RPC request, attempting to read from cache, serviceKey: {}", serviceName);
        List<ServiceMetaInfo> cachedServiceMetaInfos = consumerServiceCache.readCache(serviceName);

        if (ObjectUtil.isEmpty(cachedServiceMetaInfos)) {
            throw new RuntimeException("No ServiceMetaInfo available for serviceKey: " + serviceName);
        }

        return cachedServiceMetaInfos.get(0);
    }

    //请求参数：请求服务名 + 请求方法名；用于 Hash 计算
    private Map<String, Object> getRequestParams(Method method) {
        String serviceName = method.getDeclaringClass().getName();
        String methodName = method.getName();
        return new HashMap<>() {{
            put("serviceName", serviceName);
            put("methodName", methodName);
        }};
    }
}
