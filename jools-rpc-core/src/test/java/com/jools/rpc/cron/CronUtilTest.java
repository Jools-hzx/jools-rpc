package com.jools.rpc.cron;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import org.junit.Test;

import java.util.TreeSet;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/23 11:48
 * @description: TODO
 */
public class CronUtilTest {

    @Test
    public void testDynamicCron() throws InterruptedException {
        CronUtil.schedule("*/1 * * * * *", new Task() {
            @Override
            public void execute() {
                System.out.println("Task executing....");
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start(true);
        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
        }
    }

    @Test
    public void testCrontUtil() throws InterruptedException {
        //设置支持秒级定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start(true);

        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            System.out.println(i);
        }
    }
}
