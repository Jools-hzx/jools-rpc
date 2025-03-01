package com.jools.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/30 9:47
 * @description: 本地服务注册器
 * 使用线程安全的 ConcurrentHashMap存储服务注册信息，
 * a. key 为服务名称
 * b. value 为服务的实现类
 * c. 之后可以根据要调用的服务名称获取到对应的实现类，使用反射进行方法调用
 */
public class LocalRegistry {

    /**
     * 注册信息存储类
     */
    private static final Map<String, Object> registry = new ConcurrentHashMap<>();

    /**
     * 注册服务
     */
    public static void register(String serviceName, Object instance) {
        if (serviceName == null || serviceName.isEmpty()) {
            serviceName = "default";
        }
        try {
            registry.put(serviceName, instance);
        } catch (Exception e) {
            throw new RuntimeException("Error -" + e.getMessage());
        }
    }

    /**
     * 获取服务
     */
    public static Object getService(String serviceName) {
        if (registry.isEmpty()) {
            throw new RuntimeException("No service available");
        }
        return registry.get(serviceName);
    }

    /**
     * 删除服务
     */
    public static void delete(String serviceName) {
        if (registry.isEmpty()) {
            throw new RuntimeException("No service available!");
        }
        registry.remove(serviceName);
    }
}














