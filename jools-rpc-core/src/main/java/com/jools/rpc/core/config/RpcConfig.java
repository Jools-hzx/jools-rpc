package com.jools.rpc.core.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/1 11:09
 * @description: Rpc 框架配置类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcConfig {

    /**
     * 服务名称
     */
    private String name = "jools-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口
     */
    private String serverPort = "8888";
}
