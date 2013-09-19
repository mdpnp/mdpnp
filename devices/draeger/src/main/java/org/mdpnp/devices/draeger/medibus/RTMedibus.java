package org.mdpnp.devices.draeger.medibus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.mdpnp.devices.draeger.medibus.types.Command;
import org.mdpnp.devices.draeger.medibus.types.RealtimeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTMedibus extends Medibus {

    public RTMedibus(InputStream in, OutputStream out) {
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

        out.write(SC_DATASTREAM_1_4);
        int flags = SYNC_CMD_BYTE;
        for(int t : traces) {
            if(t > 7) {
                flags |= (0x0F & (t - 8 + 1));
            }
        }
        out.write(flags);

        out.write(SC_DATASTREAM_5_8);
        flags = SYNC_CMD_BYTE;
        for(int t : traces) {
            if(t > 3 && t <= 7) {
                flags |= (0x0F & (t - 4 + 1));
            }
        }
        out.write(flags);

        out.write(SC_DATASTREAM_1_4);
        flags = SYNC_CMD_BYTE;
        for(int t : traces) {
            if(t < 4) {
                flags |= (0x0F & (t + 1));
            }
        }
        out.write(flags);

        out.write(SYNC_CMD_BYTE);
        out.write(SC_END_OF_SEQUENCE);
        out.flush();
        log.debug("Enabled r/t " + Arrays.toString(traces) + " " + Integer.toHexString(flags));

    }

    public void receiveSyncByte(int syncByte) {
        for(int i = 0; i < transmittedDataStreams.length; i++) {
            transmittedDataStreams[i] = false;
        }
        dataCounter = 0;
        transmittedDataStreams[0] = 0 != (0x01 & syncByte);
        transmittedDataStreams[1] = 0 != (0x02 & syncByte);
        transmittedDataStreams[2] = 0 != (0x04 & syncByte);
        transmittedDataStreams[3] = 0 != (0x08 & syncByte);
    }

    public void receiveSyncCommand(int command, int argument) {
        int offset = 0;

        switch(command) {
        case SC_DATASTREAM_9_12:
            offset += 4;
        case SC_DATASTREAM_5_8:
            offset += 4;
        case SC_DATASTREAM_1_4:
            dataStreamEnabled[offset+0] = 0 != (0x01 & argument);
            dataStreamEnabled[offset+1] = 0 != (0x02 & argument);
            dataStreamEnabled[offset+2] = 0 != (0x04 & argument);
            dataStreamEnabled[offset+3] = 0 != (0x08 & argument);
            log.debug("dataStreamEnabled:"+Arrays.toString(dataStreamEnabled));
            break;
        case SC_TX_DATASTREAM_9_12:
            offset += 4;
        case SC_TX_DATASTREAM_5_8:
            offset += 4;
            transmittedDataStreams[offset+0] = 0 != (0x01 & argument);
            transmittedDataStreams[offset+1] = 0 != (0x02 & argument);
            transmittedDataStreams[offset+2] = 0 != (0x04 & argument);
            transmittedDataStreams[offset+3] = 0 != (0x08 & argument);
            log.debug("transmittedDataStreams:"+Arrays.toString(transmittedDataStreams));
            break;
        case SC_START_CYCLE:
            if(0 != (0x01 & argument)) {
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

    public void receiveData(int first, int second) throws IOException {
        // which of the transmitted streams is this
        int idx = dataCounter++;
        int i;

        for(i = 0; i < transmittedDataStreams.length; i++) {
            if(transmittedDataStreams[i]) {
                if(idx == 0) {
                    idx = i;
                    break;
                } else {
                    idx--;
                }
            }
        }

        int binval = (first & 0x3F) | ((second & 0x3F) << 6);
        if(this.lastTransmitted != null && idx < this.lastTransmitted.length) {
            RTDataConfig c = lastTransmitted[idx].rtDataConfig;
            receiveDataValue(c, lastTransmitted[idx].multiplier, idx, c.realtimeData, (1.0 * binval / c.maxbin) * (c.max - c.min) + c.min);
        } else {
            receiveDataValue(null, 1, idx, (byte)idx, binval);
        }

    }

    public void receiveDataValue(RTDataConfig config, int multiplier, int streamIndex, Object realtimeData, double data) {
        log.debug("Received(" + Medibus.toString(realtimeData) + "):"+data);
    }

    public boolean receiveFast() throws IOException {
        int leading = 0;
        while(true) {
            leading = fastIn.read();
            if(leading < 0) {
                return false;
            }
            if( (RT_BYTE_MASK & leading) == RT_BYTE) {
                receiveData(leading, fastIn.read());
            } else {
                int syncMasked = SYNC_MASK & leading;
                switch(syncMasked) {
                case SYNC_BYTE:
                    receiveSyncByte(leading);
                    break;
                case SYNC_CMD_BYTE:
                    receiveSyncCommand(leading, fastIn.read());
                    break;
                default:
                    log.warn("Unknown r/t byte:"+Integer.toHexString(leading));
                }
            }
        }
    }

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
            return "[realtimeData="+Medibus.toString(realtimeData)+", multiplier="+multiplier+"]";
        }
    }

    public void sendRTTransmissionCommand(RTTransmit[] transmits) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for(int i = 0; i < transmits.length; i++) {
            if(transmits[i].realtimeData instanceof RealtimeData) {
                sendASCIIHex(baos, ((RealtimeData)transmits[i].realtimeData).toByte());
            } else {
                sendASCIIHex(baos, ((Integer)transmits[i].realtimeData).byteValue());
            }
            sendASCIIHex(baos, (byte) transmits[i].multiplier);
        }
        this.lastTransmitted = transmits;
        sendCommand(Command.ConfigureRealtime, baos.toByteArray());
    }

    public static final class RTDataConfig {
        public Object realtimeData;
        public int interval, min, max, maxbin;
        public int ordinal;
        @Override
        public String toString() {
            return "[code="+Medibus.toString(realtimeData)+", interval="+interval+",min="+min+",max="+max+",maxbin="+maxbin+"]";
        }
    }

//    private RTDataConfig[] currentRTDataConfig;
    private RTTransmit[] lastTransmitted;

    private static final int parseInt(byte[] buf, int off, int len) {
        return Integer.parseInt(new String(buf, off, len).replaceAll(" ", ""));
    }

    private void receiveRealtimeConfig(byte[] response, int len) {
        RTDataConfig[] rtDataConfig = new RTDataConfig[len / 23];
        for(int i = 0; i < rtDataConfig.length; i++) {
            rtDataConfig[i] = new RTDataConfig();
            rtDataConfig[i].realtimeData = RealtimeData.fromByteIf((byte) recvASCIIHex(response, 1+i*23));
            rtDataConfig[i].interval = parseInt(response, 1+23*i+2, 8);
            rtDataConfig[i].min = parseInt(response, 1+23*i+10, 5);
            rtDataConfig[i].max = parseInt(response, 1+23*i+15, 5);
            rtDataConfig[i].maxbin = recvASCIIHex(response, 1+23*i+20, 3);
            rtDataConfig[i].ordinal = i;
        }
        receiveRealtimeConfig(rtDataConfig);
    }

    protected void receiveRealtimeConfig(RTDataConfig[] config) {
        log.debug("RT Config");
        for(int i = 0; i < config.length; i++) {
            log.debug("\t"+config[i]);
        }
    }

    @Override
    protected void receiveResponse(byte[] response, int len) {
        if(len < 1) {
            return;
        }
        Object cmdEcho = Command.fromByteIf(response[0]);
        if(cmdEcho instanceof Command) {
            switch((Command)cmdEcho) {
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
        if(len < 1) {
            return;
        }
        Object cmdCode = Command.fromByteIf(argument[0]);
        if(cmdCode instanceof Command) {
            switch((Command)cmdCode) {
            case RealtimeConfigChanged:
                sendResponse(cmdCode, null);
                sendCommand(Command.ReqRealtimeConfig);
                break;
            case ConfigureRealtime:
            case ReqRealtimeConfig:
                sendResponse(cmdCode, null);
                break;
            default:
                super.receiveCommand(argument, len);
            }
        } else {
            super.receiveCommand(argument, len);
        }
    }

}
