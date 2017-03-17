/**
 * @(#)RedisError.java, 2016年3月23日. Copyright 2016 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.redis;

import java.util.concurrent.atomic.AtomicInteger;
import dictinsight.notify.ErrorNotify;
import dictinsight.utils.HashMapCache;

/**
 * 用来标记redis健康状况的，便于系统降级处理
 * 
 * @author liujg
 */
public class RedisError {

    public HashMapCache<String, AtomicInteger> errorStaticMap = new HashMapCache<String, AtomicInteger>(
            10, 5 * 60, 1);// 短期内redis异常数超过100，则认为redis挂了，五分钟后再重试

    public static final String FAIL = "fail";

    public static final String ERROR = "error";

    public static RedisError instance = new RedisError();

    public RedisError() {
        errorStaticMap.init();
    }

    public static RedisError getInstance() {
        return instance;
    }

    public void addError() {
        AtomicInteger num = errorStaticMap.get(ERROR);
        if (null == num) {
            errorStaticMap.put(ERROR, new AtomicInteger(1));
        } else
            num.incrementAndGet();

        if (num.get() > 100) {
            errorStaticMap.put(FAIL, new AtomicInteger(1));
            ErrorNotify.addException(new Exception("detect redis down now"));
        }
    }

    public boolean isDown() {
        return errorStaticMap.get(FAIL) != null;
    }
}
