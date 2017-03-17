package dictinsight.thrift;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengwei on 2015/11/24.
 */
public class HomeworkProtocolImpl implements HomeworkProtocol.Iface {
    private final static Log LOG = LogFactory.getLog(HomeworkProtocolImpl.class);
    public static String STATUS = "success";

    /**
     * return json
     * @param examId
     * @return
     * @throws TException
     */
    @Override
    public ByteBuffer getHomework(int examId) throws TException {
        System.out.println(examId);
        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  ByteBuffer.allocate(1);
    }

    /**
     * return json(serialize)
     * @param examId
     * @param userId
     * @return
     * @throws TException
     */
    @Override
    public ByteBuffer getRecords(int examId, String userId) throws TException {
        return  ByteBuffer.allocate(1);
    }

    @Override
    public boolean submit(ByteBuffer record) throws TException {   
        return true;
    }

    @Override
    public boolean deleteExamCache(int examId) throws TException {
        return true;
    }

    @Override
    public boolean deleteRecord(String userId, int examId) throws TException {
        return true;
    }
}
