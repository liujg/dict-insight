/**
 * @(#)PathCacheService.java, 2015年10月9日. Copyright 2015 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.zkcurator;

import java.util.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import dictinsight.utils.DistributedServerConst;


/**
 * An example of the PathChildrenCache. The example "harness" is a command
 * processor that allows adding/updating/removed nodes in a path. A
 * PathChildrenCache keeps a cache of these changes and outputs when updates
 * occurs. *
 * 
 * @author liujg
 */
public class PathCacheService {

    private CuratorFramework client = null;

    private List<PathChildrenCache> cacheList = new ArrayList<PathChildrenCache>();

    public static PathCacheService getInstance() {
        return PathCacheServiceFactory.instance;
    }

    private static class PathCacheServiceFactory {
        private static PathCacheService instance = new PathCacheService();
    }

    public void newClient(){
        if (null == client) {
            client = CuratorFrameworkFactory.newClient(
                    DistributedServerConst.CONNECTSTRING,
                    new ExponentialBackoffRetry(1000, 3));
            client.start();
            System.out.println("connect zkstring="+DistributedServerConst.CONNECTSTRING);
        }        
    }
    
    /**
     * @param path
     * @param node
     * @param justListener
     *            true则只侦听，不赋值
     * @throws Exception
     */
    public synchronized void setNodeAndListener(String zkRoot,String path, String node,
            boolean justListener, IListener iListener) throws Exception {
        if (null == client) {
            client = CuratorFrameworkFactory.newClient(
                    DistributedServerConst.CONNECTSTRING,
                    new ExponentialBackoffRetry(1000, 3));
            client.start();
            System.out.println("connect zkstring="+DistributedServerConst.CONNECTSTRING);
        }

        if (justListener) {
            PathChildrenCache cache = new PathChildrenCache(client,
                    zkRoot + "/" + path, true);
            addListener(cache, iListener);
            cache.start();
            cacheList.add(cache);
        }
        else
            client.create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(zkRoot + "/" + path + "/"
                            + node);        
    }

    public void close() {
        CloseableUtils.closeQuietly(client);
        for (PathChildrenCache cache: cacheList) {
            CloseableUtils.closeQuietly(cache);
        }
    }

    /**
     * 通过listen某个节点，有变化时候相应的进行逻辑处理
     * @param cache
     * @param iListener
     */
    private void addListener(final PathChildrenCache cache,
            final IListener iListener) {
        PathChildrenCacheListener listener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client,
                    PathChildrenCacheEvent event) throws Exception {
                List<String> datas = new ArrayList<String>();
                //CONNECTION_SUSPENDED、CONNECTION_LOST、RECONNECTED等情况不处理
                switch (event.getType()) {
                    case CHILD_ADDED:
                    case CHILD_UPDATED:
                    case CHILD_REMOVED: {
                        for (ChildData data: cache.getCurrentData()) {
                            int index = data.getPath().lastIndexOf("/");
                            if (index < 0)
                                continue;
                            String server = data.getPath().substring(index + 1);
                            datas.add(server);
                        }
                        if (iListener != null)
                            iListener.refreshNodes(datas);
                    }
                }
                System.out.println("EventType=" + event.getType()
                        + ",refreshNodes:" + Arrays.toString(datas.toArray()));
            }
        };
        cache.getListenable().addListener(listener);
    }

    /**
     * 获取某个目录下的路径数据
     * @param zkRoot
     * @param path
     * @param sync 是否同步获取，确保数据一致性
     * @return
     * @throws Exception
     */
    public List<String> getChildren(String zkRoot,String path, boolean sync)
            throws Exception {
        if (sync)
            client.sync().forPath(zkRoot + "/" + path);
        return client.getChildren()
                .forPath(zkRoot + "/" + path);
    }
}
