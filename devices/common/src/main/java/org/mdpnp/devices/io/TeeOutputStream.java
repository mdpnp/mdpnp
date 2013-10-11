package org.mdpnp.devices.io;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeeOutputStream extends java.io.FilterOutputStream {

    private final OutputStream tee;

    public TeeOutputStream(OutputStream out, OutputStream tee) {
        super(out);
        this.tee = tee;
    }

    private static final Logger log = LoggerFactory.getLogger(TeeOutputStream.class);

    @Override
    public void write(byte[] b) throws IOException {
        try {
            tee.write(b);
        } catch (Throwable t) {
            log.warn("error writing to tee", t);
        }
        out.write(b);
    }
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try {
            tee.write(b, off, len);
        } catch (Throwable t) {
            log.warn("error writing to tee", t);
        }
        out.write(b, off, len);
    }
    @Override
    public void write(int b) throws IOException {
        try {
            tee.write(b);
        } catch (Throwable t) {
            log.warn("error writing to tee", t);
        }
        out.write(b);
    }


}
