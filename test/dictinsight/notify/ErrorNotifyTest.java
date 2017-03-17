/**
 * @(#)ErrorNotifyTest.java, 2015年12月15日. 
 * 
 * Copyright 2015 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.notify;

import org.junit.Test;

/**
 *
 * @author liujg
 *
 */
public class ErrorNotifyTest {
    
    @Test
    public void testError() {
        ErrorNotify notify=new ErrorNotify();
        notify.addException(new Exception("test"));
    }

}
