package com.jools.rpc.model.registryInfo;

/**
 * 支持的服务通信协议
 * HTTP \ HTTPS \ GRPC \ Dubbo
 */
public interface Protocol {

    String HTTP = "http";
    String HTTPS = "https";
    String GRPC = "gRPC";
    String Dubbo = "dubbo";
}
