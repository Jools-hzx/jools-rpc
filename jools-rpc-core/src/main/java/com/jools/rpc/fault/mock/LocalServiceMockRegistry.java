package com.jools.rpc.fault.mock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/11 13:56
 * @description: 容错 - Fail Back 本地容错
 */
public class LocalServiceMockRegistry {

    /**
     * mock 本地服务注册存储结合
     */
    private static final Map<String, Class<?>> LOCAL_MOCK_SERVICE_REGISTRY = new ConcurrentHashMap<>();

    /**
     * 注册 mock 服务
     */
    public static void register(String mockServiceName, Class<?> serviceCls) {
        if (mockServiceName == null || mockServiceName.isEmpty()) {
            mockServiceName = "default";
        }
        try {
            LOCAL_MOCK_SERVICE_REGISTRY.put(mockServiceName, serviceCls);
        } catch (Exception e) {
            throw new RuntimeException("Error -" + e.getMessage());
        }
    }

    /**
     * 获取 mock 服务
     */
    public static Class<?> getService(String mockServiceName) {
        if (LOCAL_MOCK_SERVICE_REGISTRY.isEmpty()) {
            throw new RuntimeException("No service available");
        }
        return LOCAL_MOCK_SERVICE_REGISTRY.get(mockServiceName);
    }

    /**
     * 删除 mock服务
     */
    public static void delete(String mockServiceName) {
        if (LOCAL_MOCK_SERVICE_REGISTRY.isEmpty()) {
            throw new RuntimeException("No service available!");
        }
        LOCAL_MOCK_SERVICE_REGISTRY.remove(mockServiceName);
    }
}
