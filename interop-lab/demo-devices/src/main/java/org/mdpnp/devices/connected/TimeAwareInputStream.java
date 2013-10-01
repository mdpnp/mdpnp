package org.mdpnp.devices.connected;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TimeAwareInputStream extends FilterInputStream {
	private long lastRead = 0L;
	
	public TimeAwareInputStream(InputStream in) {
		super(in);
	}
	
	@Override
	public int read() throws IOException {
		int r = super.read();
		if(r > 0) {
			lastRead = System.currentTimeMillis();
		}
		return r;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		int n = in.read(b);
		if(n > 0) {
			lastRead = System.currentTimeMillis();
		}
		return n;
	}
	public int read(byte[] b, int off, int len) throws IOException {
		int n = in.read(b, off, len);
		if(n > 0) {
			lastRead = System.currentTimeMillis();
		}
		return n;
	};
	public long getLastReadTime() {
		return lastRead;
	}
	public void promoteLastReadTime() {
	    this.lastRead = System.currentTimeMillis();
	}
}
