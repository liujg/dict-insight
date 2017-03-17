/**
 * @(#)ByteUtil.java, 2016年1月29日. Copyright 2016 Yodao, Inc. All rights
 * reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package dictinsight.utils;

/**
 * @author liujg
 */
public class BytesUtil {

    public static byte[] spliceBytes(byte[]... bytes) {
        int length = 0;
        for (byte[] bs: bytes) {
            length += bs.length;
        }
        byte[] copy = new byte[length];
        int srcPos = 0;
        for (byte[] bs: bytes) {
            System.arraycopy(bs, 0, copy, srcPos, bs.length);
            srcPos += bs.length;
        }
        return copy;
    }

    public static byte[] subBytes(byte[] bytes, int beginIndex) {
        return subBytes(bytes, beginIndex, bytes.length - beginIndex);
    }

    public static byte[] subBytes(byte[] bytes, int beginIndex, int endIndex) {
        if (null == bytes || beginIndex >= bytes.length||endIndex>bytes.length)
            return null;
        byte[] copy = new byte[endIndex - beginIndex];
        System.arraycopy(bytes, beginIndex, copy, 0, copy.length);
        return copy;
    }

    public static boolean equal(byte[] bytes1, byte[] bytes2) {
        int length = bytes1.length;
        if (length != bytes2.length)
            return false;

        boolean equal = true;
        for (int i = 0; i < length; i++) {
            if (bytes1[i] != bytes2[i]) {
                equal = false;
                break;
            }
        }
        return equal;
    }
}
