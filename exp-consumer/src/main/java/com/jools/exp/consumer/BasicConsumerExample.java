package com.jools.exp.consumer;

import com.jools.exp.common.model.User;
import com.jools.exp.common.service.UserService;
import com.jools.rpc.RpcApplication;
import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.proxy.ServiceProxyFactory;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerFactory;
import com.jools.rpc.serializer.SerializerKeys;
import com.jools.rpc.serializer.impl.HessianSerializer;
import com.jools.rpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/29 23:14
 * @description: 简易服务消费者示例
 */
@Slf4j
public class BasicConsumerExample {

    public static void main(String[] args) {

        //静态代理
//        UserService service = new UserServiceStaticProxy();

        //获取 - 动态代理
        UserService service = ServiceProxyFactory.getProxy(UserService.class);

        /*
          序列化器版本 2.0 - 支持多种序列化器，基于配置切换
         */
        Serializer instance = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        log.info("Consumer Serializer type:{}", instance.getClass());

        /*
         版本 3.0 - 支持切换注册中心 [Etcd + ZooKeeper + Redis]
         */
        RegistryConfig registryConfig = RpcApplication.getRpcConfig().getRegistryConfig();
        String registryType = registryConfig.getRegistryType();
        log.info("Consumer Registry cli type:{}", registryType);

        User user = new User();
        user.setName("Jools Wakoo");

        //调用 - 连续调用三次; 预期结果: 第一次直接查询服务，第二、三次查询缓存
        int cnt = 4;
        User result = null;
        while (cnt-- > 0) {
            try {
                result = service.getUser(user);
                System.out.println(user == null ? "user == NULL !!!" : "User Name is: " + user.getName());
            } catch (ClassCastException e) {
                log.error("No serivceMetaInfo found for service:{}; Using default service: {}", service.getClass().getSimpleName());
            }
        }

        //测试 - Mock 服务
//        short shortNum = service.getShortNum();
//        System.out.println(shortNum != 1);
//        System.out.println(shortNum);   //0
    }
}
