/**
 * @(#)LeaderLatch.java, 2015年10月21日. Copyright 2015 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.zkcurator;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;

import dictinsight.utils.DistributedServerConst;


/**
 * @author liujg
 */
public class CuratorLeaderLatch {

    CuratorFramework client;

    LeaderLatch latch;

    protected static final Log logger = LogFactory
            .getLog(CuratorLeaderLatch.class);
    
    public void leader() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(DistributedServerConst.CONNECTSTRING).sessionTimeoutMs(2000)
                .connectionTimeoutMs(10000).retryPolicy(retryPolicy)
                .namespace(DistributedServerConst.NAMESPACE).build();
        client.start();
        System.out.println("connect zkstring="+DistributedServerConst.CONNECTSTRING);

        latch = new LeaderLatch(client, DistributedServerConst.LEADER);
        latch.start();
        latch.await();
        logger.info("select as leader!");
    }

    public LeaderLatch getLatch(){
        return this.latch;    
    }
    
    public void close() {
        try {
            latch.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        client.close();
    }
}
