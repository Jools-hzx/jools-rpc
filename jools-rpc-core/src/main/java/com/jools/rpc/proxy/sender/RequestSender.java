package com.jools.rpc.proxy.sender;

import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/*
    动态代理发送请求
 */
public interface RequestSender {

    /**
     * 完成 Rpc请求响应封装解码编码
     *
     * @param serviceAddr 目的地址
     * @param rpcRequest  Rpc 请求
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    RpcResponse convertAndSend(String serviceAddr, RpcRequest rpcRequest) throws ExecutionException, InterruptedException, IOException;
}
