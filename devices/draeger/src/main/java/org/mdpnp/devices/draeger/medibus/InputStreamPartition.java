/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices.draeger.medibus;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * demultiplexor that takes a single input stream and separates inbound data
 * into separate InputStreams based on Filters
 * 
 * Upon creation this class spawns a daemon thread that reads from the source
 * multiplexed data stream and delivers bytes, as they pass filters, to the
 * InputStreams available through getInputStream(int).
 * 
 * Data for each demuxed stream is maintained in a circular buffer. Overflow
 * behavior is currently undefined; data must be continuously drained from the
 * demuxed InputStreams.
 * 
 * @author Jeff Plourde
 * 
 */
public class InputStreamPartition implements Runnable {
    /**
     * Filter for evaluating the bytes of the muxed InputStream
     * 
     * @author Jeff Plourde
     * 
     */
    public interface Filter {
        boolean passes(int b);
        boolean createPipe();
    }

    private final Filter[] filters;
//    private final byte[][] buffers;
//    private final boolean[] notifyBuffers;
//    private final int[] nextRead, nextWrite;
    private final PipedInputStream[] streamsToRead;
    private final PipedOutputStream[] streamsToWrite;
    private final InputStream in;

    protected final static Logger log = LoggerFactory.getLogger(InputStreamPartition.class);

    private static final int CAPACITY = 8192;

//    private class PartitionedInputStream extends java.io.InputStream {
//
//        private final int idx;
//        private long lastDrainedAt = System.currentTimeMillis();
//
//        public PartitionedInputStream(int idx) {
//            this.idx = idx;
//        }
//        @Override
//        public int read(byte[] bytes, int off, int len) throws IOException {
//            synchronized (buffers[idx]) {
//                while (nextRead[idx] >= nextWrite[idx]) {
//                    try {
//                        buffers[idx].wait();
//                    } catch (InterruptedException e) {
//                        log.error(e.getMessage(), e);
//                    }
//                }
//                if (nextRead[idx] < 0) {
//                    return -1;
//                }
//                int n = nextWrite[idx]-nextRead[idx];
//                n = n < len ? n : len;
//                System.arraycopy(buffers[idx], nextRead[idx], bytes, off, n);
//                nextRead[idx]+=n;
//
//                long now = System.currentTimeMillis();
//                if(nextRead[idx] >= nextWrite[idx]) {
//                    lastDrainedAt = now;
//                }
//                if(now-lastDrainedAt>1000L) {
//                    log.warn("PartitionedInputStream hasn't been drained for " + (now-lastDrainedAt)+"ms");
//                }
//                
//                System.arraycopy(buffers[idx], nextRead[idx], buffers[idx], 0, nextWrite[idx] - nextRead[idx]);
//                nextWrite[idx] -= nextRead[idx];
//                nextRead[idx] = 0;
//                buffers[idx].notify();
//                return n;
//            }
//        }
//        @Override
//        public int read() throws IOException {
//            synchronized (buffers[idx]) {
//                while (nextRead[idx] >= nextWrite[idx]) {
//                    try {
//                        buffers[idx].wait();
//                    } catch (InterruptedException e) {
//                        log.error(e.getMessage(), e);
//                    }
//                }
//                if (nextRead[idx] < 0) {
//                    return -1;
//                }
//                // TODO this won't actually pass through a -1
//                int b = 0xFF & buffers[idx][nextRead[idx]++];
//                
//                long now = System.currentTimeMillis();
//                if(nextRead[idx] >= nextWrite[idx]) {
//                    lastDrainedAt = now;
//                }
//                if(now-lastDrainedAt>1000L) {
//                    log.warn("PartitionedInputStream hasn't been drained for " + (now-lastDrainedAt)+"ms");
//                }                
//                // log.trace("from buffers["+idx+"] copying from src=" +
//                // nextRead[idx] +
//                // " to buffers["+idx+"] dst=0 sizeof="+(nextWrite[idx]-nextRead[idx]));
//                System.arraycopy(buffers[idx], nextRead[idx], buffers[idx], 0, nextWrite[idx] - nextRead[idx]);
//                nextWrite[idx] -= nextRead[idx];
//                nextRead[idx] = 0;
//                buffers[idx].notify();
//                return b;
//            }
//        }
//    }

    private Thread processingThread;

