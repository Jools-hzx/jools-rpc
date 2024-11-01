package com.jools.rpc.core.utils;

import cn.hutool.setting.dialect.Props;
import com.jools.rpc.core.config.RpcConfig;
import com.jools.rpc.core.constant.RpcConstant;
import com.jools.rpc.core.utils.ConfigUtils;
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
