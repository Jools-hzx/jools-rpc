package com.jools.rpc.loadbalancer;

import cn.hutool.core.util.ObjectUtil;
import com.jools.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/30 12:12
 * @description: 随机轮询负载均衡器
 */
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public ServiceMetaInfo selectService(Map<String, Object> params, List<ServiceMetaInfo> list) {
        if (ObjectUtil.isNull(list) || ObjectUtil.isEmpty(list)) {
            return null;
        }
        //仅有一个服务
        if (list.size() == 1) {
            return list.get(0);
        }
        int random = new Random().nextInt(list.size());
        return list.get(random);
    }
}




