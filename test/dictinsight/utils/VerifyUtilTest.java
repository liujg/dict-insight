/**
 * @(#)VerifyUtilTest.java, 2016年2月19日. 
 * 
 * Copyright 2016 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.utils;

import org.junit.Test;


/**
 *
 * @author liujg
 *
 */
public class VerifyUtilTest {

    @Test
    public void testVerifyUtil(){
        String s=new String();
        System.out.println(VerifyUtil.clazzIdentify(s));
    }
}
