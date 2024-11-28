package com.jools.rpc.model.registryInfo;

/**
 * 支持的服务通信协议
 * HTTP \ HTTPS \ GRPC \ Dubbo \ TCP[传输层]
 */
public interface Protocol {

    String TCP = "tcp";
    String HTTP = "http";
    String HTTPS = "https";
    String GRPC = "gRPC";
    String Dubbo = "dubbo";
}
