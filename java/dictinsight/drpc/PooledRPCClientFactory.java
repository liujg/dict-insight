package dictinsight.drpc;

import com.esotericsoftware.kryo.Kryo;
import dictinsight.thrift.ThriftClient;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.protocol.TProtocolFactory;

/**
 * Created by dengwei on 2015/11/26.
 */
public class PooledRPCClientFactory
        extends BasePooledObjectFactory<ThriftClient.Client> {

    private TProtocolFactory factory;

    private Class cls;

    private boolean sync;

    private String host;

    private int port;

    private int timeout;

    public PooledRPCClientFactory(TProtocolFactory factory, Class cls,
            boolean sync, String host, int port, int timeout) {
        this.factory = factory;
        this.cls = cls;
        this.sync = sync;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public ThriftClient.Client create() throws Exception {
        return ThriftClient.getClient(this.factory, this.cls, this.sync, host,
                port, this.timeout);
    }

    @Override
    public PooledObject<ThriftClient.Client> wrap(ThriftClient.Client obj) {
        return new DefaultPooledObject<ThriftClient.Client>(obj);
    }

    @Override
    public void destroyObject(PooledObject<ThriftClient.Client> object) {
        object.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<ThriftClient.Client> p) {
        return p.getObject().valid();
    }
}
