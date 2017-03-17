/**
 * @(#)ConsumerManage.java, 2016年2月24日. Copyright 2016 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.kafka;

import dictinsight.utils.DistributedServerConst;

/**
 * @author liujg
 */
public class ConsumerManages {

    public static String groupId = "courseweb-group1";

    public static String topic = "ljgtest";

    public static void main(String[] args) {
        MessageConsumer orderConsumer = new MessageConsumer();
        ConsumerService orderConsumerService = new ConsumerService(
                DistributedServerConst.CONNECTSTRING, groupId, topic,
                orderConsumer);
        orderConsumerService.run(1);
        
        try {
            Thread.sleep(1000*60*100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }                
    }

}
