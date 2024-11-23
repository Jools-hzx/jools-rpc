package com.jools.rpc.etcd;

import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.CallStreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/22 20:15
 * @description: TODO
 */

@Slf4j
public class JetcdTest {

    private static final String ADDRESS = "http://localhost:2379";

    private ClientBuilder builder;

    @Before
    public void inti() {
        //创建 ClientBuilder 对象
        builder = Client.builder();

        //设置 etcd 端点地址
        builder.endpoints(ADDRESS);

        //构建 Etcd 客户端
        Client client = builder.build();

        client.close();
    }

    @Test
    public void testLease() {
        Client client = builder.endpoints(ADDRESS).build();

        //创建租约
        Lease leaseClient = client.getLeaseClient();
//        LeaseGrantResponse response = leaseClient.grant(10).get();
//
//        //获取租约 ID
//        long leaseId = response.getID();
//        System.out.println("Create lease with ID:" + leaseId);

        // 准备键
        String etcdKey = "/test/example_key";
        ByteSequence key = ByteSequence.from(etcdKey.getBytes());
        ByteSequence value = ByteSequence.from(etcdKey.getBytes());

        //开始续期
        leaseClient.grant(60)
                .thenAccept(result -> {
                    // 租约ID
                    long leaseId = result.getID();

                    log.info("[{}]申请租约成功，租约ID [{}]", key, Long.toHexString(leaseId));

                    // 准备好put操作的client
                    KV kvClient = builder.build().getKVClient();

                    // put操作时的可选项，在这里指定租约ID
                    PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();

                    // put操作
                    kvClient.put(key, value, putOption)
                            .thenAccept(putResponse -> {
                                // put操作完成后，再设置无限续租的操作
                                leaseClient.keepAlive(leaseId, new CallStreamObserver<>() {
                                    @Override
                                    public boolean isReady() {
                                        return false;
                                    }

                                    @Override
                                    public void setOnReadyHandler(Runnable onReadyHandler) {

                                    }

                                    @Override
                                    public void disableAutoInboundFlowControl() {

                                    }

                                    @Override
                                    public void request(int count) {
                                    }

                                    @Override
                                    public void setMessageCompression(boolean enable) {

                                    }

                                    /**
                                     * 每次续租操作完成后，该方法都会被调用
                                     *
                                     * @param value
                                     */
                                    @Override
                                    public void onNext(LeaseKeepAliveResponse value) {
                                        log.info("[{}]续租完成，TTL[{}]", Long.toHexString(leaseId), value.getTTL());
                                    }

                                    @Override
                                    public void onError(Throwable t) {
                                        log.error("onError", t);
                                    }

                                    @Override
                                    public void onCompleted() {
                                        log.info("onCompleted");
                                    }
                                });
                            });
                });
    }

    @Test
    public void testEtcdDelete() throws ExecutionException, InterruptedException {
        Client client = builder.endpoints(ADDRESS).build();
        // 准备键
        String etcdKey = "/test/example_key";
        ByteSequence key = ByteSequence.from(etcdKey.getBytes());

        // 执行put操作（实际上也是更新）
        KV kvClient = client.getKVClient();
        DeleteResponse response = kvClient.delete(key).get();

        //输出结果
        System.out.println("Delete operation result:" + response.getDeleted());
    }

    @Test
    public void testEtcdUpdate() throws ExecutionException, InterruptedException {
        // 创建Etcd客户端
        Client client = builder.endpoints(ADDRESS).build();

        // 准备键
        String etcdKey = "/test/example_key";
        ByteSequence key = ByteSequence.from(etcdKey.getBytes());
        ByteSequence newValue = ByteSequence.from((etcdKey + new Random().nextInt(100)).getBytes());

        // 执行put操作（实际上也是更新）
        KV kvClient = client.getKVClient();
        PutResponse response = kvClient.put(key, newValue).get();

        // 输出结果
        System.out.println("Update operation successful: " + response.getPrevKv().getKey() +
                " -> " + response.getPrevKv().getValue());
    }

    @Test
    public void testEtcdPut() throws ExecutionException, InterruptedException {
        // 创建 Etcd 客户端
        Client client = builder.endpoints(ADDRESS).build();

        // 准备键值对
        ByteSequence key = ByteSequence.from("/test/example_key".getBytes());
        ByteSequence value = ByteSequence.from("/test/example_value".getBytes());

        //执行 put 操作
        KV kvClient = client.getKVClient();
        PutResponse response = kvClient.put(key, value).get();

        // 输出结果
        System.out.println("Put operation result:" + response.getPrevKv().toString());
    }

    @Test
    public void testEtcdGet() throws ExecutionException, InterruptedException {
        //创建客户端
        Client client = Client.builder().endpoints(ADDRESS).build();

        // 准备键
        String etcdKey = "/test/example_key";
        ByteSequence key = ByteSequence.from(etcdKey.getBytes());

        //执行 get 操作
        KV kvClient = client.getKVClient();
        GetResponse getResponse = kvClient.get(key).get();

        if (!getResponse.getKvs().isEmpty()) {
            System.out.println("Value of" + etcdKey + getResponse.getKvs());
        } else {
            System.out.println("No value found for " + etcdKey);
        }
    }
}








