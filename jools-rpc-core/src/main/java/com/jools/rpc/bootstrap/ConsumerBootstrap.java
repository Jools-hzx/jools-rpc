package com.jools.rpc.bootstrap;

import com.jools.rpc.RpcApplication;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/13 19:37
 * @description: 服务消费者启动类
 */
public class ConsumerBootstrap {


    /**
     * 初始化
     */
    public static void init() {
        //初始化配置
        RpcApplication.init();
    }
}


