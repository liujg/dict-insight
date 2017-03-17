package dictinsight.thrift;

import dictinsight.drpc.DRPCServer;
import dictinsight.thrift.ThriftServer.Server;
import dictinsight.utils.io.FileUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by dengwei on 2015/11/25.
 */
public class HomeworkServer {

    public static void main(String[] args) throws Exception {
        HomeworkProtocolImpl protocol = new HomeworkProtocolImpl();
        HomeworkProtocol.Processor processor = new HomeworkProtocol.Processor(
                protocol);
        Server server = ThriftServer.syncServer(processor, 1234, 1000, 1, 100,
                1000, 1000);
        server.start();
    }
}
