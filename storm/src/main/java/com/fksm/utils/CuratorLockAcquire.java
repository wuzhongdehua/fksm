package com.fksm.utils;

import com.netflix.curator.RetryPolicy;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取分布式锁
 */
public class CuratorLockAcquire  {
    private static final Logger LOGGER = LoggerFactory.getLogger(CuratorLockAcquire.class);

    private CuratorFramework client = null;

    public CuratorLockAcquire() {}

    public synchronized CuratorFramework getCuratorClient() {
        if (client == null) {
            try {
                String zkHostPort = ConfigPropertieUtil.getInstance().getString("curator.lock.zk.connect");
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                client = CuratorFrameworkFactory.newClient(zkHostPort, retryPolicy);
                client.start();
            } catch (Exception e) {
                LOGGER.error("Get curatorClient error.", e);
            }
        }

        return client;
    }
}
