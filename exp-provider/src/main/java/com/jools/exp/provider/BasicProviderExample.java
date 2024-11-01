package com.jools.exp.provider;

import cn.hutool.core.io.watch.WatchUtil;
import com.jools.joolsrpc.server.HttpServer;
import com.jools.joolsrpc.server.impl.VertxHttpServer;
import com.jools.joolsrpc.registry.LocalRegistry;
import com.jools.rpc.core.RpcApplication;
import com.jools.rpc.core.config.RpcConfigListener;
import com.jools.rpc.core.utils.ConfigUtils;

import java.io.FileNotFoundException;

/**
 * @author Jools He
 * @version 1.0
 * @description: 简易服务提供者示例
 */
public class BasicProviderExample {

    public static void main(String[] args) throws FileNotFoundException {

        //Rpc框架初始化
        RpcApplication.init(".properties");
        //监听配置文件变换
        RpcConfigListener.init();

        /*
            1.0 版本
        */
        //注册服务
        LocalRegistry.register("UserService", UserServiceImpl.class);

        //提供服务
        HttpServer vertxServer = new VertxHttpServer();
        vertxServer.doStart(8888);
    }
}
