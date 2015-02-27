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
import java.io.OutputStream;
import java.util.Arrays;

import org.mdpnp.devices.draeger.medibus.types.Command;
import org.mdpnp.devices.draeger.medibus.types.RealtimeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class RTMedibus extends Medibus {

    public RTMedibus(InputStream in, OutputStream out) throws IOException {
        super(in, out);
    }

    private static final int SYNC_BYTE = 0xD0;
    private static final int SYNC_MASK = 0xF0;
    private static final int SYNC_CMD_BYTE = 0xC0;
    private static final int RT_BYTE = 0x80;
    private static final int RT_BYTE_MASK = 0xC0;
    private static final int SC_END_OF_SEQUENCE = 0xC0;
    private static final int SC_DATASTREAM_1_4 = 0xC1;
    private static final int SC_DATASTREAM_5_8 = 0xC2;
    private static final int SC_DATASTREAM_9_12 = 0xC3;
    private static final int SC_TX_DATASTREAM_5_8 = 0xC4;
    private static final int SC_TX_DATASTREAM_9_12 = 0xC5;
    private static final int SC_START_CYCLE = 0xC6;
    private static final int SC_CORRUPT_DATA = 0xCF;

    private final boolean[] dataStreamEnabled = new boolean[12];
    private final boolean[] transmittedDataStreams = new boolean[12];
    private int dataCounter = 0;

    private static final Logger log = LoggerFactory.getLogger(RTMedibus.class);

    public void sendEnableRealtime(int[] traces) throws IOException {
        out.write(SYNC_BYTE);

        out.write(SC_DATASTREAM_9_12);
        int flags = SYNC_CMD_BYTE;
        for (int t : traces) {
            if (t > 7) {
                flags |= (0x0F & (1<<(t-8)));
            }
        }
        out.write(flags);

        out.write(SC_DATASTREAM_5_8);
        flags = SYNC_CMD_BYTE;
        for (int t : traces) {
            if (t > 3 && t <= 7) {
                flags |= (0x0F & (1<<(t-4)));
            }
        }
        out.write(flags);

        out.write(SC_DATASTREAM_1_4);
        flags = SYNC_CMD_BYTE;
        for (int t : traces) {
            if (t < 4) {
                flags |= (0x0F & (1<<t));
            }
        }
        out.write(flags);

        out.write(SYNC_CMD_BYTE);
        out.write(SC_END_OF_SEQUENCE);
        out.flush();
        log.debug("Enabled r/t " + Arrays.toString(traces) + " " + Integer.toHexString(flags));

    }

    public void receiveSyncByte(int syncByte) {
        dataCounter = 0;
        transmittedDataStreams[0] = 0 != (0x01 & syncByte);
        transmittedDataStreams[1] = 0 != (0x02 & syncByte);
        transmittedDataStreams[2] = 0 != (0x04 & syncByte);
        transmittedDataStreams[3] = 0 != (0x08 & syncByte);
        if(log.isTraceEnabled()) {
            log.trace("transmittedDataStreams(after sync byte):" + Arrays.toString(transmittedDataStreams));
        }
    }

    public void receiveSyncCommand(int command, int argument) {
        int offset = 0;

        switch (command) {
        case SC_DATASTREAM_9_12:
            offset += 4;
        case SC_DATASTREAM_5_8:
            offset += 4;
        case SC_DATASTREAM_1_4:
            dataStreamEnabled[offset + 0] = 0 != (0x01 & argument);
            dataStreamEnabled[offset + 1] = 0 != (0x02 & argument);
            dataStreamEnabled[offset + 2] = 0 != (0x04 & argument);
            dataStreamEnabled[offset + 3] = 0 != (0x08 & argument);
            if(log.isTraceEnabled()) {
                log.trace("dataStreamEnabled:" + Arrays.toString(dataStreamEnabled));
            }
            break;
        case SC_TX_DATASTREAM_9_12:
            offset += 4;
        case SC_TX_DATASTREAM_5_8:
            offset += 4;
            transmittedDataStreams[offset + 0] = 0 != (0x01 & argument);
            transmittedDataStreams[offset + 1] = 0 != (0x02 & argument);
            transmittedDataStreams[offset + 2] = 0 != (0x04 & argument);
            transmittedDataStreams[offset + 3] = 0 != (0x08 & argument);
            log.debug("transmittedDataStreams:" + Arrays.toString(transmittedDataStreams));
            break;
        case SC_START_CYCLE:
            if (0 != (0x01 & argument)) {
                startExpiratoryCycle();
            } else {
                startInspiratoryCycle();
            }
            break;
        case SC_CORRUPT_DATA:
            log.warn("Corrupt data record received");
            break;
        }

    }

    public void startInspiratoryCycle() {

    }

    public void startExpiratoryCycle() {

    }

    public RTTransmit[] getLastTransmitted() {
        return lastTransmitted;
    }

    public void receiveData(int first, int second) {
        // which of the transmitted streams is this
        int idx = dataCounter++;
        int i;

        for (i = 0; i < transmittedDataStreams.length; i++) {
            if (transmittedDataStreams[i]) {
                if (idx == 0) {
                    idx = i;
                    break;
                } else {
                    idx--;
                }
            }
        }

        int binval = (first & 0x3F) | ((second & 0x3F) << 6);
        if (this.lastTransmitted != null && idx < this.lastTransmitted.length) {
            RTDataConfig c = lastTransmitted[idx].rtDataConfig;
            if(null == c) {
                log.warn("cannot receive r/t value idx="+idx+" config is null");
            } else {
                receiveDataValue(c, lastTransmitted[idx].multiplier, idx, c.realtimeData, (1.0 * binval / c.maxbin) * (c.max - c.min) + c.min);
            }
        } else {
            log.warn("index " + idx + " was not requested in the realtime data");
//            receiveDataValue(null, 1, idx, (byte) idx, binval);
        }

    }

    public void receiveDataValue(RTDataConfig config, int multiplier, int streamIndex, Object realtimeData, double data) {
        log.debug("Received(" + Medibus.toString(realtimeData) + "):" + data);
    }

    
    private int lastFastByte = -1;
    @Override
    protected void fastByte(int b) {
        if(lastFastByte < 0) {
            // No previous byte
            switch(SYNC_MASK & b) {
            case SYNC_BYTE:
                receiveSyncByte(b);
                return;
            }
            lastFastByte = b;
        } else {
            if ((RT_BYTE_MASK & lastFastByte) == RT_BYTE) { 
                receiveData(lastFastByte, b);
                lastFastByte = -1;
            } else {
                switch(SYNC_MASK&lastFastByte) {
                case SYNC_CMD_BYTE:
                    receiveSyncCommand(lastFastByte, b);
                    lastFastByte = -1;
                    break;
                default:
                    log.warn("Unknown r/t byte:" + Integer.toHexString(lastFastByte));
                    lastFastByte = -1;
                    fastByte(b);
                    break;
                }
            }
            
        }
    }
    
//    public boolean receiveFast() throws IOException {
//        int leading = 0;
//        while (true) {
//            leading = fastIn.read();
//            if (leading < 0) {
//                return false;
//            }
//            if ((RT_BYTE_MASK & leading) == RT_BYTE) {
//                receiveData(leading, fastIn.read());
//            } else {
//                int syncMasked = SYNC_MASK & leading;
//                switch (syncMasked) {
//                case SYNC_BYTE:
//                    receiveSyncByte(leading);
//                    break;
//                case SYNC_CMD_BYTE:
//                    receiveSyncCommand(leading, fastIn.read());
//                    break;
//                default:
//                    log.warn("Unknown r/t byte:" + Integer.toHexString(leading));
//                }
//            }
//        }
//    }

    public static final class RTTransmit {

        public RTTransmit(Object realtimeData, int multiplier, RTDataConfig rtDataConfig) {
            this.realtimeData = realtimeData;
            this.multiplier = multiplier;
            this.rtDataConfig = rtDataConfig;
        }

        Object realtimeData;
        int multiplier;
        RTDataConfig rtDataConfig;

        @Override
        public String toString() {
            return "[realtimeData=" + Medibus.toString(realtimeData) + ", multiplier=" + multiplier + "]";
        }
    }

    public synchronized void sendRTTransmissionCommand(RTTransmit[] transmits) throws IOException {
        scratchpad.reset();
        for (int i = 0; i < transmits.length; i++) {
            if (transmits[i].realtimeData instanceof RealtimeData) {
                sendASCIIHex(scratchpad, ((RealtimeData) transmits[i].realtimeData).toByte());
            } else {
                sendASCIIHex(scratchpad, ((Integer) transmits[i].realtimeData).byteValue());
            }
            sendASCIIHex(scratchpad, (byte) transmits[i].multiplier);
        }
        this.lastTransmitted = transmits;
        sendCommand(Command.ConfigureRealtime, scratchpad);
    }

    public static final class RTDataConfig {
        public Object realtimeData;
        public int interval, min, max, maxbin;
        public int ordinal;

        @Override
        public String toString() {
            return "[code=" + Medibus.toString(realtimeData) + ", interval=" + interval + ",min=" + min + ",max=" + max + ",maxbin=" + maxbin + "]";
        }
    }

    // private RTDataConfig[] currentRTDataConfig;
    private RTTransmit[] lastTransmitted;

    protected static final int parseInt(final byte[] buf) {
        return parseInt(buf, 0, buf.length);
    }
    
    protected static final int parseInt(final byte[] buf, final int off, final int len) {
        int result = 0;
        int powOf10 = 0;
        int sign = 1;
        for(int i = off + len - 1; i >= off; i--) {
            if('.'==buf[i]) {
                // reset because we were to the right of a decimal and
                // that's irrelevant to an integer
                result = 0;
                powOf10 = 0;
                sign = 1;
            } else if(buf[i]>='0'&&buf[i]<='9') {
                result += (buf[i]-'0')*Math.pow(10.0, powOf10++);
            } else if(buf[i] == '-') {
                sign = -1;
            } else {
                // unknown digit
            }
        }
        return sign * result;
    }

    private void receiveRealtimeConfig(byte[] response, int len) throws CorruptMedibusException {
        // First byte is the command and the last two bytes are the checksum
        len -= 3;
        RTDataConfig[] rtDataConfig = new RTDataConfig[len / 23];
        for (int i = 0; i < rtDataConfig.length; i++) {
            rtDataConfig[i] = new RTDataConfig();
            rtDataConfig[i].realtimeData = RealtimeData.fromByteIf((byte) recvASCIIHex(response, 1 + i * 23));
            rtDataConfig[i].interval = parseInt(response, 1 + 23 * i + 2, 8);
            rtDataConfig[i].min = parseInt(response, 1 + 23 * i + 10, 5);
            rtDataConfig[i].max = parseInt(response, 1 + 23 * i + 15, 5);
            rtDataConfig[i].maxbin = recvASCIIHex(response, 1 + 23 * i + 20, 3);
            rtDataConfig[i].ordinal = i;
        }
        receiveRealtimeConfig(rtDataConfig);
    }

    protected void receiveRealtimeConfig(RTDataConfig[] config) {
        log.debug("RT Config");
        for (int i = 0; i < config.length; i++) {
            log.debug("\t" + config[i]);
        }
    }

    @Override
    protected void receiveResponse(byte[] response, int len) throws CorruptMedibusException {
        if (len < 1) {
            return;
        }
        Object cmdEcho = Command.fromByteIf(response[0]);
        if (cmdEcho instanceof Command) {
            switch ((Command) cmdEcho) {
            case ReqRealtimeConfig:
                receiveRealtimeConfig(response, len);
                break;
            case ConfigureRealtime:
                receiveRealtimeConfigSuccess();
                log.trace("Configure realtime succeeded");
                break;
            case RealtimeConfigChanged:
                break;
            default:
                super.receiveResponse(response, len);
            }
        } else {
            super.receiveResponse(response, len);
        }

    }

    protected void receiveRealtimeConfigSuccess() {

    }

    @Override
    protected void receiveCommand(byte[] argument, int len) throws IOException {
        if (len < 1) {
            return;
        }
        Object cmdCode = Command.fromByteIf(argument[0]);
        if (cmdCode instanceof Command) {
            switch ((Command) cmdCode) {
            case RealtimeConfigChanged:
                sendResponse(cmdCode);
                sendCommand(Command.ReqRealtimeConfig);
                break;
            case ConfigureRealtime:
            case ReqRealtimeConfig:
                sendResponse(cmdCode);
                break;
            default:
                super.receiveCommand(argument, len);
            }
        } else {
            super.receiveCommand(argument, len);
        }
    }

}
