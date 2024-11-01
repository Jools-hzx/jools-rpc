package com.jools.rpc.core.utils;

import com.jools.rpc.core.config.RpcConfig;
import com.jools.rpc.core.constant.RpcConstant;
import org.junit.Test;

import java.util.Random;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/1 18:51
 * @description: TODO
 */
public class TestFileModify {

    @Test
    public void monitorTest() {
        ConfigUtils.modifyConfig(
                "application.properties",
                "rpc.name",
                "New name" + new Random().nextInt(100));

        RpcConfig newConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX, "");

        //查询修改后的值
        System.out.println(newConfig.getName());
    }
}
