package com.jools.rpc.loadbalancer;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConsistentHashLoadBalancerTest {

    /**
     * 一致性 Hash 环，存放虚拟节点
     */
    private final TreeMap<Integer, String> virtualNodes = new TreeMap<>();

    /**
     * 使用 MurmurHash 算法，运算效率更高
     */
    HashFunction hashFunction = Hashing.murmur3_128(64);

    /**
     * 虚拟节点数目
     */
    private static final int VIRTUAL_NODE_NUM = 200;

    /**
     * 哈希算法，对节点 hash（IP + port）%16384结果 作为哈希环下标
     *
     * @param key
     * @return
     */
    @SuppressWarnings("all")
    private int hash(String key) {
        return hashFunction.newHasher().
                putString(key, StandardCharsets.UTF_8).
                hash().
                asInt() % 16384;
    }

    @Test
    public void testGenerateHashCircle() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i + "TEST SERVICE INFO" + new Random().nextInt(100));
        }

        //构建虚拟节点环
        for (String s : list) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = hash(s + "#" + i);
                virtualNodes.put(hash, s);
            }
        }

        //获取调用请求得 hash 值
        int hash = hash("11TEST SERVICE INFO11");

        //选择最接近并且大于等于请求调用 hash 值得虚拟节点
        Map.Entry<Integer, String> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            //如果没有大于等于调用请求 hash 值的虚拟节点；则返回环首部节点
            entry = virtualNodes.firstEntry();
        }
        System.out.println(entry.getKey() + " --->" + entry.getValue());

        /*
         输出: 852956146 --->73TEST SERVICE INFO0
         */
    }

}