package com.jools.exp.common.service;


import com.jools.exp.common.model.User;

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
    User getUser(User user);
}
