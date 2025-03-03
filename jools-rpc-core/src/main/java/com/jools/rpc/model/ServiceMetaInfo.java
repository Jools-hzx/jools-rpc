package com.jools.rpc.model;

import cn.hutool.core.util.StrUtil;
import com.jools.rpc.model.registryInfo.Protocol;
import com.jools.rpc.model.registryInfo.ServiceWeight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/6 14:58
 * @description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceMetaInfo {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion = "1.0";

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口号
     */
    private Integer servicePort;

    /**
     * 服务分组(默认 default)
     */
    private String serviceGroup = "default";

    /**
     * 节点注册时间
     */
    private String registerTime = "";

    /**
     * 节点启动时间
     */
    private String startTime = "";

    /**
     * 通信协议，默认 HTTP
     */
    private String protocol = Protocol.HTTP;

    /**
     * 服务权重，默认 0
     */
    private Integer serviceWeight = ServiceWeight.ZERO;

    /**
     * 服务的动态权重
     */
    private Integer currentWeight = ServiceWeight.ZERO;

    /**
     * 自定义元数据
     * 允许用户附加额外的服务信息，便于扩展。
     */
    private Map<String, String> metadata;

    /**
     * 获取完整服务地址
     */
    public String getServiceAddr() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", this.serviceHost, this.servicePort);
        }
        return String.format("%s:%s", this.serviceHost, this.servicePort);
    }

    /**
     * 返回当前服务 ip:port
     *
     * @return
     */
    public String getServiceIpAndPort() {
        return this.serviceHost + ":" + this.servicePort;
    }

    /**
     * 获取: 服务分组/服务注册键名:服务版本
     * 1
     *
     * @return
     */
    public String getServiceKey() {
        return String.format("%s/%s:%s", serviceGroup, serviceName, serviceVersion);
    }

    /**
     * 获取服务注册节点键名
     *
     * @return
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }
}

