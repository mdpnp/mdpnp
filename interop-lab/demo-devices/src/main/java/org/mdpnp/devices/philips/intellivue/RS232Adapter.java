package org.mdpnp.devices.philips.intellivue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.MergeBytesInputStream;
import org.mdpnp.devices.io.SplitBytesOutputStream;
import org.mdpnp.devices.io.TeeInputStream;
import org.mdpnp.devices.io.TeeOutputStream;
import org.mdpnp.devices.io.util.HexUtil;
import org.mdpnp.devices.net.NetworkConnection;
import org.mdpnp.devices.net.NetworkLoop;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translates between serial frames and UDP datagrams
 *
 * A DatagramChannel is registered on the NetworkLoop which monitors
 * UDP datagrams.  When datagrams are received they are placed into writeToSerial.
 * When the channel is available for writing the contents of writeToChannel are written.
 *
 * A separate thread monitors the inbound side of the serial connection.  As frames are read
 * they are deposited into writeToChannel.
 *
 * A separate thread handles the outbound side of the serial connection.  Every 256ms it writes
 * up to four frames with contents from writeToSerial.  The Philips monitor seems to prefer this
 * to sparse commands and this technique ensures we do not send too many frames (the spec allows up to
 * 4 frames in a 128ms period so we stay far under the limit)
 * @author jplourde
 *
 */
public class RS232Adapter implements NetworkConnection {
    private static final int BOF = 0xC0;
    private static final int EOF = 0xC1;

    private final byte[] header = new byte[4];

    private final List<ByteBuffer> writeToChannel = new ArrayList<ByteBuffer>();
    private final List<ByteBuffer> writeToSerial = new ArrayList<ByteBuffer>();
    private final List<ByteBuffer> recycleBin = new ArrayList<ByteBuffer>();

    private final ByteBuffer newBuffer() {
        synchronized(recycleBin) {
            if(recycleBin.isEmpty()) {
                log.trace("ALLOCATING A NEW ByteBuffer");
                return ByteBuffer.allocate(2048);
            } else {
                ByteBuffer bb = recycleBin.remove(0);
                if(null == bb) {
                    log.trace("ALLOCATING A NEW ByteBuffer");
                    return ByteBuffer.allocate(2048);
                } else {
                    bb.clear();
                    return bb;
                }
            }
        }
    }

    private final void deleteBuffer(ByteBuffer bb) {
        if(null != bb) {
            synchronized(recycleBin) {
                recycleBin.add(bb);
            }
        }
    }

    private SocketAddress serialSideAddress;
    private SocketAddress remoteSideAddress;
    private final DatagramChannel channel;
    private final SelectionKey selectionKey;
    private final SerialSocket serialSocket;
    private final OutputStream serialOut;
    private final InputStream serialIn;

    private final ByteArrayOutputStream traceOut;
    private final ByteArrayOutputStream traceIn;

    private volatile boolean writingSerial = true;

    private static final Logger log = LoggerFactory.getLogger(RS232Adapter.class);

    public SocketAddress getRemoteSideAddress() {
        return remoteSideAddress;
    }
    public SocketAddress getSerialSideAddress() {
        return serialSideAddress;
    }

    private final Thread udpToSerial, serialToUDP;
    private final NetworkLoop networkLoop;

    public RS232Adapter(String serialPort, SocketAddress serialSideAddress, SocketAddress remoteSideAddress, ThreadGroup threadGroup, NetworkLoop networkLoop) throws IOException {
        this.networkLoop = networkLoop;
        this.serialSideAddress = serialSideAddress;
        this.remoteSideAddress = remoteSideAddress;
        SerialProvider sp = SerialProviderFactory.getDefaultProvider();
        sp.setDefaultSerialSettings(115200, DataBits.Eight, Parity.None, StopBits.One);
        serialSocket = sp.connect(serialPort, 1000L);

        if(log.isTraceEnabled()) {
            traceOut = new ByteArrayOutputStream();
            traceIn = new ByteArrayOutputStream();
            serialOut = new TeeOutputStream(serialSocket.getOutputStream(), traceOut);
            serialIn = new TeeInputStream(serialSocket.getInputStream(), traceIn);
        } else {
            traceOut = null;
            traceIn = null;
            serialOut = serialSocket.getOutputStream();
            serialIn = serialSocket.getInputStream();
        }


        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().setReuseAddress(true);
        channel.bind(serialSideAddress);
        this.serialSideAddress = channel.getLocalAddress();
        channel.connect(remoteSideAddress);

        selectionKey = networkLoop.register(this, channel);
        selectionKey.interestOps(SelectionKey.OP_READ);

        udpToSerial = new Thread(threadGroup, new Runnable() {
            public void run() {
                while(writingSerial) {
                    try {
                        processUDPToSerial();
                    } catch(Throwable t) {
                        if(writingSerial) {
                            log.error("serious error in processUDPToSerial, restarting", t);
                        } else {
                            log.debug("error but closing anyway", t);
                        }
                    }
                }
                log.info("Thread Ended");
            }
        }, "UDP->RS232");
        udpToSerial.setDaemon(true);
        udpToSerial.start();

        serialToUDP = new Thread(threadGroup, new Runnable() {
            public void run() {
                while(writingSerial) {
                    try {
                        processSerialToUDP();
                    } catch(Throwable t) {
                        if(writingSerial) {
                            log.error("serious error in processSerialToUDP, restarting", t);
                        } else {
                            log.debug("error but closing anyway", t);
                        }

                    }
                }
                log.info("Thread Ended");
            }
        }, "RS232->UDP");
        serialToUDP.setDaemon(true);
        serialToUDP.start();
    }

