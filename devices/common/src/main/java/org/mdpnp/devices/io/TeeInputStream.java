package org.mdpnp.devices.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeeInputStream extends FilterInputStream {
    private final OutputStream out;

    public TeeInputStream(InputStream in, OutputStream out) {
        super(in);
        this.out = out;
    }

    private final static Logger log = LoggerFactory.getLogger(TeeInputStream.class);

    @Override
    public int read() throws IOException {
        int n = super.read();
        if(n >= 0) {
            try {
                out.write(n);
            } catch (Throwable t) {
                log.warn("error writing to tee", t);
            }
        }
        return n;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = in.read(b, off, len);

        if(n >= 0) {
            try {
                out.write(b, off, n);
            } catch (Throwable t) {
                log.warn("error writing to tee", t);
            }
        }

        return n;
    }

}
