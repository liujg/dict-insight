/**
 * @(#)SimplePartitioner.java, 2015年10月19日. Copyright 2015 Yodao, Inc. All
 * rights reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license
 * terms.
 */
package dictinsight.kafka;

import java.util.concurrent.atomic.AtomicInteger;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * @author liujg
 */

public class RoundRobinPartitioner implements Partitioner {

    private AtomicInteger sets = new AtomicInteger();

    public RoundRobinPartitioner(VerifiableProperties props) {

    }

    public int partition(Object key, int a_numPartitions) {
        int set = sets.incrementAndGet();
        if (Integer.MAX_VALUE == set) {
            sets.set(0);
        }
        int partition = set % a_numPartitions;
        return partition;
    }

}
