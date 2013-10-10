package org.mdpnp.devices.philips.intellivue;

import java.io.IOException;
import java.io.InputStream;

public class FCSInputStream extends java.io.FilterInputStream {

    protected FCSInputStream(InputStream in) {
        super(in);
    }

    private int fcs = FCSOutputStream.INITIAL_FCS_VALUE;

    public void resetFCS() {
        fcs = FCSOutputStream.INITIAL_FCS_VALUE;
    }

    public int currentFCS() {
        return fcs;
    }

    @Override
    public int read() throws IOException {
        int b = in.read();
        if(b >= 0) {
            fcs = FCSOutputStream.pppfcs(fcs, (byte) b);
        }
        return b;
    }
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    };
    public int read(byte[] b, int off, int len) throws IOException {
        int n = in.read(b, off, len);
        if(n >= 0) {
            fcs = FCSOutputStream.pppfcs(fcs, b, off, n);
        }
        return n;
    };
}
