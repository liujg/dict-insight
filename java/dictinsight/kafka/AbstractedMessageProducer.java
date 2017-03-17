/**
 * @(#)ProducerDemo.java, 2015年10月19日. Copyright 2015 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.kafka;

import java.util.*;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public abstract class AbstractedMessageProducer {

    private Producer<String, String> producer;

    public abstract String getBrokerList();

    public AbstractedMessageProducer(Properties props) {
        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
    }
    
    public AbstractedMessageProducer() {
        Properties props = new Properties();
        props.put("metadata.broker.list", getBrokerList());
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
        props.put("partitioner.class", "dictinsight.kafka.RoundRobinPartitioner");// 不设置就默认以key发
        props.put("request.required.acks", "-1");

        //使用异步发送消息，可设置queue.time(默认5000ms)多久发一次
        // 和batch.size(默认200)多少消息发一次
        props.put("producer.type", "async");
        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
    }

    public void send(String message, String topic) {
        send(message, "", topic);
    }

    public void send(String message, String key, String topic) {
        try {
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(
                    topic, key, message);
            producer.send(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
