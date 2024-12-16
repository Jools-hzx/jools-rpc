package com.jools.rpc.utils;

import com.jools.rpc.config.RpcConfig;
import com.jools.rpc.constant.RpcConstant;
import org.junit.Assert;
import org.junit.Test;

public class ConfigUtilsTest {

    @Test
    public void testLoadConfigByProp() {
        //测试，基于 .properties 加载
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX, "");
        System.out.println(rpcConfig);
        Assert.assertEquals(rpcConfig.getServerHost(), "localhost");
        Assert.assertEquals(rpcConfig.getServerPort(), "8888");
        Assert.assertEquals(rpcConfig.getVersion(), "2.0");
    }

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