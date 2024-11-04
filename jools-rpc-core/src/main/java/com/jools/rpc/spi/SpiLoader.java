package com.jools.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.jools.rpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/4 15:37
 * @description: SPI 机制加载器
 */
@Slf4j
public class SpiLoader {


    /**
     * 接口名 --> (键值 k [比如:jdk] + v 实现类类名[比如: JdkSerializer])
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存 (避免重复)，key 为 类路径 => 对象实例，单例模式
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * SPI 默认读取的系统目录
     */
    private static final String SPI_RPC_SYSTEM_DIR = "META-INF/rpc/system/";

    /**
     * SPI 机制读取自定义配置的目录
     */
    private static final String SPI_RPC_CUSTOM_DIR = "META-INF/rpc/custom/";

    /**
     * 动态加载类列表
     */
    private static final List<Class<?>> loadClassesList = List.of(Serializer.class);

    /**
     * SPI读取目录顺序，默认目录优先，其次是自定义目录
     */
    private static final String[] SPI_LOAD_DIR = new String[]{SPI_RPC_SYSTEM_DIR, SPI_RPC_CUSTOM_DIR};


    /**
     * 基于接口类名和其实现类 key 标识
     * 使用反射返回实例
     * 刷新内存
     *
     * @param tClass 接口 Class 类
     * @param key    键值，标识不同实现类
     */
    @SuppressWarnings("all")
    public static <T> T getInstance(Class<T> tClass, String key) {

        log.info("尝试加载接口:{} 的 {} 类型实例", tClass.getSimpleName(), key);
        String name = tClass.getName();

        if (StrUtil.isBlank(key) || StrUtil.isBlank(tClass.getName())) {
            log.error("Can not get instance from empty params");
            return null;
        }

        //基于 interface 类名查询其所有 keu => 实现类类名 记录
        Map<String, Class<?>> keyClassMap = loaderMap.get(name);

        //检查 loaderMap 是否为空
        if (keyClassMap.isEmpty()) {
            throw new RuntimeException(
                    String.format("Current loaderMap is Empty, not {%s} records exist.", tClass.getName()));
        }

        //检查 loaderMap 内该 tClass 是否存在
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("Class:{} doesn't have key:{%s} impl instance", name, key));
        }

        //获取到要加载的实现类型
        Class<?> implClass = keyClassMap.get(key);
        //从实例缓存中加载指定类型的实例
        String implClassName = implClass.getName();

        //检查缓存
        if (!instanceCache.containsKey(implClassName)) {
            //如果缓存未命中，载入缓存，保证单例
            log.info("Not impl class name:{} load it",
                    implClassName);
            try {
                instanceCache.put(implClassName, implClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //缓存命中或者加载入缓存
        return (T) instanceCache.get(implClassName);
    }


    /**
     * 基于接口名，扫描所有 SPI 定义目录
     * 将 key 标识及其所有实现类封装成 map
     * 加入到 loaderMap
     *
     * @param loadClass 该接口类型的 Class 类
     * @return 接口定义 key 标识 => 所有映射的实现类
     * Map[定义的key, key 对应的实现类])
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("加载类型 :{}", loadClass.getName());

        String name = loadClass.getName();
        Map<String, Class<?>> loadClasses = new HashMap<>(10);
        //扫描所有目录
        for (String scanDir : SPI_LOAD_DIR) {
            //Hutool 资源读取类
            List<URL> resources = ResourceUtil.getResources(scanDir + name);
            //读取每个资源
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        //读取每一行基于 = 分割
                        //k 为 该接口类型定义键值
                        String[] strArr = line.split("=");
                        //= 左侧为该接口类型定义的所有键
                        //= 右侧为该键的实现类 Class
                        if (strArr.length > 1) {
                            String key = strArr[0];
                            String className = strArr[1];
                            loadClasses.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("SPI read and load resource error: {%s}", e);
                }
            }
        }
        //加入到 类名 => (键值 -> 实现类) 集合
        loaderMap.put(name, loadClasses);
        return loadClasses;
    }


    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("利用 SPI 加载所有类型");
        for (Class<?> cls : loadClassesList) {
            load(cls);
        }
    }

}
