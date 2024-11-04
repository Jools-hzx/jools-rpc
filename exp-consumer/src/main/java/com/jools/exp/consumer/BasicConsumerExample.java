package com.jools.exp.consumer;

import com.jools.exp.common.model.User;
import com.jools.exp.common.service.UserService;
import com.jools.rpc.RpcApplication;
import com.jools.rpc.proxy.ServiceProxyFactory;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerFactory;
import com.jools.rpc.serializer.SerializerKeys;
import com.jools.rpc.serializer.impl.HessianSerializer;
import com.jools.rpc.spi.SpiLoader;


/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/29 23:14
 * @description: 简易服务消费者示例
 */
public class BasicConsumerExample {

    public static void main(String[] args) {

        //静态代理
//        UserService service = new UserServiceStaticProxy();

        //获取 - 动态代理
        UserService service = ServiceProxyFactory.getProxy(UserService.class);

        //测试获取 - 配置文件内配置的相应的实现类
        Serializer instance = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        System.out.println(instance.getClass());

        User user = new User();
        user.setName("Jools Wakoo");

        //调用
        User newUser = service.getUser(user);
        if (newUser != null) {
            System.out.println("调用成功!!!" + newUser.getName());
        } else {
            System.out.println("user == NULL !!!");
        }

        //测试 - Mock 服务
//        short shortNum = service.getShortNum();
//        System.out.println(shortNum != 1);
//        System.out.println(shortNum);   //0
    }
}
