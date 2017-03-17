/**
 * @(#)UserRedisService.java, 2015年12月11日. 
 * 
 * Copyright 2015 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.redis;

import dictinsight.redis.AbstractRedisService;

/**
 *
 * @author liujg
 *
 */
public class RedisService extends AbstractRedisService{

    public static RedisService instance=new RedisService();
    
    public static RedisService getInstance(){
        return instance;
    }

    /* (non-Javadoc)
     * @see dictinsight.redis.AbstractRedisService#getPrefix()
     */
    @Override
    public String getPrefix() {
        // TODO Auto-generated method stub
        return "test";
    }
    
    public void subRefresh(final String channel,final String msg){
        System.out.println("refreshNodes="+channel+",msg");
    };

}
