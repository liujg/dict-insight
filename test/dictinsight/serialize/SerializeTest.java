/**
 * @(#)SerializeTest.java, 2016年1月29日. Copyright 2016 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.serialize;

import dictinsight.utils.VerifyUtil;

/**
 * @author liujg
 */
public class SerializeTest {

    public String a;

    public String b;

    private String c;

    public static <T> void main(String args[]) {

//        SerializeTest t = new SerializeTest();
//        t.a = "a";
//        t.b = "b";
//        t.c = "c";
//        byte[] ser = KryoSerialization.getInstance().serialize(t);
//
//        SerializeTest t2 = KryoSerialization.getInstance().deserialize(ser,
//                SerializeTest.class);
//        System.out.println(t2.a);
        
        VerifyUtil.clazzIdentify(SerializeTest.class);
    }
}
