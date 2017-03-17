package dictinsight.serialize;

import com.esotericsoftware.kryo.Kryo;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.ArrayList;

/**
 * Created by dengwei on 2015/11/20.
 */
public class KryoFactory {

    private final GenericObjectPool<Kryo> kryoPool;

    public KryoFactory() {
        kryoPool = new GenericObjectPool<Kryo>(new PooledKryoFactory());
    }

    public KryoFactory(final int maxTotal, final int minIdle, final long maxWaitMillis, final long minEvictableIdleTimeMillis) {
        kryoPool = new GenericObjectPool<Kryo>(new PooledKryoFactory());
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);
        config.setMaxIdle((minIdle * 2) > maxTotal ? maxTotal : (minIdle * 2));
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        kryoPool.setConfig(config);
    }

    public Kryo getKryo() {
        try {
            return kryoPool.borrowObject();
        } catch (final Exception ex) {
            return null;
        }
    }

    public void returnKryo(final Kryo kryo) {
        kryoPool.returnObject(kryo);
    }
}
