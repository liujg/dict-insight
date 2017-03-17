/**
 * @(#)AbstractedCall.java, 2015年9月23日. Copyright 2015 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.asynqueue;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.alibaba.fastjson.JSONObject;

/**
 * @author liujg
 */
public class PriorityFutureTask extends FutureTask {

    public PriorityFutureTask(Callable callable) {
        super(callable);
        this.startTime = System.currentTimeMillis();
    }

    public long startTime;// 生成时间
    
    private ConsumerPriorityEnum priority = ConsumerPriorityEnum.NOMOAL;
    
    public ConsumerPriorityEnum getPriority(){
        return priority;
    }
    
    public void setPriority(ConsumerPriorityEnum p){
        this.priority=p;
    }
}
