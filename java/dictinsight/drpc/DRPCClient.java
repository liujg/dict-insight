package dictinsight.drpc;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

import dictinsight.thrift.ThriftClient;
import dictinsight.zkcurator.IListener;
import dictinsight.zkcurator.PathCacheService;
import toolbox.misc.LogFormatter;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by dengwei on 2015/11/24.
 */
public class DRPCClient implements IListener {

    private static final Logger LOG = LogFormatter.getLogger(DRPCClient.class);

    private Map<String, PooledRPCClient> clientFactories = new HashMap<String, PooledRPCClient>(
            2);

    private List<PooledRPCClient> listed = new ArrayList<PooledRPCClient>(
            2);

    private boolean sync;

    private String serviceName;

    private String zkRoot;
    
    private Class cls;

    private int timeout;

    private TProtocolFactory factory = new TBinaryProtocol.Factory();

    private Random rand = new Random();

    final private int retry = 3;

    private int threadNum = 8;

    public DRPCClient(String zkRoot,String serviceName, Class cls, int timeout, boolean sync) {
        this.serviceName = serviceName;
        this.zkRoot=zkRoot;
        this.cls = cls;
        this.timeout = timeout;
        this.sync = sync;
        try {
            PathCacheService.getInstance().setNodeAndListener(zkRoot,serviceName, null,
                    true, this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        syncAndRefreshNode();
    }
    
    public void setThreadNum(int threadNum){
        this.threadNum=threadNum;
    }
    
    public void syncAndRefreshNode() {
        List<String> datas = null;
        try {
            datas = PathCacheService.getInstance()
                    .getChildren(zkRoot,serviceName, true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        refreshNodes(datas);
    }
    
    public DRPCClient(String zkRoot,String service, Class cls, int timeout, boolean sync,
            TProtocolFactory factory) {
        this(zkRoot,service, cls, timeout, sync);
        this.factory = factory;
    }

    public void returnObject(ThriftClient.Client client) {
        if (client == null) {
            return;
        }

        if (clientFactories.containsKey(client.key)) {
            clientFactories.get(client.key).returnClient(client);
        }
    }

    public ThriftClient.Client getProxy(int index) {
        return listed.get(index).borrowObject();
    }

    public ThriftClient.Client getRandomProxy() {
        for (int i = 0; i < retry; ++i) {
            try {
                int randNum = rand.nextInt(listed.size());
                PooledRPCClient factory = listed.get(randNum);
                ThriftClient.Client client = factory.borrowObject();
                if (client != null)
                    return client;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int oneDay = 1000 * 60 * 60 * 24;

    @Override
    public void refreshNodes(List<String> nodes) {
        LOG.info("refreshing nodes for service " + serviceName + ",result="
                + Arrays.toString(nodes.toArray()));
        // List<ThriftClient.Client> oldClient = proxies;
        Map<String, PooledRPCClient> newFactories = new HashMap<String, PooledRPCClient>(
                2);
        Map<String, PooledRPCClient> oldFactories = clientFactories;
        try {
            for (String node: nodes) {
                LOG.info(node);
                try {
                    String host = node.split(":")[0];
                    int port = Integer.parseInt(node.split(":")[1]);
                    if (clientFactories.containsKey(node)) {
                        LOG.info(clientFactories.get(node) == null ? "null"
                                : clientFactories.get(node).toString());
                        newFactories.put(node, clientFactories.get(node));
                        clientFactories.remove(node);
                    } else {
                        LOG.info("new node find");
                        newFactories.put(host + ":" + port,
                                new PooledRPCClient(factory, cls, sync, host,
                                        port, timeout, threadNum, oneDay, 2000,
                                        1000));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            clientFactories = newFactories;
            listed = new ArrayList<PooledRPCClient>();
            for (PooledRPCClient f: clientFactories.values()) {
                listed.add(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Set<Map.Entry<String, PooledRPCClient>> set = oldFactories.entrySet();
        for (Map.Entry<String, PooledRPCClient> e: set) {
            e.getValue().destroy();
        }
    }

    public static DRPCClient getDRPCClient(String zkRoot,String serviceName, Class cls,
            int timeout, boolean sync) {
        DRPCClient drpcClient = new DRPCClient(zkRoot,serviceName, cls, timeout, sync);
        return drpcClient;
    }

    public static DRPCClient getDRPCClient(String zkRoot,String serviceName, Class cls,
            int timeout, boolean sync, int threadNum) {
        DRPCClient drpcClient = new DRPCClient(zkRoot,serviceName, cls, timeout, sync);
        drpcClient.setThreadNum(threadNum);
        return drpcClient;
    }

    public static DRPCClient getDRPCClient(String zkRoot,String serviceName, Class cls,
            int timeout, boolean sync, TProtocolFactory factory) {
        DRPCClient drpcClient = new DRPCClient(zkRoot,serviceName, cls, timeout, sync,
                factory);
        return drpcClient;
    }
}
