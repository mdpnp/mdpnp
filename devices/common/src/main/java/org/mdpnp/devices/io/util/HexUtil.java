package org.mdpnp.devices.io.util;

import java.nio.ByteBuffer;

public class HexUtil {
    private HexUtil() {

    }

    public static final char toHexChar(int b) {
        switch(0XF & b) {
        case 0xF:
            return 'F';
        case 0xE:
            return 'E';
        case 0xD:
            return 'D';
        case 0xC:
            return 'C';
        case 0xB:
            return 'B';
        case 0xA:
            return 'A';
        default:
            return (char)('0'+(0xF&b));

        }
    }

    public static final String toHexString(int b) {
        return ""+toHexChar(b>>4)+toHexChar(b);
    }

    public static final String dump(ByteBuffer bb) {
        return dump(bb, Integer.MAX_VALUE);
    }

    public static final String dump(byte[] bb, int bytesPerLine) {
        return dump(ByteBuffer.wrap(bb), bytesPerLine);
    }

    public static final String dump(ByteBuffer bb, int bytesPerLine) {
        StringBuilder sb = new StringBuilder("[");
        bb.mark();
        int c = 0;
        while(bb.hasRemaining()) {
            int b = 0xFF & bb.get();
            sb.append(toHexString(b)).append(" ");
            c++;
            if(c == bytesPerLine) {
                sb.append("\n ");
                c = 0;
            }
        }
        if(sb.length()>1) {
            sb.delete(sb.length()-1, sb.length());
        }
        bb.reset();
        sb.append("]");
        return sb.toString();
    }

    public static final boolean startsWith(ByteBuffer haystack, byte[] needle) {
        if(needle == null || needle.length == 0) {
            return true;
        }

        if(haystack.remaining() < needle.length) {
            return false;
        }

        for(int i = 0; i < needle.length; i++) {
            if(haystack.get(haystack.position()+i) != needle[i]) {
                return false;
            }
        }
        return true;
    }
    // CRUDE
    public static final boolean advancePast(ByteBuffer haystack, byte[][] needle) {
        while(haystack.hasRemaining()) {
            for(int i = 0; i < needle.length; i++) {
                if(startsWith(haystack, needle[i])) {
                    haystack.position(haystack.position()+needle[i].length);
                    return true;
                }
            }
            haystack.get();
        }
        return false;
    }

}
