package com.jools.rpc.mockito;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/15 17:07
 * @description: TODO
 */
public class DemoUserService {

    DemoUserDao demoUserDao;

    public DemoUserService(DemoUserDao demoUserDao) {
        this.demoUserDao = demoUserDao;
    }

    public String getUserName() {
        return this.demoUserDao.getName();
    }
}
