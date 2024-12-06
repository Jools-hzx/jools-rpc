package com.jools.rpc.loadbalancer;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.jools.rpc.model.ServiceMetaInfo;
import com.jools.rpc.registry.updater.RegistryServiceUpdater;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/1 16:59
 * @description: 加权轮询算法
 * 规则:
 * 1. 每一轮选中权重最大的服务节点；
 * 2. 被选中的服务节点当前权重(currentWeight)减去总权重(totalWeight)
 * 3. 每个服务节点更新当前权重(currentWeight) + 自身服务权重(serviceWeight)
 * 4. 异步更新各个服务节点的权重到注册中心
 */
@Slf4j
public class RoundWeightRoundBalancer implements LoadBalancer {

    private volatile int totalWeight = -1;

    @Override
    public ServiceMetaInfo selectService(Map<String, Object> params, List<ServiceMetaInfo> list) {
        //没有注册信息
        if (ObjectUtil.isNull(list) || ObjectUtil.isEmpty(list)) {
            return null;
        }

        //仅有一个服务
        if (list.size() == 1) {
            return list.get(0);
        }

        //1. 计算权重和
        synchronized (this) {
            if (this.totalWeight == -1) {
                // 提取设置的 serviceWeight 字段; 求权值总和
                totalWeight = list.stream()
                        .map(ServiceMetaInfo::getServiceWeight)
                        .filter(Objects::nonNull)
                        .mapToInt(Integer::intValue)
                        .sum();
                log.debug("Initializing Service Total Weight:{}", this.totalWeight);
            }
        }

        //2. 选取当前服务权重最高的服务
        ServiceMetaInfo selectServiceMeta = list.stream().max(Comparator.comparingInt(ServiceMetaInfo::getCurrentWeight)).get();
        log.debug("Selected ServiceMetaInfo: {}", JSONUtil.formatJsonStr(selectServiceMeta.toString()));

        //3. 更新当前选取的服务权重: 当前权重(currentWeight) - 初始总权重(totalWeight)
        selectServiceMeta.setCurrentWeight(selectServiceMeta.getCurrentWeight() - this.totalWeight);
        log.debug("Update selected ServiceMetaInfo weight:{}", selectServiceMeta.getCurrentWeight());

        //4. 更新所有服务的当前权重为: 当前权重(currentWeight) + 初始设置权重(serviceWeight)
        list.forEach((info) -> {
            info.setCurrentWeight(info.getCurrentWeight() + info.getServiceWeight());
//            log.debug("Update serviceMetaInfo:{}", JSONUtil.formatJsonStr(info.toString()));
        });

        //5. 异步 - 更新注册权重到注册中心
        RegistryServiceUpdater.asynUpdateServiceMetaInfos(list);

        return selectServiceMeta;
    }
}
