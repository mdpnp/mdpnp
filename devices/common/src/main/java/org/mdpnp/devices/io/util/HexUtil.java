package org.mdpnp.devices.io.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

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

    public static final byte[] itob(int[] ii) {
        byte[] bb = new byte[ii.length];
        for(int i = 0; i < ii.length; i++) {
            bb[i] = (byte) ii[i];
        }
        return bb;
    }

    public static final String dump(int[] ii) {
        return dump(itob(ii));
    }

    public static final String dump(int[] ii, int bytesPerLine) {
        return dump(itob(ii), bytesPerLine);
    }

    public static final String dump(int[] ii, int bytesPerLine, String charset) {
        return dump(itob(ii), bytesPerLine, charset);
    }

    public static final String dump(byte[] bb) {
        return null == bb ? null : dump(ByteBuffer.wrap(bb));
    }

    public static final String dump(byte[] bb, int bytesPerLine) {
        return null == bb ? null : dump(ByteBuffer.wrap(bb), bytesPerLine);
    }

    public static final String dump(byte[] bb, int bytesPerLine, String charset) {
        return null == bb ? null : dump(ByteBuffer.wrap(bb), bytesPerLine, charset);
    }

    public static final String dump(ByteBuffer bb, int bytesPerLine) {
        return dump(bb, bytesPerLine, null);
    }

    public static final String dump(ByteBuffer bb, int bytesPerLine, String charset) {
        StringBuilder sb = new StringBuilder("[");
        StringBuilder lineEncoder = null == charset ? null : new StringBuilder();
        CharsetDecoder cd = null == charset ? null : Charset.forName(charset).newDecoder();
        CharBuffer charBuffer = null == charset ? null : CharBuffer.allocate(1);
        bb.mark();
        int c = 0;
        while(bb.hasRemaining()) {
            if(null != charset) {
                int pos = bb.position();
                cd.decode(bb, charBuffer, true);
                bb.position(pos);
                charBuffer.clear();
                char ch = charBuffer.array()[0];
                ch = Character.isLetterOrDigit(ch) ? ch : ' ';
                lineEncoder.append(ch).append(" ");
            }

            int b = 0xFF & bb.get();
            sb.append(toHexString(b)).append(" ");

            c++;
            if(c == bytesPerLine) {
                if(null != charset) {
                    sb.append(" ").append(lineEncoder.toString());
                    lineEncoder.delete(0, lineEncoder.length());
                }
                sb.append("\n ");
                c = 0;
            }
        }
        if(sb.length()>1) {
            sb.delete(sb.length()-1, sb.length());
        }

        bb.reset();
        sb.append("]");

        if (null != charset) {
            sb.append(" ").append(lineEncoder.toString());
        }
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
