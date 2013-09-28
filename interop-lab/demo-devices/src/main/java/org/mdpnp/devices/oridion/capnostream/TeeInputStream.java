package org.mdpnp.devices.oridion.capnostream;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TeeInputStream extends FilterInputStream {
    private final OutputStream out;

    protected TeeInputStream(InputStream in, OutputStream out) {
        super(in);
        this.out = out;
    }

    @Override
    public int read() throws IOException {
        int n = super.read();
        if(n >= 0) {
            out.write(n);
        }
        return n;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        for(int i = 0; i < len; i++) {
            int n = read();
            if(n < 0) {
                return n;
            } else {
                b[off + i] = (byte) n;
            }
        }
        return len;
    }

}
