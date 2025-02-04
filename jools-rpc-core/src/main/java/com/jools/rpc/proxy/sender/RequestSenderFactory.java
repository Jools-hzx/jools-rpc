package com.jools.rpc.proxy.sender;

import com.jools.rpc.model.registryInfo.Protocol;
import com.jools.rpc.spi.SpiLoader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求协议简单工厂
 * 版本 3.0 优化
 */
@Slf4j
public class RequestSenderFactory {

    /**
     * 基于自定义 SPI 资源路径加载所有 RequestSender 配置
     * 配置格式: key=RequeSender实现类全类名
     */
    static {
        SpiLoader.load(RequestSender.class);
    }

    public static final RequestSender DEFAULT_REQUEST_SENDER = new TcpRequestSender();

    public static RequestSender getSender(String protocol) {
        return SpiLoader.getInstance(RequestSender.class, protocol);
    }
}
