package com.jools.rpc.proxy.sender;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jools He
 * @version 1.0
 * @description: 请求协议 - HTTP
 */
@Slf4j
public class HttpRequestSender implements RequestSender {

    @Override
    public byte[] convertAndSend(String serviceAddr, byte[] bytes) {
        try (HttpResponse httpResponse = HttpRequest
                .post(serviceAddr)
                .body(bytes)
                .execute()) {
            return httpResponse.bodyBytes();
        } catch (Exception e) {
            log.error("HttpRequestSender - convertAndSend - fail to send and get response:{}", e.getMessage());
            return new byte[]{};
        }
    }
}
