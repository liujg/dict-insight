/**
 * @(#)RedisPubSub.java, 2016年1月20日. 
 * 
 * Copyright 2016 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.redis;

import redis.clients.jedis.JedisPubSub;

/**
 *
 * @author liujg
 *
 */
public class RedisPubSub {

    public void sub(){      
        RedisService.getInstance().subscribe(new String[]{"channel1","channel2"}); 
    }
    
    public static void main(String args[]) {
        RedisPubSub sub=new RedisPubSub();    
        sub.sub();
    }
    
}
