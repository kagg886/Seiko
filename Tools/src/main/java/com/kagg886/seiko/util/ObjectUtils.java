package com.kagg886.seiko.util;

/**
 * @author kagg886
 * @date 2023/6/2 19:30
 **/
public class ObjectUtils {
    public static void requireNull(Object... obj) {
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] == null) {
                throw new NullPointerException("arg[" + i + "] is null!");
            }
        }
    }

    public static byte[] copyInto(byte[] src, byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        System.arraycopy(src, startIndex, destination, destinationOffset, endIndex - startIndex);
        return destination;
    }

    //public actual fun ByteArray.copyInto(destination: ByteArray, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = size): ByteArray {
    //    System.arraycopy(this, startIndex, destination, destinationOffset, endIndex - startIndex)
    //    return destination
    //}
}
