package org.mdpnp.devices.draeger.medibus;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputStreamPartition implements Runnable {
	public interface Filter {
		boolean passes(int b);
	}
	
	final Filter[] filters;
	final byte[][] buffers;
	final int[] nextRead, nextWrite;
	private final InputStream[] streams;
	private final InputStream in;
	
	private final static Logger log = LoggerFactory.getLogger(InputStreamPartition.class);
	
	private static final int CAPACITY = 8192 * 4;
	private class PartitionedInputStream extends java.io.InputStream {

		private final int idx;
		
		public PartitionedInputStream(int idx) {
			this.idx = idx;
		}
		
		@Override
		public int read() throws IOException {
			synchronized(buffers[idx]) {
				while(nextRead[idx] >= nextWrite[idx]) {
					try {
						buffers[idx].wait();
					} catch (InterruptedException e) {
						log.error(e.getMessage(), e);
					}
				}
				if(nextRead[idx]<0) {
					return -1;
				}
				// TODO this won't actually pass through a -1
				int b = 0xFF & buffers[idx][nextRead[idx]++];
				System.arraycopy(buffers[idx], nextRead[idx], buffers[idx], 0, nextWrite[idx]-nextRead[idx]);
				nextWrite[idx] -= nextRead[idx];
				nextRead[idx] = 0;
				return b;
			}
		}
	}
	
	private Thread processingThread;
	
	public InputStreamPartition(Filter[] filters, InputStream in) {
		this.in = in;
		this.filters = filters;
		this.buffers = new byte[filters.length][];
		this.streams = new InputStream[filters.length];
		this.nextRead = new int[filters.length];
		this.nextWrite = new int[filters.length];
		for(int i = 0; i < buffers.length; i++) {
			buffers[i] = new byte[CAPACITY];
			streams[i] = new PartitionedInputStream(i);
		}
		processingThread = new Thread(this);
		processingThread.setDaemon(true);
		processingThread.start();
	}
	public Thread getProcessingThread() {
		return processingThread;
	}
	public InputStream getInputStream(int i) {
		return streams[i];
	}
	public int read() throws IOException {
		int n = in.read(onebyte, 0, 1);
		
		if(n < 0) {
			for(int i = 0; i < buffers.length; i++) {
				synchronized(buffers[i]) {
					nextRead[i] = -1;
					buffers[i].notifyAll();
				}
			}
		} else if(n == 0) {
			return 0;
		} else {
			for(int i = 0; i < filters.length; i++) {
				if(filters[i].passes(onebyte[0])) {
					synchronized(buffers[i]) {
						buffers[i][nextWrite[i]++] = (byte) onebyte[0];
						buffers[i].notify();
					}
				}
			}
		}
		return n;
	}

	public void close() {
		processingThread.interrupt();
	}
	
	private final byte[] onebyte = new byte[1];
	
	@Override
	public void run() {
		try {
			log.trace("InputStreamPartition processing begins");
			int n = 0;
			while(n >= 0 && !Thread.interrupted()) {
				try {
					n = read();
				} catch (IOException e) {
					// I don't think we should continue on IOExceptions
					throw new RuntimeException(e);
				}
			}
		} finally {
			log.trace("InputStreamPartition processing ends");
			// show love to those who are waiting for this defunct thread
			for(int i = 0; i < buffers.length; i++) {
				synchronized(buffers[i]) {
					nextRead[i] = -1;
					buffers[i].notifyAll();
				}
			}
		}
		
		
	}
}
