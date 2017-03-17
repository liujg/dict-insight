package dictinsight.drpc;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.protocol.TProtocolFactory;
import dictinsight.thrift.ThriftClient;

/**
 * Created by dengwei on 2015/11/26.
 */
public class PooledRPCClient {
    private final GenericObjectPool<ThriftClient.Client> poolClient;
    public PooledRPCClient(TProtocolFactory factory,
                             Class cls, boolean sync,
                             String host, int port, int timeout,
                             int maxTotal, int minIdle,
                             long maxWaitMillis, long minEvictableIdleTimeMillis) {      
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);
        config.setMaxIdle((minIdle * 2) > maxTotal ? maxTotal : (minIdle * 2));
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        config.setTestOnBorrow(true);
        poolClient = new GenericObjectPool<ThriftClient.Client>(new PooledRPCClientFactory(
                factory, cls, sync, host, port, timeout),config);
    }

    public ThriftClient.Client borrowObject() {
        try {
            return poolClient.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void returnClient(ThriftClient.Client client) {
        poolClient.returnObject(client);
    }

    public void destroy() {
        poolClient.close();
    }
}
