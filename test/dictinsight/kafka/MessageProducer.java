/**
 * @(#)ProducerDemo.java, 2015年10月19日. Copyright 2015 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.kafka;

import java.util.*;

import dictinsight.kafka.AbstractedMessageProducer;
import joptsimple.internal.Strings;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class MessageProducer extends AbstractedMessageProducer{

    private Producer<String, String> producer;

    public static MessageProducer instance = new MessageProducer();

    public static MessageProducer getInstance() {
        return instance;
    }
    
    /* (non-Javadoc)
     * @see dictinsight.kafka.AbstractedMessageProducer#getBrokerList()
     */
    @Override
    public String getBrokerList() {
        // TODO Auto-generated method stub
        return "61.135.221.253:9092,61.135.221.253:9093,61.135.221.253:9094";
    }

    public static void main(String[] args) {
        String message = "abcdefg";
        String topic = "ljgtest";
        for (int i = 0; i < 3; i++)
            MessageProducer.getInstance().send(message + i, topic);
    }
}
