package com.jools.rpc.proxy;

import com.github.javafaker.Faker;
import com.jools.rpc.config.RpcConfig;
import com.jools.rpc.model.RpcRequest;
import com.jools.rpc.model.RpcResponse;
import com.jools.rpc.protoc.UserOuterClass;
import com.jools.rpc.server.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/3 23:09
 * @description: TODO
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    public static final Faker faker = new Faker(new Locale("zh-CN"));

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //根据方法发返回值，生成特定的默认值对象返回
        Class<?> returnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(returnType);
    }

    /**
     * 基于返回类型返回 Mock 值
     *
     * @param returnType
     * @return
     */
    public Object getDefaultObject(Class<?> returnType) {
        //返回基本类型
        if (returnType.isPrimitive()) {
            if (returnType == boolean.class) {
                return false;
            } else if (returnType == short.class) {
                return (short) 0;
            } else if (returnType == int.class) {
                return 0;
            } else if (returnType == long.class) {
                return 0L;
            }
        }

        //RpcRequest/Response - Model
        if (returnType == RpcRequest.class || returnType == RpcResponse.class) {
            return getDefaultRpcReqResp(returnType);
        }

        //Rpc - Config
        if (returnType == RpcConfig.class) {
            return getDefaultRpcConfig();
        }

        //Http Server
        if (returnType.isAssignableFrom(HttpServer.class)) {
            return getDefaultHttpServer();
        }

        log.error("Receive unsupported return type:{}", returnType);
        //其他类型
        throw new RuntimeException("Unsupported return type:" + returnType);
    }

    private HttpServer getDefaultHttpServer() {
        return port -> System.out.println(
                faker.bothify("Default-HttpServer-##; Start on Port:" + port)
        );
    }

    private RpcConfig getDefaultRpcConfig() {
        RpcConfig rpcConfig = new RpcConfig();

        //faker属性值
        rpcConfig.setMock(true);
        rpcConfig.setName(faker.bothify("Default-RpcConfig-##"));
        rpcConfig.setServerHost(faker.internet().ipV4Address());
        rpcConfig.setServerPort(faker.numerify("####"));
        rpcConfig.setVersion(faker.bothify("v0.#"));

        return rpcConfig;
    }

    //返回 Request Response Model 对象
    private Object getDefaultRpcReqResp(Class<?> type) {
        if (type == RpcRequest.class) {
            RpcRequest request = new RpcRequest();

            //Faker 属性值
            request.setServiceName(faker.bothify("DefaultService-##"));
            request.setMethodName(faker.bothify("Default-Method-##"));
            request.setParams(new Object[]{"Default-Params-##"});
            request.setParamTypes(new Class<?>[]{faker.getClass()});

            return request;
        } else if (type == RpcResponse.class) {
            RpcResponse rpcResponse = new RpcResponse();

            //faker 属性值
            rpcResponse.setMsg(faker.bothify("Default-Message-??"));
            rpcResponse.setException(new RuntimeException());
            rpcResponse.setData(faker.getClass());
            rpcResponse.setDataType(faker.getClass());

            return rpcResponse;
        }
        return null;
    }
}
