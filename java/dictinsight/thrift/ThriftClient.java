package dictinsight.thrift;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.*;

import dictinsight.notify.ErrorNotify;

import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * Created by dengwei on 2015/11/23.
 */
public class ThriftClient {

    public static abstract class Client<T>{

        TProtocol protocol;
        TProtocolFactory factory;
        Class<T> cls;
        TTransport transport;

        String host;
        int port;
        int timeout;
        public String key;
        public Client(TProtocolFactory factory, Class<T> cls, String host, int port, int timeout) {
            this.factory = factory;
            this.cls = cls;
            this.host = host;
            this.port = port;
            this.timeout = timeout;
            this.key=host+":"+port;
        }

        public abstract T getConnection();
        
        public void close() {
            if(transport != null && transport.isOpen())
                transport.close();
        }

        public boolean valid() {
            return transport != null && transport.isOpen();
        }
    }

    public static class SyncClient<T extends TServiceClient> extends Client<T> {

        private T object = null;
        public SyncClient(TProtocolFactory factory, Class<T> cls, String host, int port, int timeout) throws TTransportException {
            super(factory, cls, host, port, timeout);                
            getConnection();           
        }

        @Override
        public  T getConnection(){
            if (object != null) {
                return object;
            }
            try {
                transport = new TSocket(host, port, timeout);
                System.out.println(host + ":" + port);
                transport.open();
                protocol = factory.getProtocol(transport);
                Constructor<T> constructor = cls
                        .getConstructor(TProtocol.class);
                object = constructor.newInstance(protocol);
            } catch (Exception e) {
                e.printStackTrace();
                ErrorNotify.addException(e);
            } finally {
                return object;
            }
        }
    }

    public static class AsyncClient<T extends TAsyncClient> extends Client<T> {

        private T object;
        public AsyncClient(TProtocolFactory factory, Class<T> cls, String host, int port, int timeout) throws IOException {
            super(factory, cls, host, port, timeout);
            getConnection();
        }

        @Override
        public T getConnection() {
            if (object != null) {
                return object;
            }
            try {
                TAsyncClientManager clientManager = new TAsyncClientManager();
                transport = new TNonblockingSocket(host, port, timeout);
                Constructor<T> constructor = cls.getConstructor(
                        TProtocolFactory.class, TAsyncClientManager.class,
                        TNonblockingTransport.class);
                object = constructor.newInstance(factory, clientManager,
                        transport);
            } catch (Exception e) {
                e.printStackTrace();
                ErrorNotify.addException(e);
            } finally {
                return object;
            }
        }
    }

    public static <T> Client<T> getClient(TProtocolFactory factory,
                                          Class cls, boolean sync,
                                          String host, int port, int timeout) throws Exception {
        if(sync) {
            return new SyncClient(factory, cls, host, port, timeout);
        } else {
            return new AsyncClient(factory, cls, host, port, timeout);
        }
    }
}
