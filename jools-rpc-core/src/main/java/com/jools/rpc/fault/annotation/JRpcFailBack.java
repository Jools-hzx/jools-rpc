package com.jools.rpc.fault.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Deque;

/**
 * 注解驱动 —— Fail Back 策略实现本地伪装
 *
 * @author Jools He
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JRpcFailBack {

    /**
     * 服务名 - 全类名
     * 默认为当前字段的类型即为服务名
     *
     * @return
     */
    String serviceName() default "";

    /**
     * 指定本地伪装 - 全类名
     *
     * @return
     */
    String mockServiceName() default "";

    /**
     * 本地伪装的服务实现类 - 全类名
     * true 表示启动本地伪装
     *
     * @return
     */
    boolean mock() default true;
}
