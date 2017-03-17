package dictinsight.redis;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import dictinsight.notify.ErrorNotify;
import dictinsight.serialize.KryoSerialization;
import dictinsight.utils.HashMapCache;
import dictinsight.utils.io.FileUtils;
import dictinsight.utils.io.HttpUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

/**
 * jedis的接口没有对异常处理，所以包了一层异常处理模块
 * 
 * @author liujg
 */

public abstract class AbstractRedisService {

    private JedisCluster jedis;

    public AbstractRedisService() {
        jedis = JedisInstance.getInstance().getJedis();
    }

    public JedisCluster getJedis() {
        return this.jedis;
    }

    // 前缀设置
    public abstract String getPrefix();

    // 订阅触发逻辑接口
    public void subRefresh(final String channel, final String msg) {};

    public Integer getIntValue(String key) {
        String value = getStringValue(key);
        try {
            if (value != null)
                return Integer.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getLongValue(String key) {
        String value = getStringValue(key);
        try {
            if (value != null)
                return Long.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean getBooleanValue(String key) {
        String value = getStringValue(key);
        try {
            if (value != null)
                return Boolean.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getStringValue(String key) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            return jedis.get(getPrefix() + key);
        } catch (Exception e) {
            e.printStackTrace();
            RedisError.getInstance().addError();
            ErrorNotify.addException(e);
            return null;
        }
    }

    public boolean setValue(String key, String value) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.setex(getPrefix() + key, RedisConst.HOURIMEOUT, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setByteValue(String key, byte[] value, int seconds) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.set((getPrefix() + key).getBytes(), value);
            jedis.expire((getPrefix() + key).getBytes(), seconds);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public byte[] getByte(String key) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            return jedis.get((getPrefix() + key).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean setValue(String key, String value, int seconds) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.setex(getPrefix() + key, seconds, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void delKey(String key) {
        if (RedisError.getInstance().isDown())
            return;
        
        try {
            jedis.del(getPrefix() + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delByte(String key) {
        if (RedisError.getInstance().isDown())
            return;
        
        try {
            jedis.del((getPrefix() + key).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新的接口请用下面的fullExist
     * @param key
     * @return
     */
    public boolean exist(String key) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            return jedis.exists(getPrefix() + key);
        } catch (Exception e) {
            e.printStackTrace();
            RedisError.getInstance().addError();
            return false;
        }
    }
    
    /**
     * 区别上面的原因是这里返回值是true、false、null三种情况，保留上面的原因是为了兼容已有接口
     * @param key
     * @return
     */
    public Boolean fullExist(String key) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            return jedis.exists(getPrefix() + key);
        } catch (Exception e) {
            e.printStackTrace();
            RedisError.getInstance().addError();
            return null;
        }
    }

    public Long incrBy(String key, int seconds, int step) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            long result = jedis.incrBy(getPrefix() + key, step);
            jedis.expire(getPrefix() + key, seconds);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorNotify.addException(e);
            return null;
        }
    }

    public Long decrBy(String key, int seconds, int step) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            long result = jedis.decrBy(getPrefix() + key, step);
            jedis.expire(getPrefix() + key, seconds);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorNotify.addException(e);
            return null;
        }
    }

    public Set<String> smembers(String key) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            Set<String> set = jedis.smembers(getPrefix() + key);
            return set;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean sadd(final String key, final String member, int seconds) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.sadd(getPrefix() + key, member);
            jedis.expire(getPrefix() + key, seconds);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sismember(final String key, final String member) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            return jedis.sismember(getPrefix() + key, member);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Long scard(final String key) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            return jedis.scard(getPrefix() + key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // SortedSet相关函数
    public boolean zadd(final String key, final double score,
            final String member) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.zadd(getPrefix() + key, score, member);
            jedis.expire(getPrefix() + key, RedisConst.MONTHIMEOUT);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean zadd(final String key, final double score,
            final String member, int seconds) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.zadd(getPrefix() + key, score, member);
            jedis.expire(getPrefix() + key, seconds);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Set<String> zrange(final String key, final long start,
            final long end) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            Set<String> result = jedis.zrange(getPrefix() + key, start, end);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> zrevrange(final String key, final long start,
            final long end) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            Set<String> result = jedis.zrevrange(getPrefix() + key, start, end);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean zrem(final String key, final String member) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.zrem(getPrefix() + key, member);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Long zrank(final String key, final String member) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            return jedis.zrank(getPrefix() + key, member);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long zrevrank(final String key, final String member) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            return jedis.zrevrank(getPrefix() + key, member);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Double zscore(final String key, final String member) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            return jedis.zscore(getPrefix() + key, member);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String lindex(final String key, final long index) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            return jedis.lindex(getPrefix() + key, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> lrange(final String key, final long start,
            final long end) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            return jedis.lrange(getPrefix() + key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean lpushx(final String key, String... objs) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.lpushx(getPrefix() + key, objs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String lpop(final String key) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            String val = jedis.lpop(getPrefix() + key);
            return val;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean rpush(final String key, String... objs) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.rpush(getPrefix() + key, objs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, String> hgetAll(final String key) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            return jedis.hgetAll(getPrefix() + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<byte[], byte[]> hgetByteAll(final String key) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            return jedis.hgetAll((getPrefix() + key).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hset(final String key, final String field,
            final String value) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.hset(getPrefix() + key, field, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hset(final String key, final String field,
            final byte[] value) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.hset((getPrefix() + key).getBytes(), field.getBytes(), value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public long hdel(final String key, final String field) {
        if (RedisError.getInstance().isDown())
            return 0;
        
        try {
            return jedis.hdel((getPrefix() + key), field);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean hmset(final String key, final Map<String, String> map) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.hmset(getPrefix() + key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hmsetByteMap(final String key,
            final Map<byte[], byte[]> map) {
        if (RedisError.getInstance().isDown())
            return false;
        
        try {
            jedis.hmset((getPrefix() + key).getBytes(), map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public long zcard(final String key) {
        if (RedisError.getInstance().isDown())
            return 0;
        
        try {
            return jedis.zcard(getPrefix() + key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long expire(final String key, int seconds) {
        if (RedisError.getInstance().isDown())
            return 0;
        
        try {
            return jedis.expire(getPrefix() + key, seconds);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public long setnx(final String key, String value, int seconds) {
        if (RedisError.getInstance().isDown())
            return 0;
        
        try {
            long isSet = jedis.setnx(getPrefix() + key, value);
            if (isSet == 1)
                return jedis.expire(getPrefix() + key, seconds);
            else
                return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean setObject(String key, Object obj, int seconds) {
        if (RedisError.getInstance().isDown())
            return false;
        
        if (obj == null) {
            return false;
        }
        byte[] value = KryoSerialization.getInstance().serialize(obj);
        return setByteValue(key, value, seconds);
    }

    public <T> T getObject(String key, Class<T> cls) {
        if (RedisError.getInstance().isDown())
            return null;
        
        byte[] value = getByte(key);
        if (null == value || value.length < 1) {
            return null;
        }
        try {
            return KryoSerialization.getInstance().deserialize(value, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long publish(final String channel, final String message) {
        if (RedisError.getInstance().isDown())
            return null;
        
        try {
            return jedis.publish(channel, message);
        } catch (Exception e) {
            e.printStackTrace();
            return -1l;
        }
    }

    public void subscribe(final String... channels) {
        JedisPubSub jedisPubSub = new JedisPubSub() {
            @Override
            public void onUnsubscribe(String channel, int number) {
                System.out.println("onUnsubscribe=channel: " + channel
                        + ",number :" + number);
            }

            @Override
            public void onSubscribe(String channel, int number) {
                System.out.println("onSubscribe=channel: " + channel
                        + ",number :" + number);
            }

            @Override
            public void onPUnsubscribe(String arg0, int arg1) {}

            @Override
            public void onPSubscribe(String arg0, int arg1) {}

            @Override
            public void onPMessage(String arg0, String arg1, String arg2) {}

            @Override
            public void onMessage(String channel, String msg) {
                System.out.println("收到频道 : 【" + channel + " 】的消息 ：" + msg);
                subRefresh(channel, msg);
            }
        };

        jedis.subscribe(jedisPubSub, channels);
    }
}
