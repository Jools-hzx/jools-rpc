package com.jools.rpc.loadbalancer;

import cn.hutool.core.lang.func.Func;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.Log;
import com.google.common.hash.Funnel;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.jools.rpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/30 12:12
 * @description: 一致性Hash负载均衡
 * 规则:
 * 1. hash(IP + port) % 16384 计算选取的 hash 环下标
 * 2. 选择最接近且大于调用请求的 hash 值的虚拟节点
 * 3. 如果不存在则返回首个服务节点下标
 */
@Slf4j
public class ConsistentHashLoadBalancer implements LoadBalancer {

    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 每个服务节点的虚拟节点个数; 推荐 32 或者更大
     */
    private static final int VIRTUAL_NODES_NUM = 64;

    /**
     * 选择 MurmurHash 算法，运算效率高; 预防碰撞也很强
     */
    private static final HashFunction HASH_FUNCTION = Hashing.murmur3_128(64);

    @Override
    public ServiceMetaInfo selectService(Map<String, Object> reqestParams, List<ServiceMetaInfo> list) {

        if (ObjectUtil.isNull(list) || ObjectUtil.isEmpty(list)) {
            return null;
        }

        //仅有一个服务
        if (list.size() == 1) {
            return list.get(0);
        }

        //构建虚拟链表环
        for (ServiceMetaInfo serviceMetaInfo : list) {
            for (int i = 0; i < VIRTUAL_NODES_NUM; i++) {
                //构建虚拟节点
                String virtualAddr = serviceMetaInfo.getServiceIpAndPort() + "#" + i;
                int hash = getHash(virtualAddr);
//                log.info("Add virtual service node:{}, index:{}", virtualAddr, hash);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        int hash = getHash(reqestParams);

        //选择最接近且大于调用请求的 hash 值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            //如果不存在大于等于调用请求 hash 值的虚拟节点，则返回环首部节点
            entry = virtualNodes.firstEntry();
        }
        //返回该entry 存储的节点信息
//        log.debug("key:{} index:{} select service Node:{}",
//                reqestParams.toString(),
//                hash,
//                entry.getValue().getServiceHost());
        return entry.getValue();
    }

    /**
     * 哈希算法，对节点 hash（IP + port）% 16384结果 作为哈希环下标
     *
     * @param key
     * @return
     */
    @SuppressWarnings("all")
    private int getHash(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Object to hash cannot be null");
        }

        return Math.abs(
                HASH_FUNCTION.newHasher()
                        .putObject(obj, createFunnel())
                        .hash()
                        .asInt()
        ) % 16384;
    }

    /**
     * 转化为字节表示；默认使用 toString
     *
     * @return
     */
    private Funnel<Object> createFunnel() {
        return (obj, into) -> {
            into.putString(obj.toString(), StandardCharsets.UTF_8);
        };
    }
}
