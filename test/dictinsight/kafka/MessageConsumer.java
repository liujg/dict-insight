/**
 * @(#)MessageConsumer.java, 2016年2月24日. 
 * 
 * Copyright 2016 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.kafka;

/**
 *
 * @author liujg
 *
 */
public class MessageConsumer implements IConsumer{

    /* (non-Javadoc)
     * @see dictinsight.kafka.IConsumer#consumer(java.lang.String)
     */
    @Override
    public void consumer(String message) {
        // TODO Auto-generated method stub
        System.out.println(System.currentTimeMillis()+"="+message);
    }

}
