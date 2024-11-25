package com.jools.rpc.proxy.sender;

import com.jools.rpc.model.registryInfo.Protocol;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求协议简单工厂
 * 版本 3.0 优化
 */
@Slf4j
public class RequestSenderFactory {

    private static ConcurrentHashMap<String, RequestSender> senders = new ConcurrentHashMap<>();

    static {
        senders.put(Protocol.HTTP, new HttpRequestSender());
    }

    public static RequestSender getSender(String protocol) {
        if (Protocol.HTTP.equals(protocol)) {
            return senders.get(Protocol.HTTP);
        }
        log.error("Not Protocol type match", protocol);
        throw new RuntimeException("Not Protocol type match" + protocol);
    }
}
