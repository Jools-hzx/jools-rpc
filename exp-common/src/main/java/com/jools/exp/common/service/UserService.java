package com.jools.exp.common.service;


import com.jools.exp.common.model.User;
import com.jools.rpc.fault.retry.RetryStrategyKeys;
import com.jools.rpc.fault.tolerant.ErrorTolerantKeys;
import com.jools.rpc.loadbalancer.LoadBalancerKeys;
import com.jools.rpc.proxy.annotation.ErrorTolerant;
import com.jools.rpc.proxy.annotation.Group;
import com.jools.rpc.proxy.annotation.LoadBalance;
import com.jools.rpc.proxy.annotation.Retry;

/**
 * 用户服务
 *
 * @author Jools He
 */
public interface UserService {

    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    @Group("user")
    @ErrorTolerant(strategy = ErrorTolerantKeys.FAIL_BACK)
    @Retry(strategy = RetryStrategyKeys.fixInterval)
    @LoadBalance(strategy = LoadBalancerKeys.CONSISTENT_HASH)
    User getUser(User user);

    /**
     * 新方法 - 默认返回 1
     *
     * @return 默认返回 1 (short 类型)
     */
    default short getShortNum() {
        return 1;
    }
}
