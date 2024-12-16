package com.jools.rpc.config;

import com.jools.rpc.fault.retry.RetryStrategyFactory;
import com.jools.rpc.fault.retry.RetryStrategyKeys;
import com.jools.rpc.fault.tolerant.ErrorTolerantKeys;
import com.jools.rpc.fault.tolerant.ErrorTolerantStrategy;
import com.jools.rpc.loadbalancer.LoadBalancerKeys;
import com.jools.rpc.registry.RegistryFactory;
import com.jools.rpc.serializer.SerializerKeys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Jools He
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcConfig {

    /**
     * 服务名称
     */
    private String name = "jools-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口
     */
    private String serverPort = "8888";

    /**
     * 开启模拟调用供测试
     */
    private boolean mock = false;

    /**
     * 序列化器类型
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器配置
     */
    private String loadBalance = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 请求重试策略，默认不开启
     */
    private String retryStrategyKey = RetryStrategyKeys.fixInterval;

    /**
     * 容错策略，默认快速失败
     */
    private String errorTolerantStrategyKeys = ErrorTolerantKeys.FAIL_FAST;
}
