package org.mdpnp.devices.puritanbennett._840;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

public abstract class PB840 {
    protected final BufferedReader in;
    protected final OutputStream out;
    
    public PB840(InputStream in, OutputStream out) {
        this.in = new BufferedReader(new InputStreamReader(in, Charset.forName("ASCII")));;
        this.out = out;
    }
    public abstract boolean receive() throws IOException;
}
