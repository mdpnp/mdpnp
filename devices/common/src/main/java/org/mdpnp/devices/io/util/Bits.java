/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices.io.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Jeff Plourde
 *
 */
public class Bits {
    private Bits() {

    }

    public static final long getUnsignedInt(byte[] b) {
        return getUnsignedInt(b, 0);
    }

    public static final long getUnsignedInt2(byte[] b, int off) {
        return (0xFF000000L & (b[off + 0] << 24L)) | (0x00FF0000L & (b[off + 1] << 16L)) | (0x0000FF00L & (b[off + 2] << 8L))
                | (0x000000FFL & (b[off + 3] << 0L));
    }

    public static long getUnsignedInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFFL) << 0L) | ((b[off + 2] & 0xFFL) << 8L) | ((b[off + 1] & 0xFFL) << 16L) | ((b[off + 0] & 0xFFL) << 24L);

    }

    public static final short getUnsignedByte(ByteBuffer b) {
        return (short) (0xFF & b.get());
    }

    public static final int getUnsignedShort(ByteBuffer b) {
        return 0xFFFF & b.getShort();
    }

    public static final long getUnsignedInt(ByteBuffer b) {
        return 0xFFFFFFFFL & b.getInt();
    }

    public static final void putUnsignedByte(ByteBuffer b, short x) {
        b.put((byte) (0xFF & x));
    }

    public static final void putUnsignedShort(ByteBuffer b, int x) {
        b.putShort((short) (0xFFFF & x));
    }

    public static final void putUnsignedInt(ByteBuffer b, long x) {
        b.putInt((int) (0xFFFFFFFF & x));
    }

    public static void main(String[] args) {

        final int N = 10000000;
        byte[] b = new byte[] { 0x01, 0x00, 0x00, 0x00 };

        long two = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            if (16777216L != getUnsignedInt2(b, 0)) {
                System.out.print("FAIL");
            }
        }
        System.out.println("Style 2 took " + (System.currentTimeMillis() - two) + "ms");

        long one = System.currentTimeMillis();

        for (int i = 0; i < N; i++) {
            if (16777216L != getUnsignedInt(b, 0)) {
                System.out.print("FAIL");
            }
        }
        System.out.println("Style 1 took " + (System.currentTimeMillis() - one) + "ms");

    }

    public static void putUnsignedInt(byte[] bytes, long address) {
        ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).putInt((int) (0xFFFFFFFF & address));
    }
}
