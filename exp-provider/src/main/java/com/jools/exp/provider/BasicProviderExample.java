package com.jools.exp.provider;

import com.jools.joolsrpc.server.HttpServer;
import com.jools.joolsrpc.server.impl.VertxHttpServer;
import com.jools.joolsrpc.registry.LocalRegistry;
import com.jools.rpc.RpcApplication;
import com.jools.rpc.config.RpcConfigListener;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerFactory;

import java.io.FileNotFoundException;

/**
 * @author Jools He
 * @version 1.0
 * @description: 简易服务提供者示例
 */
public class BasicProviderExample {

    public static void main(String[] args) throws FileNotFoundException {

        //测试获取 - 配置文件内配置的相应的实现类
        Serializer instance = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        System.out.println(instance.getClass());

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
