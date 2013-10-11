package org.mdpnp.devices.io;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MergeBytesInputStream extends java.io.FilterInputStream {
        public static final int BEGIN_FRAME = -2;
        public static final int END_FRAME = -3;

        public static final int END_OF_FILE = -1;

        private static final Logger log = LoggerFactory.getLogger(MergeBytesInputStream.class);

        private final int beginFrame, endFrame, escape;
        private final Merger merger;

        public interface Merger {
            byte merge(byte b1, byte b2);
        }

        public MergeBytesInputStream(java.io.InputStream inputStream) {
            this(inputStream, 0x85, -1, 0x80, new Merger() {

                @Override
                public byte merge(byte b1, byte b2) {
                    log.trace("Merge " + Integer.toHexString(0xFF&b1) + " + " + Integer.toHexString(0xFF&b2) + " yields " + Integer.toHexString(0xFF&((byte)(b1+b2))));
                    return (byte)(b1 + b2);
                }

            });
        }

        public MergeBytesInputStream(java.io.InputStream inputStream, int beginFrame, int endFrame, int escape, Merger merger) {
            super(inputStream);
            this.beginFrame = beginFrame;
            this.endFrame = endFrame;
            this.escape = escape;
            this.merger = merger;
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            len = in.read(b, off, len);

            if(len < 0) {
                return END_OF_FILE;
            }
            int n;
            for(int i = 0; i < len; i++) {
                final int x = 0xFF&b[off+i];
                if(x == beginFrame) {
                    return BEGIN_FRAME;
                } else if(x == endFrame) {
                    return END_FRAME;
                } else if(x == escape){
                    if( (i+1) >= len) {
                        // Read an extra byte to complete the 2-byte sequence
                        n = in.read();
                        if(n < 0) {
                            return n;
                        }
                        if(n == endFrame) {
                            return END_FRAME;
                        }
                        byte merged = merger.merge(b[off+i], (byte)n);
                        log.trace("Merged " + Integer.toHexString(0xFF & b[off+i]) + " + " + Integer.toHexString(0xFF & n) + " => " + Integer.toHexString(0xFF&merged));
                        b[off+i] = merged;

                    } else {
                        if(b[off+i+1] == endFrame) {
                            return END_FRAME;
                        }

                        // Add the second byte to the first
                        byte merged = merger.merge(b[off+i], b[off+i+1]);
                        log.trace("Merged " + Integer.toHexString(0xFF & b[off+i]) + " + " + Integer.toHexString(0xFF & b[off + i + 1]) + " => " + Integer.toHexString(0xFF&merged));
                        b[off+i] = merged;
                        if( (i+2)<len ) {
                            System.arraycopy(b, off+i+2, b, off+i+1, len - (i + 2));
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
            final int b = in.read();
            if(b < 0) {
                return END_OF_FILE;
            } else {
                if(b == beginFrame) {
                    return BEGIN_FRAME;
                } else if(b == endFrame) {
                    return END_FRAME;
                } else if(b == escape) {
                    b1 = in.read();
                    if(b1 < 0) {
                        return END_OF_FILE;
                    } else if(b1 == endFrame) {
                        return END_FRAME;
                    } else {
                        return 0xFF & merger.merge((byte)b, (byte)b1);
                    }
                } else {
                    return b;
                }
            }
        }
    }