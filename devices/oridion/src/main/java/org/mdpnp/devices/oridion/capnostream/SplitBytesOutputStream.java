package org.mdpnp.devices.oridion.capnostream;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SplitBytesOutputStream extends FilterOutputStream {

    public SplitBytesOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int h = 0, i = 0;

        for(i = 0; i < len; i++) {
            switch(0xFF&b[off+i]) {
            case 0x80:
                if(i > h) {
                    super.write(b, off+h, i-h);
                }
                super.write(0x80);
                super.write(0x00);
                h = i + 1;
                break;
            case 0x85:
                if(i > h) {
                    super.write(b, off+h, i-h);
                }
                super.write(0x80);
                super.write(0x05);
                h = i + 1;
                break;
            default:
            }
        }

        if(i > h) {
            super.write(b, off + h, i - h);
        }
    }

    @Override
    public void write(int b) throws IOException {
        switch(b) {
        case 0x80:
            super.write(0x80);
            super.write(0x00);
            break;
        case 0x85:
            super.write(0x80);
            super.write(0x05);
            break;
        default:
            super.write(b);
        }
    }

    public void writeHeader() throws IOException {
        super.write(0x85);
    }

}
