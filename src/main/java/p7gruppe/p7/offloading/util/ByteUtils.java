package p7gruppe.p7.offloading.util;

import java.nio.ByteBuffer;

public class ByteUtils {

    public static byte[] intToBytes(int x){
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(0, x);
        return buffer.array();
    }

    public static int bytesToInt(byte[] x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(x, 0, x.length);
        buffer.flip();//need flip
        return buffer.getInt();
    }

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}
