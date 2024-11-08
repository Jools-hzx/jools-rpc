package com.jools.rpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * 获取完整服务地址
     */
    public String getServiceAddr() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", this.serviceHost, this.servicePort);
        }
        return String.format("%s:%s", this.serviceHost, this.servicePort);
    }

    /**
     * 获取服务注册键名 + 服务当前版本
     *1
     * @return
     */
    public String getServiceKey() {
        return String.format("%s:%s", serviceName, serviceVersion);
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

