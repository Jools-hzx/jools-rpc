package com.jools.exp.consumer.api;

import com.jools.exp.common.model.User;
import com.jools.exp.common.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/12/11 13:50
 * @description: TODO
 */
@Slf4j
public class UserServiceMock implements UserService {
    @Override
    public User getUser(User user) {
        log.warn("Fail Back, using {} as local mock service", this.getClass().getSimpleName());
        return new User("Fail Back - Mock User Service!!!");
    }
}
