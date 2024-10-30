package com.jools.exp.provider;

import com.jools.exp.common.model.User;
import com.jools.exp.common.service.UserService;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/10/29 23:08
 * @description: 用户服务实现类
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名:" + user.getName());
        return user;
    }
}
