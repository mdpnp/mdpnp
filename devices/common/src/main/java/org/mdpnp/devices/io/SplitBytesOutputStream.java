package org.mdpnp.devices.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class SplitBytesOutputStream extends FilterOutputStream {

    public interface Splitter {
        int split(byte b, byte[] b1);
    }

    private final Splitter splitter;
    private final byte[] buffer = new byte[8];

    public SplitBytesOutputStream(OutputStream out) {
        this(out, new Splitter() {

            @Override
            public int split(byte b, byte[] b1) {
                switch(0xFF & b) {
                case 0x80:
                    b1[0] = (byte) 0x80;
                    b1[1] = 0x00;
                    return 2;
                case 0x85:
                    b1[0] = (byte) 0x80;
                    b1[1] = (byte) 0x05;
                    return 2;
                default:
                    b1[0] = b;
                    return 1;
                }
            }

        });
    }

    public SplitBytesOutputStream(OutputStream out, Splitter splitter) {
        super(out);
        this.splitter = splitter;
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int h = 0, i = 0;

        for(i = 0; i < len; i++) {
            int j = splitter.split(b[off+i], buffer);
            if(j > 1) {
                if(i > h) {
                    out.write(b, off+h, i-h);
                }
                out.write(buffer, 0, j);
                h = i + 1;
            }
        }
        if(i > h) {
            out.write(b, off + h, i - h);
        }
    }

    @Override
    public void write(int b) throws IOException {
        int i = splitter.split((byte)b, buffer);
        for(int h = 0; h < i; h++) {
            out.write(0xFF&buffer[h]);
        }
    }

    public void writeProtected(int b) throws IOException {
        out.write(b);
    }

}
