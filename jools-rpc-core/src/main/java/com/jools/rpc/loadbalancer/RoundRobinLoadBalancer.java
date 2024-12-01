package com.jools.rpc.loadbalancer;

import cn.hutool.core.util.ObjectUtil;
import com.jools.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/30 12:12
 * @description: 轮询负载均衡器，基于 JUC 得 AtomicInteger
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo selectService(Map<String, Object> params, List<ServiceMetaInfo> list) {
        if (ObjectUtil.isNull(list) || ObjectUtil.isEmpty(list)) {
            return null;
        }
        //仅有一个服务可用
        if (list.size() == 1) {
            return list.get(0);
        }
        //轮询
        int atomic = ATOMIC_INTEGER.incrementAndGet();
        int idx = atomic % list.size();
        return list.get(idx);
    }
}





