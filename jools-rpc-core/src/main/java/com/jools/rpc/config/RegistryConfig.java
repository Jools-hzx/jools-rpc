package com.jools.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/6 15:13
 * @description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistryConfig {

    /**
     * 注册中心配置类别
     */
    private RegistryType registryType = RegistryType.ETCD;

    /**
     * 注册中心地址
     */
    private String address = "http://localhost:2380";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间，单位 ms
     */
    private Long timeout = 10000L;
}
