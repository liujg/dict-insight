/**
 * @(#)JedisInstance.java, 2015年12月11日. Copyright 2015 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.redis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import dictinsight.utils.io.FileUtils;
import dictinsight.utils.io.HttpUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * @author liujg
 */
public class JedisInstance {

    private JedisCluster jedis;

    private final int DEFAULT_TIMEOUT = 2000;

    public static JedisInstance instance = new JedisInstance();

    public JedisInstance() {
        boolean online = FileUtils.getBooleanConfig(RedisConst.filename, "online", false);
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        if (online) {
            List<String> seedList = HttpUtils
                    .getSvnConfServer(RedisConst.seedList);
            for (String seed: seedList) {
                String[] s = seed.split(":");
                try {
                    jedisClusterNodes
                            .add(new HostAndPort(s[0], Integer.valueOf(s[1])));
                    System.out.println("connect to rediscluster:"+s[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            String host = FileUtils.getStringConfig(RedisConst.filename, "redis-hosts",
                    "nc042x");
            String ports = FileUtils.getStringConfig(RedisConst.filename, "redis-port",
                    "7000:7001:7002:7003:7004:7005");
            String[] portArray = ports.split(":");
            for (String port: portArray) {
                jedisClusterNodes
                        .add(new HostAndPort(host, Integer.valueOf(port)));
                System.out.println("connect to "+host);
            }
        }

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(200);
        poolConfig.setMaxIdle(20);
        poolConfig.setMinIdle(10);
        poolConfig.setMaxWaitMillis(5 * 1000);
        poolConfig.setTimeBetweenEvictionRunsMillis(5 * 60 * 1000);
        poolConfig.setEvictionPolicyClassName(
                "org.apache.commons.pool2.impl.DefaultEvictionPolicy");
        poolConfig.setSoftMinEvictableIdleTimeMillis(2 * 60 * 1000);
        jedis = new JedisCluster(jedisClusterNodes, DEFAULT_TIMEOUT, 3,
                poolConfig);
    }

    public static JedisInstance getInstance() {
        return instance;
    }

    public JedisCluster getJedis() {
        return jedis;
    }
}
