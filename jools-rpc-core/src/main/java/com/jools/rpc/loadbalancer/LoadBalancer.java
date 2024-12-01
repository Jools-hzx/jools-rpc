package com.jools.rpc.loadbalancer;

import com.jools.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 提供一个负载均衡器得通用接口 `LoadBalancer`
 */
public interface LoadBalancer {

    /**
     * 基于选择的负载均衡算法获取服务节点
     *
     * @param params 请求参数
     * @param list   服务节点列表
     * @return
     */
    ServiceMetaInfo selectService(Map<String, Object> params, List<ServiceMetaInfo> list);
}
