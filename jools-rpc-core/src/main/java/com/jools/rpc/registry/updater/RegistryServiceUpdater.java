package com.jools.rpc.registry.updater;

import com.jools.rpc.RpcApplication;
import com.jools.rpc.model.ServiceMetaInfo;
import com.jools.rpc.registry.Registry;
import com.jools.rpc.registry.RegistryFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/1 18:08
 * @description: TODO
 */
@Slf4j
public class RegistryServiceUpdater {
    // 创建线程池
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * 异步更新服务到注册中心
     *
     * @param list 要更新的服务列表
     * @return
     */
    public static CompletableFuture<Void> asynUpdateServiceMetaInfos(List<ServiceMetaInfo> list) {
        // 获取注册中心实例
        Registry registry = RegistryFactory.getRegistry(
                RpcApplication.getRpcConfig().getRegistryConfig().getRegistryType()
        );

        //将每个服务的注册操作包装成异步任务
        List<CompletableFuture<Void>> futures = list.stream().map(
                info -> CompletableFuture.runAsync(() -> {
                    try {
                        registry.registry(info);
                    } catch (Exception e) {
                        log.debug("Error when update ServiceMetaInfo key:{}, message:{}",
                                info.getServiceNodeKey(),
                                e.getMessage());
                        throw new RuntimeException(e);
                    }
                }, executor)
        ).toList();
        // 等待所有任务完成
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

}
