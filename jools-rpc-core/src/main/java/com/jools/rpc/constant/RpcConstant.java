package com.jools.rpc.constant;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/1 11:28
 * @description: TODO
 */
public interface RpcConstant {

    /**
     * 默认配置文件加载前缀
     */
    String DEFAULT_CONFIG_PREFIX = "rpc";

    /**
     * 默认服务版本号
     */
    String DEFAULT_SERVICE_VERSION = "1.0";

    /**
     * properties 配置文件后缀
     */
    String PROP_CONFIG_SUFFIX = ".properties";

    /**
     * yaml 配置文件后缀
     */
    String YAML_CONFIG_SUFFIX = ".yaml;.yml";

    /**
     * 默认RPC响应等待时间，单位：秒
     */
    int DEFAULT_RESP_WAIT_DURATION = 10;
}
