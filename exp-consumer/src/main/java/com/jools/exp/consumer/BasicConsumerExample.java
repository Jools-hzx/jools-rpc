package com.jools.exp.consumer;

import cn.hutool.core.util.ObjectUtil;
import com.google.errorprone.annotations.Var;
import com.jools.exp.common.model.User;
import com.jools.exp.common.service.UserService;
import com.jools.exp.consumer.api.UserServiceMock;
import com.jools.rpc.bootstrap.ConsumerBootstrap;
import com.jools.rpc.fault.annotation.JRpcFailBack;
import com.jools.rpc.fault.annotation.MockScanPackage;
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
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;


/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/29 23:14
 * @description: 简易服务消费者示例
 */
@Slf4j
@MockScanPackage(basePackage = "com.jools.exp.consumer.api")
public class BasicConsumerExample {

    @JRpcFailBack(mockServiceName = "com.jools.exp.consumer.api.UserServiceMockTwo", mock = true)
    private static UserService userService;

    static {

        //服务消费者初始化: 注册中心类型 + 序列化器类型等配置
        ConsumerBootstrap.init();
        try {
            //初始化本地伪装
            initMockService();
            //绑定本地伪装
            bindServiceProxy();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //扫描指定包下的所有 Mock 服务; 按照实现接口全类名(key) + 实现类全类名(value) 注册
    private static void initMockService() {
        boolean isPresent = BasicConsumerExample.class.isAnnotationPresent(MockScanPackage.class);
        if (isPresent) {
            //获取扫描包路径
            MockScanPackage mockScanPackage = BasicConsumerExample.class.getAnnotation(MockScanPackage.class);

            if (StringUtils.isBlank(mockScanPackage.basePackage())) {
                return;
            }

            //通过包名得到包的路径; 需要将 . 转化为 /
            String basePackage = mockScanPackage.basePackage();
            String packPath = BasicConsumerExample.class.getClassLoader().getResource(basePackage.replaceAll("\\.", "/")).getPath();
            //扫描包下的所有文件
            File file = new File(packPath);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    String fileName = f.getName();
                    //拼接成全类名: 包名 + 文件名(提出 .class 子串)
                    String clsName = basePackage + "." + fileName.substring(0, fileName.indexOf("."));
                    //获取其实现到的接口作为服务类名
                    Class<?> cls;
                    try {
                        cls = Class.forName(clsName);
                        //TODO: If instance implement multi interfaces
                        Class<?>[] interfaces = cls.getInterfaces();
                        //默认以第一个实现的接口名注册
                        String serviceName = interfaces[0].getName();

                        //注册:服务名 - 本地伪装实现类名 [均为全类名]
                        LocalServiceMockRegistry.register(serviceName, cls);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    //如果存在@JRpcFailBack 注解; 需要开启 Fail Back 策略
    private static void bindServiceProxy() throws Exception, InstantiationException, IllegalAccessException {
        //遍历所有 Field 字段
        Field[] declaredFields = BasicConsumerExample.class.getDeclaredFields();

        //扫描是否启用 JRpcFailBack 注解
        for (Field declaredField : declaredFields) {
            //如果开启了本地伪装，需要设置重试策略为 FAIL_BACK
            if (declaredField.isAnnotationPresent(JRpcFailBack.class) && declaredField.getAnnotation(JRpcFailBack.class).mock()) {
                String tolerantStrategyKeys = RpcApplication.getRpcConfig().getErrorTolerantStrategyKeys();
                if (!tolerantStrategyKeys.equals(ErrorTolerantKeys.FAIL_BACK)) {
                    throw new RuntimeException("Error Tolerant Strategy need to set to FAIL BACK to enable Local Mock Serice");
                }
                JRpcFailBack rpcFailBack = declaredField.getAnnotation(JRpcFailBack.class);
                String serviceName = declaredField.getType().getName();
                //是否指定 serviceName 字段
                if (!StringUtils.isBlank(rpcFailBack.serviceName())) {
                    serviceName = rpcFailBack.serviceName();
                }
                //若开启本地Mock, 必须注册
                List<Class<?>> localMockServices = LocalServiceMockRegistry.getService(serviceName);
                if (ObjectUtil.isNull(localMockServices) || ObjectUtil.isEmpty(localMockServices)) {
                    throw new RuntimeException("No local service register for service name:" + serviceName);
                }
                //是否指定 mockServiceName
                String mockServiceName = "";
                if (!StringUtils.isBlank(rpcFailBack.mockServiceName())) {
                    mockServiceName = rpcFailBack.mockServiceName();
                    Class<?> mockServiceCls = Class.forName(mockServiceName);
                    //绑定唯一伪装服务
                    LocalServiceMockRegistry.bindMockService(serviceName, mockServiceCls);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {

        //查看配置 - 序列化器
        log.info("Consumer Serializer type:{}", RpcApplication.getRpcConfig().getSerializer());

        //查看配置 - 注册中心类型
        log.info("Consumer Registry cli type:{}", RpcApplication.getRpcConfig().getRegistryConfig().getRegistryType());

        //获取 - 动态代理对象，实现 RPC 透明调用
        UserService service = ServiceProxyFactory.getProxy(UserService.class);

        //RPC调用服务名
        String serviceName = UserService.class.getName();

        //优化 - 基于注解驱动
//        LocalServiceMockRegistry.register(serviceName, UserServiceMock.class);

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
                        Class<?> cls = LocalServiceMockRegistry.getService(failBackRequest.getServiceName()).get(0);
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
