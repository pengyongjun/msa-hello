package com.amwalle.msahello.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;


/**
 * @program: msa-hello
 * @description: zookeeper操作实例
 * @author: pengyongjun
 * @create: 2021.06.26 19:03
 **/
public class ZookeeperDemo {
    private static Logger logger = LoggerFactory.getLogger(ZookeeperDemo.class);

    private static final String CONNECTION_STRING = "47.243.173.250:2181";
    private static final int SESSION_TIMEOUT = 20000;
    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        // 链接ZooKeeper
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTION_STRING, SESSION_TIMEOUT, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                latch.countDown();
            }
        });

        latch.await();

        // 获取ZooKeeper客户端对象
        logger.info("Connected: " + zooKeeper.toString());

        // 获取子节点: List<String> getChildren(String path，Watcher watcher)
        logger.info("node: " + zooKeeper.getChildren("/", null).get(0));

        // 创建节点: String create(String path，byte[] data，List<ACL> acl，CreateMode createMode)
        logger.info(zooKeeper.create("/foo1", "hello zookeeper from java".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));

        // 获取节点数据: byte[] getData(String path，Watcher watcher，Stat stat)
        logger.info(new String(zooKeeper.getData("/foo1", null, null)));

        // 以异步的方式获取节点： void getData(String path，Watcher watcher，DataCallback cb，Object ctx)
        zooKeeper.getData("/foo1", null, (rc, path, ctx, data, stat) -> {
            logger.info("异步方式获取数据： " + new String(data));
        }, null);

        // 更新节点数据： void setData(String path，byte[] data，int version，StatCallback cb，Object ctx)
        Stat stat = zooKeeper.setData("/foo1", "new value from java".getBytes(), -1);
        logger.info("更新结果： " + (stat != null));

        // 删除节点: void delete(String path，int version，VoidCallback cb，Object ctx)
        zooKeeper.delete("/foo1", -1);
    }
}
