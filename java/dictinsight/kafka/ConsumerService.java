package dictinsight.kafka;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConsumerService {
    private final ConsumerConnector consumer;

    private final String topic;

    private ExecutorService executor;

    private IConsumer iconsumer;
    
    protected static final Log logger = LogFactory
            .getLog(ConsumerService.class);
    
    public ConsumerService(String a_zookeeper, String a_groupId,
            String a_topic,IConsumer iconsumer) {
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig(a_zookeeper, a_groupId,"smallest"));
        this.topic = a_topic;
        this.iconsumer=iconsumer;
    }

    public ConsumerService(String a_zookeeper, String a_groupId,
            String a_topic,IConsumer iconsumer,String reset) {
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig(a_zookeeper, a_groupId,reset));
        this.topic = a_topic;
        this.iconsumer=iconsumer;
    }
    
    public void shutdown() {
        if (consumer != null)
            consumer.shutdown();
        if (executor != null)
            executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out.println(
                        "Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out
                    .println("Interrupted during shutdown, exiting uncleanly");
        }
    }

    public void run(int a_numThreads) {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(a_numThreads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer
                .createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

        // now launch all the threads
        executor = Executors.newFixedThreadPool(a_numThreads);

        // now create an object to consume the messages
        for (final KafkaStream stream: streams) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    ConsumerIterator<byte[], byte[]> it = stream.iterator();
                    while (it.hasNext()) {
                        String message = new String(it.next().message());
                        logger.info(message);
                        iconsumer.consumer(message);
                    }
                    System.out.println("Shutting down Thread: " + stream);
                }

            });
        }
    }

    /**
     * 
     * @param a_zookeeper
     * @param a_groupId
     * @param reset:smallest\largest
* What to do when there is no initial offset in ZooKeeper or if an offset is out of range:
* smallest : automatically reset the offset to the smallest offset
* largest : automatically reset the offset to the largest offset
     * @return
     */
    private static ConsumerConfig createConsumerConfig(String a_zookeeper,
            String a_groupId,String reset) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", reset);
        
        return new ConsumerConfig(props);
    }
    
}