    public void shutdown() {
        networkLoop.unregister(selectionKey, this);
        writingSerial = false;

        try {
            serialSocket.close();
            channel.close();
        } catch (IOException e) {
            log.error("closing the serial port or channel", e);
        }
        try {
            serialToUDP.join(2000L);
            if(serialToUDP.isAlive()) {
                log.warn("RS232->UDP thread did not exit");
            }
            udpToSerial.join(2000L);
            if(udpToSerial.isAlive()) {
                log.warn("UDP->RS232 thread did not exit");
            }
        } catch (InterruptedException e) {
            log.error("interrupted", e);
        }
    }


    private int readFrame(FCSInputStream is, DatagramChannel channel) throws IOException {
        int count = 0;
        int r = 0;

        is.resetFCS();

        while(count < header.length) {
            r = is.read(header, count, header.length - count);
            if(r < 0) {
                return r;
            }
            count += r;
        }

        short protocolId = (short)(0xFF & header[0]);
        short msgType = (short)(0xFF & header[1]);
        int length = 0xFF00 & (header[2] << 8);
        length += (0xFF & header[3]);


        ByteBuffer body = newBuffer();

        if(length <= 0) {
            log.warn("Invalid frame length:"+length);
            return count;
        }

        body.limit(length);


        while(body.hasRemaining()) {
            r = is.read(body.array(), body.arrayOffset()+body.position(), body.remaining());
            if(r < 0) {
                return r;
            } else {
                count += r;
                if(r > body.remaining()) {
                    log.warn("Read of length=" + body.remaining() + " returned " + r + " bytes ... forced to extend the buffer limit");
                    body.limit(body.position()+r);
                }
                body.position(body.position()+r);
            }
        }

        int calculatedFCS = is.currentFCS();

        if((r = is.read()) < 0) {
            return r;
        }
        int receivedFCS = 0xFF & r;
        if((r = is.read()) < 0) {
            return r;
        }
        receivedFCS |= 0xFF00 & (r << 8);


        if(0x11 != protocolId) {
            log.warn("Unknown Protocol Id:"+Integer.toHexString(0xFF&protocolId));
            return count;
        }
        if(0x01 != msgType) {
            log.warn("Unknown message type:"+Integer.toHexString(0xFF&msgType));
            return count;
        }
        body.flip();

        if( (0xFFFF^receivedFCS) != calculatedFCS) {
            log.warn("Invalid CRC Received:"+Integer.toHexString((0xFFFF^receivedFCS)) + " but calculated: " + Integer.toHexString(calculatedFCS));
        }

        if(log.isTraceEnabled()) {
            log.trace("Received frame from RS232 len="+ body.remaining() + "\n" + HexUtil.dump(body, 50));
        }
        synchronized(writeToChannel) {
            writeToChannel.add(body);
        }
        selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
        return count;
    }

