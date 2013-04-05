package org.mdpnp.devices.draeger.medibus;

import java.io.IOException;
import java.io.InputStream;

public class SuspendableInputStream extends java.io.FilterInputStream {

	private final int suspend, resume;
	private volatile boolean suspended;
	
	public SuspendableInputStream(int suspend, int resume, InputStream in) {
		super(in);
		this.suspend = suspend;
		this.resume = resume;
	}
	
	@Override
	public int read() throws IOException {
		while(true) {
			int r = super.read();
//			System.out.println("read " + Integer.toHexString(r));
			if(r == resume) {
				resume();
			} else if(r == suspend) {
				suspend();
			} else if(!suspended) {
				return r;
			}
		}		
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {
		int n = in.read(b, off, len);
		if(n <= 0) {
			return n;
		} else {
			for(int i = off; i < (off+n); i++) {
				int r = b[i];
				if(r == resume) {
					if( (i+1) < (off+n)) {
						System.arraycopy(b, i+1, b, i, 1);
					}
					n--;
					resume();
				} else if(r == suspend) {
					if( (i+1) < (off+n)) {
						System.arraycopy(b, i+1, b, i, 1);
					}
					n--;
					suspend();
				} else if(suspended) {
					if( (i+1) < (off+n)) {
						System.arraycopy(b, i+1, b, i, 1);
					}
					
					n--;
				}
			}
		}
		return n;
	}
	
	protected synchronized void suspend() {
		this.suspended = true;
		this.notifyAll();
	}
	
	protected synchronized void resume() {
		this.suspended = false;
		this.notifyAll();
	}
	
	public synchronized void suspended() {
		while(suspended) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

}
