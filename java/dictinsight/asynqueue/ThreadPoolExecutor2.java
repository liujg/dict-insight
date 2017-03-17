/**
 * @(#)ThreadPoolExecutor2.java, 2015年9月24日. Copyright 2015 Yodao, Inc. All
 * rights reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license
 * terms.
 */
package dictinsight.asynqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolExecutor2 base on ThreadPoolExecutor,it rewrite submit function,if
 * Runnable task instanceof FutureTask,doesnot use newTaskFor to convert
 * 
 * @author liujg
 */
public class ThreadPoolExecutor2 extends ThreadPoolExecutor {

    /**
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     */
    public ThreadPoolExecutor2(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        // TODO Auto-generated constructor stub
    }

    /**
     * @throws RejectedExecutionException
     *             {@inheritDoc}
     * @throws NullPointerException
     *             {@inheritDoc}
     */
    public Future<?> submit(Runnable task) {
        if (task == null)
            throw new NullPointerException();
        if (task instanceof FutureTask) {
            FutureTask ftask = (FutureTask) task;
            execute(ftask);
            return ftask;
        } else {
            RunnableFuture<?> ftask = newTaskFor(task, null);
            execute(ftask);
            return ftask;
        }
    }
}