    protected void processSerialToUDP() {
        InputStream is = new MergeBytesInputStream(serialIn, 0xC0, 0xC1, 0x7D, new MergeBytesInputStream.Merger() {

            @Override
            public byte merge(byte b1, byte b2) {
                return (byte)(0x20 ^ b2);
            }

        });
        FCSInputStream fcsin = new FCSInputStream(is);
        int r = 0;
        boolean eof = false;

        try {
            while(!eof) {
                if(null != traceIn) {
                    traceIn.reset();
                }
                r = is.read();
                switch(r) {
                case MergeBytesInputStream.END_OF_FILE:
                    eof = true;
                    continue;
                case MergeBytesInputStream.BEGIN_FRAME:
                    switch(readFrame(fcsin, channel)) {
                    case MergeBytesInputStream.END_OF_FILE:
                        eof = true;
                        continue;
                    case MergeBytesInputStream.END_FRAME:
                        log.info("Aborted Frame");
                        break;
                    default:
                        if(MergeBytesInputStream.END_FRAME != is.read()) {
                            log.warn("Frame not properly ended");
                        }
                        break;
                    }

                    if(null != traceIn && log.isTraceEnabled()) {
                        byte[] bytes = traceIn.toByteArray();
                        log.trace("from raw RS232 len=" + bytes.length + "\n" + HexUtil.dump(bytes, 50));
                    }
                    break;
                default:
                    // Unknown byte
                }
            }
            try {
                fcsin.close();
            } catch (IOException e) {
                log.error("closing serial port", e);
            }
        } catch (IOException e) {
            log.debug("Reading serial", e);
        }
    }


    protected void processUDPToSerial() {
        final SplitBytesOutputStream sbos = new SplitBytesOutputStream(serialOut, new IntellivueByteSplitter());

        // FCS is calculated without escape sequences
        // This is closed elsewhere
        @SuppressWarnings("resource")
        final FCSOutputStream fcsout = new FCSOutputStream(sbos);

        ByteBuffer[] buffers = new ByteBuffer[0];

        while(writingSerial) {
            synchronized(writeToSerial) {
                buffers = writeToSerial.toArray(buffers);
                writeToSerial.clear();
            }
            if(traceOut != null) {
                traceOut.reset();
            }
            int i = 0;
            for(ByteBuffer bb : buffers) {
                if(null != bb) {
                    if(i++ < 4) {
                        int length = bb.remaining();
                        if(log.isTraceEnabled()) {
                            log.trace("Datagram len=" + length + "\n" + HexUtil.dump(bb, 50));
                        }
                        try {
                            serialOut.write(BOF);
                            fcsout.resetFCS();
                            fcsout.write(0x11);
                            fcsout.write(0x01);

                            fcsout.write( 0xFF & (length >> 8) );
                            fcsout.write( 0xFF & length);
                            fcsout.write(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining());
                            // CRC
                            fcsout.writeFCS();
                            serialOut.write(EOF);


                        } catch (IOException ioe) {
                            log.error("Writing to the serial", ioe);
                        } finally {
                            deleteBuffer(bb);
                        }
                    } else {
                        synchronized(writeToSerial) {
                            writeToSerial.add(bb);
                        }
                    }
                } else {
                    break;
                }
            }
            try {
                fcsout.flush();
            } catch (IOException e1) {
                log.error("flushing serial", e1);
            }
            if(log.isTraceEnabled()) {
                byte[] bytes = traceOut.toByteArray();
                if(bytes.length > 0) {
                    log.trace("to raw RS232 len=" + bytes.length + "\n" + HexUtil.dump(bytes, 50));
                }
            }
            try {
                Thread.sleep(256L);
            } catch (InterruptedException e) {
                log.warn("interrupted", e);
            }
        }
    }

    @Override
    public void read(SelectionKey sk) throws IOException {
        ByteBuffer bb = newBuffer();
        int n = channel.read(bb);
        if(n > 0) {
            bb.flip();
            log.debug("Read " + bb.remaining() + " bytes from UDP " + n);
            synchronized(writeToSerial) {
                writeToSerial.add(bb);
            }
        } else {
            deleteBuffer(bb);
        }

    }
    @Override
    public void write(SelectionKey sk) throws IOException {
        ByteBuffer toWrite = null;
        synchronized(writeToChannel) {
            if(!writeToChannel.isEmpty()) {
                toWrite = writeToChannel.remove(0);
            }
            if(writeToChannel.isEmpty()) {
                selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
            } else {
                selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
            }
        }

        if(null != toWrite) {
            int n = channel.write(toWrite);
            if(n > 0) {
                deleteBuffer(toWrite);
            } else {
                synchronized(writeToChannel) {
                    writeToChannel.add(0, toWrite);
                }
            }
        }
    }
    @Override
    public void registered(NetworkLoop networkLoop, SelectionKey sk) {

    }
    @Override
    public void unregistered(NetworkLoop networkLoop, SelectionKey sk) {

    }
}
