package com.jools.exp.consumer;

import com.jools.exp.common.model.User;
import com.jools.exp.common.service.UserService;
import com.jools.joolsrpc.proxy.ServiceProxyFactory;

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

        //动态代理
        UserService service = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("Jools Wakoo");

        //调用
        User newUser = service.getUser(user);
        if (newUser != null) {
            System.out.println("调用成功!!!" + newUser.getName());
        } else {
            System.out.println("user == NULL !!!");
        }
    }
}
