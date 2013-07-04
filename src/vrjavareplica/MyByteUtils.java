/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.nio.ByteBuffer;

/**
 *
 * @author karol
 */
public class MyByteUtils {
    
    public static byte[] toByteArray(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        //buffer.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
        buffer.putInt(value);
        byte[] result = buffer.array();
        return result;
    }
    
    public static byte[] toByteArray(String text) {
        return text.getBytes();
    }
    
    public static byte[] toByteArray(boolean value) {
        return new byte[]{(byte) (value ? 1 : 0)};
    }
    
    public static int byteArrayToInt(byte[] bytes) {
        final ByteBuffer bb = ByteBuffer.wrap(bytes);
        //bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }
    
    public static boolean byteArrayToBoolean(byte[] bytes) {
        return (bytes[0]!=0);
    }
}
