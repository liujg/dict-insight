package dictinsight.serialize;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import dictinsight.notify.ErrorNotify;
import dictinsight.utils.BytesUtil;
import dictinsight.utils.VerifyUtil;
import scala.actors.threadpool.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by dengwei on 2015/11/20.
 */
public class KryoSerialization {

    public static int maxPool = 1000;
    public static int minPool = 50;
    public static int waitTimeout = 1000;
    public static int minIdleTime = 1000 * 60;

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    // 100 size 10 min 100 max 1min
    public static KryoSerialization instance = null;

    public static KryoSerialization getInstance() {
        if(instance != null)
            return instance;
        synchronized (KryoSerialization.class) {
            if(instance != null) {
                return instance;
            }
            instance = new KryoSerialization(
                    new KryoFactory(maxPool, minPool, waitTimeout, minIdleTime));
        }
        return instance;
    }

    private KryoFactory factory;

    private KryoSerialization(KryoFactory factory) {
        this.factory = factory;
    }

    public byte[] serialize(Object object) {
        return serialize(object, true);
    }

    /**
     * 
     * @param object
     * @param verify 默认true则在byte[]前四位添加类信息，存储的是其hash值，为了检测类发生变化
     * @return
     */
    public byte[] serialize(Object object, boolean verify) {
        Output output = new Output(1024, -1);
        Kryo kryo = null;
        try {
            kryo = factory.getKryo();

            if(kryo == null) {
                LOG.error("Error:kryo have no remain element ***");
                ErrorNotify.addException(new Exception("kryo pool exhaust,class="+object.getClass().getName()));
                return null;
            }
            LOG.debug("id==========="
                    + kryo.getRegistration(object.getClass()).getId()
                    + object.getClass().getName());
            kryo.writeObject(output, object);
            byte[] bytes = output.toBytes();
            if(verify){
                byte[] verifys=VerifyUtil.object2bytes(object);
                return BytesUtil.spliceBytes(verifys,bytes);
            }
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorNotify.addException(e);
            return null;
        } finally {
            factory.returnKryo(kryo);
            output.close();
        }
    }

    public <T> T deserialize(byte[] bytes, Class<T> cls) {
        return deserialize(bytes, cls, true);
    }

    /**
     * 
     * @param bytes
     * @param cls
     * @param verify 默认true则将byte[]前四位类信息取出来对比，如果类改动了则返回null,存储的是其hash值，所以也存在很低的冲突率
     * @return
     */
    public <T> T deserialize(byte[] bytes, Class<T> cls, boolean verify) {
        Input input = new Input();
        Kryo kryo = null;
        try {
            kryo = factory.getKryo();

            if(kryo == null) {
                LOG.error("Error:kryo have no remain element ***");
                ErrorNotify.addException(new Exception("kryo pool exhaust,class="+cls.getName()));
                return null;
            }

            if(verify){
                byte[] heads=BytesUtil.subBytes(bytes,0,4);
                byte[] verifys=VerifyUtil.clazz2bytes(cls);
                //class信息发生了变化
                if(!BytesUtil.equal(heads, verifys)){
                    LOG.info("class "+cls.getName()+" changed!");                 
                    return null;
                }
                
                bytes=BytesUtil.subBytes(bytes, 4,bytes.length);
            }
            input.setBuffer(bytes);
            
            T obj = kryo.readObject(input, cls);
            if (!input.eof())
                return null;
            LOG.debug("id========" + kryo.getRegistration(cls).getId()
                    + cls.getName());
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorNotify.addException(e);
            return null;
        } finally {
            factory.returnKryo(kryo);
            input.close();
        }
    }
}
