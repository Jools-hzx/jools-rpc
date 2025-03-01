package com.jools.rpc.fault.mock;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/11 13:56
 * @description: 容错 - Fail Back 本地容错
 */
@Slf4j
public class LocalServiceMockRegistry {

    /**
     * 所有服务类的本地服务伪装服务
     */
    private static Map<String, Set<Class<?>>> mockServices = new ConcurrentHashMap<>();

    /**
     * 绑定服务与注解注册的本地伪装服务
     *
     * @param mockServiceName    服务类名(全类名)
     * @param mockServiceImplcls 本地伪装服务类(全类名)
     */
    public static void bindMockService(String mockServiceName, Class<?> mockServiceImplcls) {
        if (ObjectUtil.isNull(mockServiceImplcls) || StringUtils.isBlank(mockServiceName)) {
            throw new RuntimeException("Service name can not be NULL");
        }

        if (ObjectUtil.isNull(mockServices.get(mockServiceName))) {
            log.error("No local service mock found for {}", mockServiceImplcls);
            return;
        }
        //覆盖;绑定 mockServiceName 和注册的本地伪装全类名
        mockServices.computeIfAbsent(mockServiceName, k -> new CopyOnWriteArraySet<>()).add(mockServiceImplcls);
    }

    /**
     * 注册 mock 服务
     */
    public static void register(String mockServiceName, Class<?> serviceCls) {
        if (mockServiceName == null || mockServiceName.isEmpty()) {
            mockServiceName = "default";
        }
        try {
            //为空，初始化
            if (ObjectUtil.isNull(mockServices.get(mockServiceName))) {
                mockServices.put(mockServiceName, new CopyOnWriteArraySet<>());
            }
            mockServices.get(mockServiceName).add(serviceCls);
        } catch (Exception e) {
            throw new RuntimeException("Error -" + e.getMessage());
        }
    }

    /**
     * 获取 mock 服务
     */
    public static Class<?> getService(String mockServiceName) {
        if (mockServices.isEmpty()) {
            throw new RuntimeException("No local mock service available");
        }
        log.debug("Using local service, service name :{}", mockServiceName);
        return mockServices.get(mockServiceName).stream().findFirst().orElse(null);
    }

    /**
     * 删除 mock服务
     */
    public static void delete(String mockServiceName) {
        if (mockServices.isEmpty()) {
            throw new RuntimeException("No service available!");
        }
        mockServices.remove(mockServiceName);
    }
}
