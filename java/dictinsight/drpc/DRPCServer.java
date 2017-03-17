package dictinsight.drpc;

import dictinsight.notify.ErrorNotify;
import dictinsight.thrift.ThriftServer;
import dictinsight.thrift.ThriftServer.Server;
import dictinsight.zkcurator.PathCacheService;

import org.apache.thrift.TProcessor;
import org.apache.zookeeper.CreateMode;
import toolbox.misc.LogFormatter;

import java.net.InetAddress;
import java.util.logging.Logger;

/**
 * Created by dengwei on 2015/11/24.
 */
public class DRPCServer {
    private static final Logger LOG = LogFormatter.getLogger(DRPCServer.class);

    /**
     * 同步调用
     * 
     * @param processor
     * @param port
     * @param timeout
     * @param minWorkerThreads
     * @param maxWorkerThreads
     * @param requestTimeout
     * @param stopTimeout
     * @return
     */
    public static void startSyncServer(String zkRoot, String serviceName,
            TProcessor processor, int port, int timeout, int minWorkerThreads,
            int maxWorkerThreads, int requestTimeout, int stopTimeout)
                    throws Exception {
        final Server server = ThriftServer.syncServer(processor, port, timeout,
                minWorkerThreads, maxWorkerThreads, requestTimeout,
                stopTimeout);

        LOG.info("DRpc server started");
        String address = InetAddress.getLocalHost().getHostName() + ":" + port;
        PathCacheService.getInstance().setNodeAndListener(zkRoot, serviceName,
                address, false, null);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.stop();
                
                // zk资源关闭
                PathCacheService.getInstance().close();
            }
        });
        /**
         * 同步阻塞
         */
        server.start();
    }

    public static void startAsyncServer(String zkRoot, String serviceName,
            TProcessor processor, int port, int timeout, int selectorThread,
            int workerThreads, int acceptQueueSizePerThread) throws Exception {
        final Server server = ThriftServer.asyncServer(processor, port, timeout,
                selectorThread, workerThreads, acceptQueueSizePerThread);
        String address = InetAddress.getLocalHost().getHostName() + ":" + port;
        PathCacheService.getInstance().setNodeAndListener(zkRoot, serviceName,
                address, false, null);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.stop();
                
                // zk资源关闭
                PathCacheService.getInstance().close();
            }
        });
        server.start();
    }
}
