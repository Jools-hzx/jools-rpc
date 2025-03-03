package com.jools.rpc.proxy.annotation;


import com.jools.rpc.fault.tolerant.ErrorTolerantKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jools He
 * @description: 容错注解
 * 用于方法上，标识该方法需要进行容错处理
 * 目前支持的容错策略：
 * 1. 快速失败 Fail Fast
 * 2. 熔断 Fail Safe
 * 3. 降级 Fail Back
 * 4. 故障迁移 Fail Over
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorTolerant {

    String strategy() default ErrorTolerantKeys.FAIL_FAST;
}
