package com.jools.rpc.core;

import com.jools.rpc.core.config.RpcConfig;
import com.jools.rpc.core.constant.RpcConstant;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class RpcApplicationTest {

    @Test
    public void testGetRpcConfigByYml() throws FileNotFoundException {
        RpcConfig rpcConfig = RpcApplication.getRpcConfig(".yml");
        System.out.println(rpcConfig);
    }

    @Test
    public void testGetRpcConfigByYaml() throws FileNotFoundException {
        RpcConfig rpcConfig = RpcApplication.getRpcConfig(".yaml");
        System.out.println(rpcConfig);
    }

    @Test
    public void testGetRpcConfigByProps() throws FileNotFoundException {
        RpcConfig config = RpcApplication.getRpcConfig(".properties");
        System.out.println(config);
    }

    @Test
    public void testGetRpcConfigDefault() throws FileNotFoundException {

        RpcConfig config = RpcApplication.getRpcConfig();
        System.out.println("默认加载配置" + config);
    }

    @Test
    public void testGetRpcConfig() {

        RpcConfig config = RpcApplication.getRpcConfig();

        System.out.println(config.getName());
        System.out.println(config.getVersion());
        System.out.println(config.getServerPort());
        System.out.println(config.getServerHost());

        RpcConfig newConfig = RpcApplication.getRpcConfig();
        assert config == newConfig;
        Assert.assertEquals(config, newConfig);
    }
}