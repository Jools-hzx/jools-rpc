package com.jools.rpc.utils;

import com.jools.rpc.config.RpcConfig;
import com.jools.rpc.constant.RpcConstant;
import org.junit.Test;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/1 11:30
 * @description: TODO
 */
public class TestLoadConfig {

    @Test
    public void testLoadConfig() {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        assert rpc != null;
        System.out.println(rpc.getName());
        System.out.println(rpc.getVersion());
        System.out.println(rpc.getServerHost());
        System.out.println(rpc.getServerPort());
    }
}
