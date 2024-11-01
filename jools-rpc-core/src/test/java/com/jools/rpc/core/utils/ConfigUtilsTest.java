package com.jools.rpc.core.utils;

import com.jools.rpc.core.config.RpcConfig;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigUtilsTest {

    @Test
    public void loadConfigYaml() {
        RpcConfig rpcConfig = ConfigUtils.loadConfigYaml(RpcConfig.class, ".yml");
        System.out.println(rpcConfig);
        System.out.println(rpcConfig.getName());
        System.out.println(rpcConfig.getVersion());
        System.out.println(rpcConfig.getServerHost());
        System.out.println(rpcConfig.getServerPort());

        System.out.println("--- .yaml 格式后缀 ---");
        rpcConfig = ConfigUtils.loadConfigYaml(RpcConfig.class, ".yaml");
        System.out.println(rpcConfig);
        System.out.println(rpcConfig.getName());
        System.out.println(rpcConfig.getVersion());
        System.out.println(rpcConfig.getServerHost());
        System.out.println(rpcConfig.getServerPort());
    }
}