    /**
     * Constructs an InputStream partition of the specified InputStream for the
     * specified Filters.
     * 
     * Subsequent calls to getInputStream(int) will return the demuxed stream
     * for each filter. This constructor spawns a daemon thread to handle the
     * muxed InputStream.
     * 
     * @param filters
     *            Each filter will create a demultiplexed InputStream that
     *            receives only bytes that pass that filter
     * @param in
     *            The multiplexed InputStream source
     * @throws IOException 
     */
    public InputStreamPartition(Filter[] filters, InputStream in) throws IOException {
        this.in = in;
        this.filters = filters;
//        this.buffers = new byte[filters.length][];
//        this.notifyBuffers = new boolean[filters.length];
//        this.streams = new InputStream[filters.length];
//        this.nextRead = new int[filters.length];
//        this.nextWrite = new int[filters.length];
//        for (int i = 0; i < buffers.length; i++) {
//            buffers[i] = new byte[CAPACITY];
//            streams[i] = new PartitionedInputStream(i);
//        }
        this.streamsToRead = new PipedInputStream[filters.length];
        this.streamsToWrite = new PipedOutputStream[filters.length];
        for(int i = 0; i < filters.length; i++) {
            if(filters[i].createPipe()) {
                this.streamsToRead[i] = new PipedInputStream(8192);
                this.streamsToWrite[i] = new PipedOutputStream(this.streamsToRead[i]);
            }
        }
        processingThread = new Thread(this);
        processingThread.setPriority(Thread.NORM_PRIORITY+1);
        processingThread.setDaemon(true);
        processingThread.start();
    }

    /**
     * Returns the daemon thread that is reading the multiplexed InputStream.
     * 
     * @return
     */
    public Thread getProcessingThread() {
        return processingThread;
    }

    /**
     * Returns the demultiplexed InputStream for the i'th Filter passed to the
     * constructor
     * 
     * @param i
     * @return
     */
    public InputStream getInputStream(int i) {
        return streamsToRead[i];
    }

    int read() throws IOException {
        int n = in.read(manybytes, 0, manybytes.length);

        if (n < 0) {
            for (int i = 0; i < streamsToWrite.length; i++) {
                if(streamsToWrite[i]!= null) {
                    streamsToWrite[i].close();
                }
//                synchronized (buffers[i]) {
//                    nextRead[i] = -1;
//                    buffers[i].notifyAll();
//                }
            }
        } else if (n == 0) {
            return 0;
        } else {
            for(int i = 0; i < n; i++) {
                for (int j = 0; j < filters.length; j++) {
                    if (filters[j].passes(0xFF&manybytes[i])) {
                        if(streamsToWrite[j] != null) {
                            streamsToWrite[j].write(manybytes[i]);
                        }
//                        synchronized (buffers[j]) {
//                            long giveup = System.currentTimeMillis() + 5000L;
//                            while (nextWrite[j] >= buffers[j].length) {
//                                if (System.currentTimeMillis() >= giveup) {
//                                    throw new IllegalStateException("Refusing to overflow buffer that is not being drained");
//                                }
//                                log.warn("Buffer full (nextWrite[" + j + "]=" + nextWrite[j] + " and buffer[" + j + "].length=" + buffers[j].length
//                                        + "; clumsily hoping someone drains the buffer");
//                                try {
//                                    buffers[j].notify();
//                                    buffers[j].wait(200);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            buffers[j][nextWrite[j]++] = (byte) manybytes[i];
//                            notifyBuffers[j] = true;
//                        }
                    }
                }
            }
//            for(int i = 0; i < notifyBuffers.length; i++) {
//                if(notifyBuffers[i]) {
//                    synchronized(buffers[i]) {
//                        buffers[i].notify();
//                    }
//                    notifyBuffers[i] = false;
//                }
//            }
        }
        return n;
    }

    /**
     * Ends the multiplexed InputStream reading thread
     */
    public void close() {
        processingThread.interrupt();
    }

    private final byte[] manybytes = new byte[CAPACITY];

    /**
     * reads bytes from the multiplexed InputStream, deposits them into the
     * buffer for the first matching demuxed InputStream, and notifies any
     * caller currently blocked on a read of the demuxed InputStream.
     */
    @Override
    public void run() {
        try {
            log.trace("InputStreamPartition processing begins");
            int n = 0;
            while (n >= 0 && !Thread.interrupted()) {
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
            for(int i = 0; i < this.streamsToWrite.length; i++) {
                try {
                    if(null != streamsToWrite[i]) {
                        streamsToWrite[i].close();
                    }
                } catch (IOException e) {
                    log.error("Unable to close writing side of the pipe", e);
                }
            }
//            for (int i = 0; i < buffers.length; i++) {
//                synchronized (buffers[i]) {
//                    nextRead[i] = -1;
//                    buffers[i].notifyAll();
//                }
//            }
        }
    }
}
