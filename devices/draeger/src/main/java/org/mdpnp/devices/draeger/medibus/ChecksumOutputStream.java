package org.mdpnp.devices.draeger.medibus;

import java.io.IOException;
import java.io.OutputStream;

public class ChecksumOutputStream extends java.io.FilterOutputStream {

    public ChecksumOutputStream(OutputStream out) {
        super(out);
    }
    
    private int checksum = 0;
    
    @Override
    public void write(int b) throws IOException {
        checksum += 0xFF & b;
        super.write(b);
    }
    
    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
    }
    
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
    }
    
    public void resetChecksum() {
        checksum = 0;
    }
    
    public int getChecksum() {
        return checksum;
    }

}
