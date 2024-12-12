package com.jools.rpc.fault.tolerant;

import com.jools.rpc.fault.retry.RetryStrategy;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.model.ServiceMetaInfo;
import com.jools.rpc.proxy.sender.RequestSender;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/11 10:59
 * @description: 容错 —— 故障转移
 */

@Slf4j
public class FailOverTolerantStrategy implements ErrorTolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        //记录已经访问过的服务
        ServiceMetaInfo visited = (ServiceMetaInfo) context.get("visited");
        //记录未被访问过的服务
        List<ServiceMetaInfo> serviceInfos = ((List<ServiceMetaInfo>) context.get("serviceInfos"));
        serviceInfos.remove(visited);
        //迁移，尝试访问未访问过的服务
        RetryStrategy retry = (RetryStrategy) context.get("retryStrategy");
        RequestSender requestSender = (RequestSender) context.get("sender");
        RpcRequest request = (RpcRequest) context.get("rpcRequest");
        for (ServiceMetaInfo serviceInfo : serviceInfos) {
            log.warn("Fail Over - select service:{}", serviceInfo.getServiceAddr());
            try {
                retry.doRetry(() -> {
                    return requestSender.convertAndSend(serviceInfo.getServiceAddr(), request);
                });
            } catch (Exception ex) {
                //尝试下一个节点
                continue;
            }
        }
        return new RpcResponse();
    }
}
