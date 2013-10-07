package org.mdpnp.devices.oridion.capnostream;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MergeBytesInputStream extends java.io.FilterInputStream {
        public static final int HEADER = -2;
        public static final int EOF = -1;

        private final Logger log = LoggerFactory.getLogger(MergeBytesInputStream.class);

        public MergeBytesInputStream(java.io.InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            len = super.read(b, off, len);

            if(len < 0) {
                return EOF;
            }
            int n;
            for(int i = 0; i < len; i++) {
                switch(0xFF&b[off+i]) {
                case 0x85:
                    return HEADER;
                case 0x80:
                    if( (i+1) >= len) {
                        // Read an extra byte to complete the 2-byte sequence
                        n = super.read();
                        if(n < 0) {
                            return n;
                        }
                        b[off+i] += n;
                    } else {
                        // Add the second byte to the first
                        b[off+i] += b[off+i+1];
                        if( (i+2)<len ) {
                            System.arraycopy(b, off+i+2, b, off+i+1, len - (off + i + 2));
                        }
                        len--;
                    }
                }
            }
            return len;
        }

        @Override
        public int read() throws IOException {
            int b1;
            int b = super.read();
            if(b < 0) {
                return EOF;
            } else {
                switch(b) {
                case 0x85:
                    return HEADER;
                case 0x80:
                    b1 = super.read();
                    if(b1 < 0) {
                        return EOF;
                    } else {
                        return (0x80 + b1);
                    }
                default:
                    return b;
                }
            }
        }
    }