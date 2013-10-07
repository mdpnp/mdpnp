package org.mdpnp.devices.cpc.ansarB;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnsarB {

    private final InputStream inputStream;
    private final OutputStream outputStream;

    public AnsarB(InputStream in, OutputStream out) {
        this.inputStream = in;
        this.outputStream = out;
    }

    private static final Logger log = LoggerFactory.getLogger(AnsarB.class);

    private final byte[] buffer = new byte[2048];
    private int length;

    private final static int ECG_PTS = 200, RESP_PTS = 50, PLETH_PTS = 50, P1_PTS = 50, P2_PTS = 50;
    private final static int ECG_OFF = 0, RESP_OFF = 200, PLETH_OFF = 250, P1_OFF = 300, P2_OFF = 350;
    private final static int ECG_OFFSET = 118, RESP_OFFSET = 0, PLETH_OFFSET = 86, P1_OFFSET = 80, P2_OFFSET = 251;
    private final static int ECG_PERIOD = 5, RESP_PERIOD = 20, PLETH_PERIOD = 20, P1_PERIOD = 20, P2_PERIOD = 20;

    private int[] wavedata = new int[ECG_PTS];
    private String ecgLabel;
    private final static Charset ASCII = Charset.forName("ASCII");
    private final static int WAVEFORM_LENGTH = 400;
    private final static String ANSAR_B_STR = "ANSAR-B ";
    private final static byte[] ANSAR_B = ANSAR_B_STR.getBytes(ASCII);
    private final static byte START_BYTE = (byte) 0xFF;
    private final static byte END_BYTE = (byte) 0xFE;
    private final static int FIXED_LENGTH = WAVEFORM_LENGTH + ANSAR_B.length + 1;

    private final int messageStartIndex() {
        int messageStartIndex = -1;
        // Look for the magical 0xFF to start a message
        for(int i = 0; i < length; i++) {
            if(buffer[i] == START_BYTE) {
                messageStartIndex = i;
                break;
            }
        }

        // Pull the data up to the front of the buffer if necessary
        if(messageStartIndex > 0) {
            System.arraycopy(buffer, messageStartIndex, buffer, 0, length - messageStartIndex);
            length -= messageStartIndex;
            messageStartIndex = 0;
        }

        return messageStartIndex;
    }

    private final int messageEndIndex(int messageStartIndex) {
        if(messageStartIndex >= 0 && (length - messageStartIndex) > FIXED_LENGTH) {
            // Look for a message terminator
            for(int i = messageStartIndex + FIXED_LENGTH; i < length; i++) {
                if(buffer[i] == END_BYTE) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean receive() throws IOException {
        int b = 0;
        int msgStart, msgEnd;

        for(;;) {

            while( (msgStart = messageStartIndex()) >= 0 && (msgEnd = messageEndIndex(msgStart)) >= 0) {
//                log.info("msgStart="+msgStart+" msgEnd="+msgEnd+" buffer[msgStart]="+Integer.toHexString(buffer[msgStart])+" buffer[msgEnd]="+Integer.toHexString(buffer[msgEnd]));
                receiveMessage(buffer, msgStart, msgEnd - msgStart);
                length = length - ( msgEnd - msgStart + 1);
                System.arraycopy(buffer, msgEnd, buffer, 0, length);
            }

            // Read some bytes into the buffer
            b = inputStream.read(buffer, length, buffer.length - length);
            if(b<0) {
                log.info("Received EOF reading AnsarB stream");
                return false;
            } else {
                length += b;
            }
        }
    }
    protected void receiveHeartRate(Integer value, String label) {

    }
    protected void receiveRespiratoryRate(Integer value, String label) {

    }
    protected void receiveEndTidalCO2(Integer value, String label) {

    }
    protected void receiveSpO2(Integer value, String label, Integer pulseRate) {

    }
    protected void receivePressure1(Integer systolic, Integer diastolic, Integer mean, String label) {

    }
    protected void receiveNIBP(Integer systolic, Integer diastolic, Integer mean, Integer pulse, String label) {

    }
    protected void receivePressure2(Integer systolic, Integer diastolic, Integer mean, String label) {

    }
    protected void receiveTemperature1(Float value, String label) {

    }
    protected void receiveTemperature2(Float value, String label) {

    }

    protected void receiveECGWave(int[] data, int count, int msPerSample, String label) {

    }
    protected void receiveRespWave(int[] data, int count, int msPerSample) {

        }
    protected void receivePlethWave(int[] data, int count, int msPerSample) {

    }
    protected void receiveP1Wave(int[] data, int count, int msPerSample) {

    }
    protected void receiveP2Wave(int[] data, int count, int msPerSample) {

    }
    protected void receiveLine(String line) {
        String[] fields = line.split(";");
        if(fields.length > 0) {
            String[] name = fields[0].split("=");
            if(name.length > 0) {
                if("HR".equals(name[0]) && fields.length > 2) {
                    receiveHeartRate(parseIntOrNull(fields[1]), ecgLabel = fields[fields.length - 1]);
                } else if("RR".equals(name[0]) && fields.length > 2) {
                    receiveRespiratoryRate(parseIntOrNull(fields[1]), fields[fields.length-1]);
                } else if("ETCO2".equals(name[0]) && fields.length > 2) {
                    receiveEndTidalCO2(parseIntOrNull(fields[1]), fields[fields.length-1]);
                } else if("SPO2".equals(name[0]) && fields.length > 2) {
                    String[] label = fields[fields.length-1].split("=");
                    receiveSpO2(parseIntOrNull(fields[1]), fields[fields.length-1], label.length > 1 ? parseIntOrNull(label[1]) : null);
                } else if("NIBP".equals(name[0]) && fields.length > 5) {
                    receiveNIBP(parseIntOrNull(fields[1]), parseIntOrNull(fields[2]), parseIntOrNull(fields[3]), parseIntOrNull(fields[4]), fields[fields.length-1]);
                } else if("P1".equals(name[0]) && fields.length > 3) {
                    receivePressure1(parseIntOrNull(fields[1]), parseIntOrNull(fields[2]), parseIntOrNull(fields[3]), fields.length > 4 ? fields[fields.length-1] : "");
                } else if("P2".equals(name[0]) && fields.length > 3) {
                    receivePressure2(parseIntOrNull(fields[1]),  parseIntOrNull(fields[2]), parseIntOrNull(fields[3]), fields.length > 4 ? fields[fields.length-1] : "");
                } else if("T1".equals(name[0]) && fields.length > 1) {
                    receiveTemperature1(parseFloatOrNull(fields[1]), fields[fields.length-1]);
                } else if("T2".equals(name[0]) && fields.length > 1) {
                    receiveTemperature2(parseFloatOrNull(fields[1]), fields[fields.length-1]);
                } else {
                    log.debug("Nothing to do for line: " + line);
                }
            } else {
                log.debug("Nothing to do for line: " + line);
            }
        } else {
            log.debug("Nothing to do for line: " + line);
        }
    }

    private static final Float parseFloatOrNull(String s) {
        s = s.trim();
        if(s.isEmpty()) {
            return null;
        }
        try {

            return Float.parseFloat(s);
        } catch (NumberFormatException nfe) {
            log.warn("Badly formatted number:"+s);
            return null;
        }
    }


    private static final Integer parseIntOrNull(String s) {
        s = s.trim();
        if(s.isEmpty()) {
            return null;
        }
        try {

            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            log.warn("Badly formatted number:"+s);
            return null;
        }
    }

    private static final void offset(int[] dest, byte[] source, int source_offset, int data_offset, int length) {
        for(int i = 0; i < length; i++) {
            dest[i] = (0xFF & source[source_offset + i])  + data_offset;
        }
    }

    public boolean receiveMessage(byte[] message, int off, int len) throws IOException {
        int last = off + FIXED_LENGTH;
        for(int i = off + FIXED_LENGTH; i < (len - 1); i++) {
            if(message[i] == '\r') {
                // Now there is a line from last to (i - last - 1)
                // We've put off decoding it long enough
                String line = new String(message, last, i - last - 1, ASCII).intern();
                receiveLine(line);
                last = i + 1;
            }
        }

        // ECG Wave
        offset(wavedata, message, off + ANSAR_B.length + 1 + ECG_OFF, ECG_OFFSET, ECG_PTS);
        receiveECGWave(wavedata, ECG_PTS, ECG_PERIOD, ecgLabel);
        offset(wavedata, message, off + ANSAR_B.length + 1 + RESP_OFF, RESP_OFFSET, RESP_PTS);
        receiveRespWave(wavedata, RESP_PTS, RESP_PERIOD);
        offset(wavedata, message, off + ANSAR_B.length + 1 + PLETH_OFF, PLETH_OFFSET, PLETH_PTS);
        receivePlethWave(wavedata, PLETH_PTS, PLETH_PERIOD);
        offset(wavedata, message, off + ANSAR_B.length + 1 + P1_OFF, P1_OFFSET, P1_PTS);
        receiveP1Wave(wavedata, P1_PTS, P1_PERIOD);
        offset(wavedata, message, off + ANSAR_B.length + 1 + P2_OFF, P2_OFFSET, P2_PTS);
        receiveP2Wave(wavedata, P2_PTS, P2_PERIOD);

        return true;


    }

}
