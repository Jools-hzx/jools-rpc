package com.jools.rpc.fault.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 扫描所有本地伪装服务的包
 *
 * @author Jools He
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MockScanPackage {

    /**
     * 扫描的包名
     *
     * @return
     */
    String basePackage() default "com.jools.exp.consumer.api";
}
