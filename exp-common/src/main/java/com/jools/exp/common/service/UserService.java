package com.jools.exp.common.service;


import com.jools.exp.common.model.User;
import com.jools.rpc.proxy.annotation.Group;

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
