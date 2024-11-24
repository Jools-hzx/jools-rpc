package com.jools.rpc.proxy.sender;

/*
    动态代理发送请求
 */
public interface RequestSender {

    byte[] convertAndSend(String serviceAddr, byte[] bytes);
}
