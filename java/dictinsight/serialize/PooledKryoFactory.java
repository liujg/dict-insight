package dictinsight.serialize;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import java.util.*;
import org.objenesis.strategy.StdInstantiatorStrategy;
import static com.esotericsoftware.kryo.Kryo.NOT_NULL;
import static com.esotericsoftware.kryo.Kryo.NULL;

/**
 * Created by dengwei on 2015/11/20.
 */
public class PooledKryoFactory extends BasePooledObjectFactory<Kryo> {

    private final static Log LOG = LogFactory.getLog(PooledKryoFactory.class);

    public static Class<?>[] REGISTER_ClS = {};

    @Override
    public Kryo create() throws Exception {
        return createKryo();
    }

    @Override
    public PooledObject<Kryo> wrap(Kryo kryo) {
        return new DefaultPooledObject<Kryo>(kryo);
    }

    private Kryo createKryo() {
        Kryo kryo = new Kryo();
        Kryo.DefaultInstantiatorStrategy strategy = (Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy();
        strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
        register(kryo);
        return kryo;
    }

    private static final int START = 2000;
    private static synchronized boolean register(Kryo kryo) {

        kryo.addDefaultSerializer(JSONObject.class, JSONSerializer.class);
        kryo.addDefaultSerializer(JSONArray.class, JSONArraySerializer.class);
        return true;
    }

    public static final int NULL_LEN = -1;
    public static class JSONSerializer extends Serializer<JSONObject> {

        @Override
        public void write(Kryo kryo, Output output, JSONObject object) {
            int length = object == null ? NULL_LEN : object.size();
            LOG.debug("write len " + length);
            output.writeInt(length);
            if(object == null) {
                return;
            }
            Set<Map.Entry<String, Object>> entrySet = object.entrySet();
            for(Map.Entry<String, Object> o : entrySet) {
                // 1.write key
                output.writeString(o.getKey());
                //System.out.println("write key " + o.getKey());
                Object value = o.getValue();
                if(value == null) {
                    output.writeInt(NULL_LEN);
                    continue;
                }

                // 2.value length
                output.writeInt(1);
                String clsName = value.getClass().getName();

                // 3.class type
                output.writeString(clsName);
                // 4.value
                kryo.writeObject(output, value);
            }
        }

        @Override
        public JSONObject read(Kryo kryo, Input input, Class<JSONObject> type) {
            int length = input.readInt();
            LOG.debug("read len " + length);
            if(length == NULL_LEN) {
                return null;
            }
            JSONObject object = new JSONObject(length);
            for(int i = 0; i < length; ++i) {
                // 1. read key
                String key = input.readString();
                LOG.debug("read key " + key);
                // 2. read value length
                int olen = input.readInt();

                //System.out.println("read value byte length " + olen);
                if(olen == NULL_LEN) {
                    object.put(key, null);
                    continue;
                }
                // 3. read className
                String clsName = input.readString();
                Class<?> cls;
                try {
                    cls = Class.forName(clsName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
                Object o = kryo.readObject(input, cls);
                //System.out.println("read value object " + o);
                object.put(key, o);
            }
            //System.out.println("final jsonobject \n" + object);
            return object;
        }

    }

    public static class JSONArraySerializer extends Serializer<JSONArray> {

        @Override
        public void write(Kryo kryo, Output output, JSONArray object) {
            int length = object == null ? NOT_NULL : object.size();
            output.writeInt(length);
            for(int i = 0; i < length; ++i) {
                Object element = object.get(i);


                if(element == null) {
                    output.writeInt(NULL_LEN);
                    continue;
                }
                output.writeInt(1);
                output.writeString(element.getClass().getName());
                kryo.writeObject(output, element);
            }
        }

        @Override
        public JSONArray read(Kryo kryo, Input input, Class<JSONArray> type) {
            int length = input.readInt();
            if(length == NULL_LEN) {
                return null;
            }
            JSONArray array = new JSONArray();
            for(int i = 0; i < length; ++i) {
                int olen = input.readInt();
                if(olen == NULL_LEN) {
                    array.add(null);
                }

                String clsName = input.readString();
                Class<?> cls;
                try {
                    cls = Class.forName(clsName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
                Object o = kryo.readObject(input, cls);

                array.add(o);
            }
            //System.out.println("final jsonarray" + array.toString());
            return array;
        }
    }
}
