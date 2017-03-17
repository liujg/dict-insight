/**
 * @(#)AsynQueneService.java, 2015年9月23日. Copyright 2015 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.asynqueue;

import dictinsight.utils.HashMapCache;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liujg
 */
public class AsynQueueService {

    private final int QUEUESIZE = 500;

    private final int MAXIMUMPOOLSIZE = 4;

    private final int COREPOOLSIZE = 2;

    private final long keepAliveTime = 5;

    private ThreadPoolExecutor executor;

    private BlockingQueue workQueue;

    public HashMapCache<String, Boolean> codeCache;

    public static AsynQueueService instance = new AsynQueueService();

    public static AsynQueueService getInstance() {
        return instance;
    }

    public AsynQueueService() {
        this.workQueue = new FixedPriorityBlockingQueue<PriorityFutureTask>(
                QUEUESIZE, new Comparator<PriorityFutureTask>() {
                    @Override
                    public int compare(PriorityFutureTask o1,
                            PriorityFutureTask o2) {
                        // TODO Auto-generated method stub
                        return o2.getPriority().compareTo(o1.getPriority());
                    }

                });
        this.executor = new ThreadPoolExecutor2(COREPOOLSIZE, MAXIMUMPOOLSIZE,
                keepAliveTime, TimeUnit.MINUTES, workQueue);
        this.executor.allowCoreThreadTimeOut(true);

        this.codeCache = new HashMapCache<String, Boolean>(100, 5 * 60);
        this.codeCache.init();
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
