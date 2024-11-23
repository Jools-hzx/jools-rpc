package com.jools.rpc.zkcli;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/23 15:26
 * @description: TODO
 */
public class JavaZkClient {

    private static final String ZOOKEEPER_SERVER = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;
    private static String path = "/example";
    private ZooKeeper zooKeeper;

    @Before
    public void init() throws IOException {

        zooKeeper = new ZooKeeper(
                ZOOKEEPER_SERVER,
                SESSION_TIMEOUT,
                watchedEvent -> System.out.println("ZooKeeper connect succeed! -- address:" + ZOOKEEPER_SERVER)
        );
    }

    @Test
    public void registerNode() throws InterruptedException, KeeperException {
        byte[] data = "Hello, ZooKeeper!".getBytes();

        String result = zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("Register Node:" + result);
    }

    @Test
    public void readNode() throws InterruptedException, KeeperException {
        byte[] readData = zooKeeper.getData(path, false, null);
        String dataStr = new String(readData);
        System.out.println("Read data:" + dataStr);
    }

    @Test
    public void testUpdate() throws InterruptedException, KeeperException {
        Stat stat = zooKeeper.setData(path, "Update data".getBytes(), -1);
        System.out.println("Updated data:" + stat);
    }

    @After
    public void closConnection() {
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
