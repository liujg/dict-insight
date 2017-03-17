/**
 * @(#)VerifyUtil.java, 2016年1月29日. Copyright 2016 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.utils;

import java.lang.reflect.Field;

/**
 * @author liujg
 */
public class VerifyUtil {

    public static byte[] object2bytes(Object o) {
        int identify = clazzIdentify(o);
        return toBytes(identify);
    }
    
    public static int clazzIdentify(Object o) {
        Field[] fileds = o.getClass().getDeclaredFields();
        StringBuilder s = new StringBuilder();
        for (Field f: fileds) {
            s.append(f);
        }
        return s.toString().hashCode();
    }

    public static byte[] clazz2bytes(Class clazz) {
        int identify = clazzIdentify(clazz);
        return toBytes(identify);
    }

    public static int clazzIdentify(Class clazz) {
        Field[] methods = clazz.getDeclaredFields();
        StringBuilder s = new StringBuilder();
        for (Field f: methods) {
            s.append(f);
        }
        return s.toString().hashCode();
    }
    
    public static byte[] toBytes(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i /* >> 0 */);
        return result;
    }
}
