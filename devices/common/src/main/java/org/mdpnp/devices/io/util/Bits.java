package org.mdpnp.devices.io.util;

import java.nio.ByteBuffer;

public class Bits {
    private Bits() {
        
    }
    public static long getUnsignedInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFFL) << 0L) |
           ((b[off + 2] & 0xFFL) << 8L) |
           ((b[off + 1] & 0xFFL) << 16L) |
           ((b[off + 0] & 0xFFL) << 24L);           
        
    }
    public static final short getUnsignedByte(ByteBuffer b) {
        return (short)(0xFF & b.get());
    }
    public static final int getUnsignedShort(ByteBuffer b) {
        return 0xFFFF & b.getShort();
    }
    public static final long getUnsignedInt(ByteBuffer b) {
        return 0xFFFFFFFFL & b.getInt();
    }
    public static final void putUnsignedByte(ByteBuffer b, short x) {
        b.put((byte)(0xFF & x));
    }
    public static final void putUnsignedShort(ByteBuffer b, int x) {
        b.putShort((short)(0xFFFF&x));
    }
    public static final void putUnsignedInt(ByteBuffer b, long x) {
        b.putInt((int)(0xFFFFFFFF & x));
    }
}
