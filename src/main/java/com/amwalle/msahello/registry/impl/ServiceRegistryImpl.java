package com.amwalle.msahello.registry.impl;

import com.amwalle.msahello.registry.ServiceRegistry;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @program: msa-hello
 * @description: 注册服务信息的具体实现
 * @author: pengyongjun
 * @create: 2021.06.27 10:47
 **/
@Component
public class ServiceRegistryImpl implements ServiceRegistry, Watcher {
    private static Logger logger = LoggerFactory.getLogger(ServiceRegistryImpl.class);

    private static CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zooKeeper;
    private static final String REGISTRY_PATH = "/registry";
    private static final int SESSION_TIMEOUT = 5000;

    public ServiceRegistryImpl(@Value("${registry.zookeeper.servers}") String zooKeeperServers) {
        try {
            zooKeeper = new ZooKeeper(zooKeeperServers, SESSION_TIMEOUT, this);
            latch.await();
            logger.info("Connected to ZooKeeper.");
        } catch (IOException | InterruptedException e) {
            logger.error("Create ZooKeeper client failed: " + e.getMessage());
        }
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        try {
            // 创建根节点（持久节点）
            String registryPath = REGISTRY_PATH;
            if (zooKeeper.exists(registryPath, false) == null) {
                zooKeeper.create(registryPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info("Create registry node {}", registryPath);
            }

            // 创建服务节点（持久节点）
            String servicePath = REGISTRY_PATH + "/" + serviceName;
            if (zooKeeper.exists(servicePath, false) == null) {
                zooKeeper.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info("Create service node {}", servicePath);
            }

            // 创建地址节点（临时顺序节点）
            String addressPath = servicePath + "/address-";
            if (zooKeeper.exists(addressPath, false) == null) {
                String addressNode = zooKeeper.create(addressPath, serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                logger.info("Create address node {} => {}", addressNode, serviceAddress);
            }
        } catch (KeeperException | InterruptedException e) {
            logger.error("Create node failed, " + e);
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            latch.countDown();
        }
    }
}
