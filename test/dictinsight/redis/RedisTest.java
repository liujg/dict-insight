/**
 * @(#)RedisTest.java, 2016年1月20日. 
 * 
 * Copyright 2016 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.redis;

import org.junit.Test;

/**
 *
 * @author liujg
 *
 */
public class RedisTest {

    @Test
    public void testRedis() {
        RedisService.getInstance().setValue("a", "b");
        System.out.print(RedisService.getInstance().getStringValue("a"));
    }
    
    @Test
    public void testPublish() {
        long num=RedisService.getInstance().publish("channel1", "hello1");
        System.out.println(num);
    }
}
