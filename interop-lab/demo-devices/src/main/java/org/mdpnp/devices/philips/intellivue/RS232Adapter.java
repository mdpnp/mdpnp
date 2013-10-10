package org.mdpnp.devices.philips.intellivue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.DatagramChannel;

import org.mdpnp.devices.io.MergeBytesInputStream;
import org.mdpnp.devices.io.SplitBytesOutputStream;
import org.mdpnp.devices.io.util.HexUtil;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RS232Adapter {
    private static final int BOF = 0xC0;
    private static final int EOF = 0xC1;

    private final byte[] header = new byte[4];
    private final ByteBuffer body = ByteBuffer.allocate(8192);

    private SocketAddress serialSideAddress;
    private SocketAddress remoteSideAddress;
    private final DatagramChannel channel;
    private final SerialSocket serialSocket;
    private final OutputStream serialOut;
    private final InputStream serialIn;

    private static final Logger log = LoggerFactory.getLogger(RS232Adapter.class);

    public SocketAddress getRemoteSideAddress() {
        return remoteSideAddress;
    }
    public SocketAddress getSerialSideAddress() {
        return serialSideAddress;
    }

    private final Thread udpToSerial, serialToUDP;

    public RS232Adapter(String serialPort, SocketAddress serialSideAddress, SocketAddress remoteSideAddress, ThreadGroup threadGroup) throws IOException {
        this.serialSideAddress = serialSideAddress;
        this.remoteSideAddress = remoteSideAddress;
        SerialProvider sp = SerialProviderFactory.getDefaultProvider();
        sp.setDefaultSerialSettings(115200, DataBits.Eight, Parity.None, StopBits.One);
        serialSocket = sp.connect(serialPort, 1000L);
        serialOut = new BufferedOutputStream(serialSocket.getOutputStream());
        serialIn = new BufferedInputStream(serialSocket.getInputStream());

        channel = DatagramChannel.open();
        channel.socket().setReuseAddress(true);
        channel.bind(serialSideAddress);
        this.serialSideAddress = channel.getLocalAddress();
        if(null != remoteSideAddress) {
            channel.connect(remoteSideAddress);
        }

        udpToSerial = new Thread(threadGroup, new Runnable() {
            public void run() {
                try {
                    processUDPToSerial();
                } finally {
                    log.warn("Thread Ended");
                }
            }
        }, "UDP->RS232");
        udpToSerial.setDaemon(true);
        udpToSerial.start();

        serialToUDP = new Thread(threadGroup, new Runnable() {
            public void run() {
                try {
                    processSerialToUDP();
                } finally {
                    log.warn("Thread Ended");
                }
            }
        }, "RS232->UDP");
        serialToUDP.setDaemon(true);
        serialToUDP.start();
    }

//    public void start() {
//
//    }

    public void shutdown() {
        try {
            serialSocket.close();
            serialToUDP.join(2000L);
            if(serialToUDP.isAlive()) {
                log.warn("RS232->UDP thread did not exit");
            }
        } catch (IOException e) {
            log.error("closing the serial port", e);
        } catch (InterruptedException e) {
            log.error("interrupted", e);
        }
        try {
            channel.close();
            udpToSerial.join(2000L);
            if(udpToSerial.isAlive()) {
                log.warn("UDP->RS232 thread did not exit");
            }
        } catch (IOException e) {
            log.error("closing UDP channel", e);
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
                return -1;
            }
            count += r;
        }

        short protocolId = (short)(0xFF & header[0]);
        short msgType = (short)(0xFF & header[1]);
        int length = 0xFF00 & (header[2] << 8);
        length += (0xFF & header[3]);

        body.clear();

        body.limit(length);

        while(body.hasRemaining()) {
            r = is.read(body.array(), body.arrayOffset()+body.position(), body.remaining());
            if(r < 0) {
                return -1;
            } else {
                count += r;
                body.position(body.position()+r);
            }
        }

        int calculatedFCS = is.currentFCS();

        if((r = is.read()) < 0) {
            return -1;
        }
        int receivedFCS = 0xFF & r;
        if((r = is.read()) < 0) {
            return -1;
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
        body.mark();
        channel.write(body);
        body.reset();
        if(log.isTraceEnabled()) {
            log.trace("Got a frame:"+HexUtil.dump(body));
        }
        // TODO check for trailer validity .. CRC .. etc
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
                r = is.read();
                switch(r) {
                case MergeBytesInputStream.END_OF_FILE:
                    eof = true;
                    continue;
                case MergeBytesInputStream.BEGIN_FRAME:
                    readFrame(fcsin, channel);
                    if(MergeBytesInputStream.END_FRAME != is.read()) {
                        log.warn("Frame not properly ended");
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
        long lastMessage = 0L;

        // FCS is calculated without escape sequences
        final FCSOutputStream fcsout = new FCSOutputStream(sbos);
        ByteBuffer bb = ByteBuffer.allocate(8192);
        while(channel.isOpen()) {
            SocketAddress addr;
            try {
                addr = channel.receive(bb);
                if(channel.isConnected()) {
                    if(null == remoteSideAddress || !addr.equals(remoteSideAddress)) {
                        log.warn("Previously connected to " + channel.getRemoteAddress());
                        channel.disconnect();
                    }
                }

                if(!channel.isConnected()) {
                    remoteSideAddress = addr;
                    log.info("Connected to " + remoteSideAddress);
                    channel.connect(remoteSideAddress);
                }

                bb.flip();
                if(log.isTraceEnabled()) {
                    log.trace("Got a datagram from " + addr + " " + HexUtil.dump(bb, 50));
                }
                long now = System.currentTimeMillis();
                while(now-lastMessage<128L) {
                    try {
                        Thread.sleep(lastMessage+128L-now);
                        log.trace("sleep");
                        now = System.currentTimeMillis();
                    } catch (InterruptedException e) {
                        log.warn("Interrupted", e);
                    }
                }

                if(!channel.isConnected()) {
                    channel.connect(addr);
                }
                serialOut.write(BOF);
                fcsout.resetFCS();
                fcsout.write(0x11);
                fcsout.write(0x01);
                int length = bb.remaining();
                // TODO is this little or big endian?
                // TODO does this get escapes or not?
                fcsout.write( 0xFF & (length >> 8) );
                fcsout.write( 0xFF & length);
                fcsout.write(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining());
                // CRC
                fcsout.writeFCS();
                serialOut.write(EOF);
                fcsout.flush();
                lastMessage = System.currentTimeMillis();
                log.trace("Wrote the datagram of length " + length);
            } catch (AsynchronousCloseException ace) {
                log.debug("The channel was closed", ace);
            } catch (IOException e) {
                log.error("writing datagram", e);
            }
        }
//        try {
//            fcsout.close();
//        } catch (IOException e) {
//            log.error("Closing the FCSOutputStream", e);
//        }
    }

    public static void main(String[] args) throws IOException {
        String portId = "cu.PL2303-00002014";
        new RS232Adapter(portId, new InetSocketAddress(InetAddress.getLoopbackAddress(), Intellivue.DEFAULT_UNICAST_PORT), null, null);
    }
}
