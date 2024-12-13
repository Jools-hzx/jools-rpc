package com.jools.exp.consumer;

import com.jools.exp.common.model.User;
import com.jools.exp.common.service.UserService;
import com.jools.exp.consumer.api.UserServiceMock;
import com.jools.rpc.bootstrap.ConsumerBootstrap;
import com.jools.rpc.fault.mock.LocalServiceMockRegistry;
import com.jools.rpc.RpcApplication;
import com.jools.rpc.config.RegistryConfig;
import com.jools.rpc.fault.tolerant.ErrorTolerantKeys;
import com.jools.rpc.fault.tolerant.ErrorTolerantStrategy;
import com.jools.rpc.fault.tolerant.queue.FailBackMessageQueueFactory;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.proxy.ServiceProxyFactory;
import com.jools.rpc.serializer.Serializer;
import com.jools.rpc.serializer.SerializerFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;


/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/29 23:14
 * @description: 简易服务消费者示例
 */
@Slf4j
public class BasicConsumerExample {

    public static void main(String[] args) throws Exception {

        //服务消费者初始化: 注册中心类型 + 序列化器类型等配置
        ConsumerBootstrap.init();

        //查看配置 - 序列化器
        log.info("Consumer Serializer type:{}", RpcApplication.getRpcConfig().getSerializer());

        //查看配置 - 注册中心类型
        log.info("Consumer Registry cli type:{}", RpcApplication.getRpcConfig().getRegistryConfig().getRegistryType());

        //获取 - 动态代理对象，实现 RPC 透明调用
        UserService service = ServiceProxyFactory.getProxy(UserService.class);

        //RPC调用服务名
        String serviceName = UserService.class.getName();

        //优化 - 容错机制 Fail Back 本地伪装
        LocalServiceMockRegistry.register(serviceName, UserServiceMock.class);

        User user = new User();
        user.setName("Jools Wakoo");

        //调用 - 连续调用四次; 预期结果: 第一次直接查询服务，后续查询缓存
        int cnt = 4;
        Object result = null;
        while (cnt-- > 0) {
            try {
                result = service.getUser(user);
            } catch (Exception e) {
                if (result == null) {
                    log.error("No serivceMetaInfo found for service:{} from service registry and Consumer Cache", serviceName);
                    //如果容错策略为 Fail Back
                    String consumerErrorTolerantKey = RpcApplication.getRpcConfig().getErrorTolerantStrategyKeys();
                    log.warn("Consumer using {} strategy for Error tolerant", consumerErrorTolerantKey);
                    if (consumerErrorTolerantKey.equals(ErrorTolerantKeys.FAIL_BACK)) {
                        //执行调用本地伪装服务
                        RpcRequest failBackRequest = FailBackMessageQueueFactory.getMessageQueue(ErrorTolerantKeys.FAIL_BACK).poll();
                        Class<?> cls = LocalServiceMockRegistry.getService(failBackRequest.getServiceName());
                        log.warn("Using Fail Back strategy for service:{}", failBackRequest.getServiceName());
                        Object obj = cls.getDeclaredConstructor().newInstance();
                        Method method = cls.getDeclaredMethod(failBackRequest.getMethodName(), failBackRequest.getParamTypes());
                        result = method.invoke(obj, failBackRequest.getParams());
                    }
                }
            }
            System.out.println(result == null ? "user == NULL !!!" : "User Name is: " + ((User) result).getName());
        }

        //测试 - Mock 服务
//        short shortNum = service.getShortNum();
//        System.out.println(shortNum != 1);
//        System.out.println(shortNum);   //0
    }
}
