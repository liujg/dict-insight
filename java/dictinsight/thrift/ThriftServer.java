package dictinsight.thrift;

import com.alibaba.fastjson.JSONObject;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.*;

import java.net.InetSocketAddress;
import java.util.concurrent.*;

/**
 * <p>
 * 主要采用了TThreadedSelectorServer和TThreadPoolServer这两种server类型，具体的对比可以参考https://
 * github.com/m1ch1/mapkeeper/wiki/Thrift-Java-Servers-Compared
 * <p>
 * TThreadedSelectorServer是非阻塞io，It maintains 2 thread pools, one for handling
 * network I/O, and one for handling request processing，可以为client提供异步接口。
 * TThreadPoolServer是阻塞io，一个连接一个线程，所有的连接数不能超过预定设置的线程数，所以在使用上需要注意设置合理的最大值，
 * 在并发不高的情况下吞吐率要高
 * <p>
 * Created by dengwei on 2015/11/23.
 */
public class ThriftServer {

    public static abstract class Server {
        // thrift注册的proccessor
        TProcessor processor;

        int port;

        int timeout = 2000;

        // 协议信息
        TTransportFactory transportFactory = new TFramedTransport.Factory();

        TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();

        TServer server = null;

        // 配置信息
        Object tArgs;

        public Server(TProcessor processor, int port, int timeout) {
            this.processor = processor;
            this.port = port;
            this.timeout = timeout;
        }

        public void stop() {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public abstract void start() throws Exception;

        public String toString() {
            return JSONObject.toJSONString(this).toString();
        }
    }

    public static class AsyncServer extends Server {

        int selectorThread = 8;

        int workerThreads = 16;

        int acceptQueueSizePerThread = 20;

        private int timeout = 2000;

        TTransportFactory transportFactory = new TFramedTransport.Factory();

        TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();

        TServer server = null;

        private Object tArgs;

        public AsyncServer(TProcessor processor, int port, int timeout,
                int selectorThread, int workerThreads,
                int acceptQueueSizePerThread) {
            super(processor, port, timeout);
            this.selectorThread = selectorThread;
            this.workerThreads = workerThreads;
            this.acceptQueueSizePerThread = acceptQueueSizePerThread;
        }

        public void start() throws TTransportException {
            InetSocketAddress address = new InetSocketAddress(port);
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(
                    address, timeout);

            TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(
                    serverTransport);
            tArgs.processor(processor);
            tArgs.selectorThreads(selectorThread);
            tArgs.workerThreads(workerThreads);
            tArgs.acceptQueueSizePerThread(acceptQueueSizePerThread);

            tArgs.transportFactory(transportFactory);
            tArgs.protocolFactory(protocolFactory);

            this.tArgs = tArgs;
            server = new TThreadedSelectorServer(tArgs);

            System.out.println(
                    "Starting the TNonblockingServerSocket server...targs="
                            + JSONObject.toJSONString(tArgs));
            server.serve();
        }
    }

    public static class SyncServer extends Server {

        int minWorkerThreads = 1;

        int maxWorkerThreads = 16;

        int requestTimeout = 100;

        int stopTimeout = 100;

        int queueMaxSize = 0;

        public SyncServer(TProcessor processor, int port, int timeout,
                int minWorkerThreads, int maxWorkerThreads, int requestTimeout,
                int stopTimeout) {
            super(processor, port, timeout);
            this.minWorkerThreads = minWorkerThreads;
            this.maxWorkerThreads = maxWorkerThreads;
            this.requestTimeout = requestTimeout;
            this.stopTimeout = stopTimeout;
        }

        public SyncServer(TProcessor processor, int port, int timeout,
                          int minWorkerThreads, int maxWorkerThreads, int requestTimeout,
                          int stopTimeout, int queueMaxSize) {
            super(processor, port, timeout);
            this.minWorkerThreads = minWorkerThreads;
            this.maxWorkerThreads = maxWorkerThreads;
            this.requestTimeout = requestTimeout;
            this.stopTimeout = stopTimeout;
            this.queueMaxSize = queueMaxSize;
        }

        @Override
        public void start() throws TTransportException {
            InetSocketAddress address = new InetSocketAddress(port);
            TServerSocket serverTransport = new TServerSocket(address, timeout);
            TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(
                    serverTransport);
            tArgs.minWorkerThreads(minWorkerThreads);
            tArgs.maxWorkerThreads(maxWorkerThreads);
            tArgs.requestTimeout(requestTimeout);
            tArgs.stopTimeoutVal(stopTimeout);
            tArgs.processor(processor);
            
            if (queueMaxSize > 0) {
                BlockingQueue<Runnable> executorQueue = new ArrayBlockingQueue<Runnable>(
                        queueMaxSize);
                ThreadPoolExecutor executor = new ThreadPoolExecutor(
                        tArgs.minWorkerThreads, tArgs.maxWorkerThreads,
                        tArgs.stopTimeoutVal, TimeUnit.SECONDS, executorQueue);
                tArgs.executorService(executor);
            }
            this.tArgs = tArgs;
            server = new TThreadPoolServer(tArgs);
            server.serve();
        }
    }

    public static Server syncServer(TProcessor processor, int port, int timeout,
            int minWorkerThreads, int maxWorkerThreads, int requestTimeout,
            int stopTimeout) throws Exception {
        Server rpc = new SyncServer(processor, port, timeout, minWorkerThreads,
                maxWorkerThreads, requestTimeout, stopTimeout);
        System.out.println("syncServer rpc get info:" + rpc.toString());
        return rpc;
    }

    public static Server syncServer(TProcessor processor, int port, int timeout,
                                    int minWorkerThreads, int maxWorkerThreads, int requestTimeout,
                                    int stopTimeout, int queueMaxSize) throws Exception {
        Server rpc = new SyncServer(processor, port, timeout, minWorkerThreads,
                maxWorkerThreads, requestTimeout, stopTimeout, queueMaxSize);
        System.out.println("syncServer rpc get info:" + rpc.toString());
        return rpc;
    }

    public static Server asyncServer(TProcessor processor, int port,
            int timeout, int selectorThread, int workerThreads,
            int acceptQueueSizePerThread) throws Exception {
        Server rpc = new AsyncServer(processor, port, timeout, selectorThread,
                workerThreads, acceptQueueSizePerThread);
        System.out.println("asyncServer rpc get info:" + rpc.toString());
        return rpc;
    }
}
