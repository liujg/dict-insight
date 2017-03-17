/**
 * @(#)FixedPriorityBlockingQueue.java, 2015年9月22日. Copyright 2015 Yodao, Inc.
 * All rights reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to
 * license terms.
 */
package dictinsight.asynqueue;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * FixedPriorityBlockingQueue base on PriorityBlockingQueue,it limit the
 * maxCapacity!
 * 
 * @author liujg
 */
public class FixedPriorityBlockingQueue<E> extends PriorityBlockingQueue<E> {

    private int maxCapacity;

    private final ReentrantLock lock;

    public FixedPriorityBlockingQueue(int maxCapacity,
            Comparator<? super E> comparator) {
        super(maxCapacity, comparator);
        this.maxCapacity = maxCapacity;
        this.lock = new ReentrantLock();
    }

    public boolean offer(E e) {
        if (e == null)
            throw new NullPointerException();
        try {
            lock.lock();
            if (size() > maxCapacity)
                return false;
            else
                return super.offer(e);
        } finally {
            lock.unlock();
        }
    }
}
