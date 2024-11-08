package com.jools.rpc.registry;

import cn.hutool.json.JSONUtil;
import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/6 14:22
 * @description: TODO
 */
@Slf4j
public class EtcdRegistry implements Registry {

    /**
     * Etcd 客户端
     */
    private Client client;

    /**
     * 键值对操作客户端
     */
    private KV kvClient;

    /**
     * 注册服务的根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // create client using endpoints
        Client client = Client.builder()
                .endpoints("http://localhost:2379")
                .build();

        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());

        // put the key-value
        kvClient.put(key, value).get();

        // get the CompletableFuture
        CompletableFuture<GetResponse> getFuture = kvClient.get(key);

        // get the value from CompletableFuture
        GetResponse response = getFuture.get();

        // delete the key
        kvClient.delete(key).get();
    }

    @Override
    public void init(RegistryConfig registryConfig) {
        //基于配置中ip+端口+过期时间创建 client
        this.client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();

        //创建 kv 客户端
        kvClient = client.getKVClient();
    }

    @Override
    public boolean registry(ServiceMetaInfo serviceMetaInfo) {
        //创建 Lease 客户端
        Lease leaseClient = client.getLeaseClient();

        //租约 Id
        long leaseId;
        //注册的 key 值
        String registryKey;

        //转化为字节流 k - v
        ByteSequence key;
        ByteSequence value;

        try {
            //30s 租约
            leaseId = leaseClient.grant(300).get().getID();

            //设置要存储的服务键值: /rpc/ + ServiceNode(服务名:版本号/IP:Port)
            registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
            key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
            value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

            // 将键值对与租约关联起来，并且设置过期时间
            PutOption putOption = PutOption
                    .builder()
                    .withLeaseId(leaseId)
                    .build();

            //执行 put 操作
            kvClient.put(key, value, putOption).get();
            log.info("Register Service Node key:{}", serviceMetaInfo.getServiceNodeKey());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean unRegistry(ServiceMetaInfo serviceMetaInfo) {
        //删除 - 基于 ServiceNode (service name + service version + host address + port)
        try {
            this.kvClient.delete(ByteSequence.from(
                    ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(),
                    StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //基于前缀索引搜索
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        GetOption getOption = GetOption.builder().isPrefix(true).build();
        try {
            List<KeyValue> keyValueList = kvClient.get(
                    ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                    getOption
            ).get().getKvs();

            //解析服务名称
            return keyValueList
                    .stream()
                    .map((kv) -> {
                        //映射成 ServiceMetaInfo 对象
                        String value = kv.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败:" + e);
        }
    }

    @Override
    public void destory() {
        System.out.println("节点下线");
        if (this.kvClient != null) {
            this.kvClient.close();
        }
        if (this.client != null) {
            this.client.close();
        }
    }
}
