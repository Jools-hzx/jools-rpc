package com.jools.rpc.mockito;

import java.util.Random;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/15 17:07
 * @description: TODO
 */
public class DemoUserDao {

    public String getName() {
        return "Elon Mask-" + new Random().nextInt(10);
    }
}
