package dictinsight.thrift;

import java.nio.ByteBuffer;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransportException;

import dictinsight.drpc.DRPCClient;
import dictinsight.thrift.ThriftClient;
import dictinsight.thrift.ThriftClient.SyncClient;

/**
 * Created by dengwei on 2015/11/25.
 */
public class HomeworkClient {

    public static void main(String[] args) {
        TProtocolFactory factory = new TBinaryProtocol.Factory();
        try {
            ThriftClient.Client client = new SyncClient(factory,
                    HomeworkProtocol.Client.class, "127.0.0.1", 1234, 10000);
            HomeworkProtocol.Client connection = (HomeworkProtocol.Client) client.getConnection();
            ByteBuffer homework = connection.getHomework(1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
