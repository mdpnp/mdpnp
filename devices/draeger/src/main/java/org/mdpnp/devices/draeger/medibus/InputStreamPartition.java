package org.mdpnp.devices.draeger.medibus;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * demultiplexor that takes a single input stream and separates inbound
 * data into separate InputStreams based on Filters
 * 
 * Upon creation this class spawns a daemon thread that reads from the
 * source multiplexed data stream and delivers bytes, as they pass filters,
 * to the InputStreams available through getInputStream(int).
 * 
 * Data for each demuxed stream is maintained in a circular buffer.  Overflow
 * behavior is currently undefined; data must be continuously drained from 
 * the demuxed InputStreams.
 * 
 * @author jplourde
 *
 */
public class InputStreamPartition implements Runnable {
    /**
     * Filter for evaluating the bytes of the muxed InputStream
     * @author jplourde
     *
     */
	public interface Filter {
		boolean passes(int b);
	}
	
	private final Filter[] filters;
	private final byte[][] buffers;
	private final int[] nextRead, nextWrite;
	private final InputStream[] streams;
	private final InputStream in;
	
	private final static Logger log = LoggerFactory.getLogger(InputStreamPartition.class);
	
	private static final int CAPACITY = 8192 * 16;
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
//				log.trace("from buffers["+idx+"] copying from src=" + nextRead[idx] + " to buffers["+idx+"] dst=0 sizeof="+(nextWrite[idx]-nextRead[idx]));
				System.arraycopy(buffers[idx], nextRead[idx], buffers[idx], 0, nextWrite[idx]-nextRead[idx]);
				nextWrite[idx] -= nextRead[idx];
				nextRead[idx] = 0;
				return b;
			}
		}
	}
	
	private Thread processingThread;
	
	/**
	 * Constructs an InputStream partition of the specified InputStream for the
	 * specified Filters.
	 * 
	 * Subsequent calls to getInputStream(int) will return the demuxed stream for
	 * each filter.  This constructor spawns a daemon thread to handle the muxed
	 * InputStream.
	 * @param filters Each filter will create a demultiplexed InputStream that receives only bytes that pass that filter
	 * @param in The multiplexed InputStream source
	 */
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
	
	/**
	 * Returns the daemon thread that is reading the multiplexed InputStream.
	 * @return
	 */
	public Thread getProcessingThread() {
		return processingThread;
	}
	
	/**
	 * Returns the demultiplexed InputStream for the i'th Filter passed to the constructor
	 * @param i
	 * @return
	 */
	public InputStream getInputStream(int i) {
		return streams[i];
	}

	int read() throws IOException {
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
					    long giveup = System.currentTimeMillis() + 5000L;
					    while(nextWrite[i] >= buffers[i].length) {
					        if(System.currentTimeMillis()>=giveup) {
					            throw new IllegalStateException("Refusing to overflow buffer that is not being drained");
					        }
					        log.warn("Buffer full (nextWrite["+i+"]="+nextWrite[i]+" and buffer["+i+"].length="+buffers[i].length+"; clumsily hoping someone drains the buffer");
					        try {
					            buffers[i].notify();
					            buffers[i].wait(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
					    }
						buffers[i][nextWrite[i]++] = (byte) onebyte[0];
						buffers[i].notify();
					}
				}
			}
		}
		return n;
	}

	/**
	 * Ends the multiplexed InputStream reading thread
	 */
	public void close() {
		processingThread.interrupt();
	}
	
	private final byte[] onebyte = new byte[1];
	
	/**
	 * reads bytes from the multiplexed InputStream, deposits them into the buffer
	 * for the first matching demuxed InputStream, and notifies any caller currently
	 * blocked on a read of the demuxed InputStream.
	 */
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
