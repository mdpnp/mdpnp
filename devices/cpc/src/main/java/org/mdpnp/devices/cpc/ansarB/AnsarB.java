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
package org.mdpnp.devices.cpc.ansarB;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.mdpnp.devices.DeviceClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class AnsarB {

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final DeviceClock  deviceClock;

    public AnsarB(DeviceClock  deviceClock, InputStream in, OutputStream out) {
        this.inputStream = in;
        this.outputStream = out;
        this.deviceClock = deviceClock;
    }

    private static final Logger log = LoggerFactory.getLogger(AnsarB.class);

    private final byte[] buffer = new byte[2048];
    private int length;

    private final static int ECG_PTS = 200, RESP_PTS = 50, PLETH_PTS = 50, P1_PTS = 50, P2_PTS = 50;
    private final static int ECG_OFF = 0, RESP_OFF = 200, PLETH_OFF = 250, P1_OFF = 300, P2_OFF = 350;
    private final static int ECG_OFFSET = 100, RESP_OFFSET = 0, PLETH_OFFSET = 86, P1_OFFSET = 80, P2_OFFSET = 251;
    private final static int ECG_FREQUENCY = 200, RESP_FREQUENCY = 50, PLETH_FREQUENCY = 50, P1_FREQUENCY = 50, P2_FREQUENCY = 50;

    private Float[] wavedata = new Float[ECG_PTS];
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
        for (int i = 0; i < length; i++) {
            if (buffer[i] == START_BYTE) {
                messageStartIndex = i;
                break;
            }
        }

        // Pull the data up to the front of the buffer if necessary
        if (messageStartIndex > 0) {
            System.arraycopy(buffer, messageStartIndex, buffer, 0, length - messageStartIndex);
            length -= messageStartIndex;
            messageStartIndex = 0;
        }

        return messageStartIndex;
    }

    private final int messageEndIndex(int messageStartIndex) {
        if (messageStartIndex >= 0 && (length - messageStartIndex) > FIXED_LENGTH) {
            // Look for a message terminator
            for (int i = messageStartIndex + FIXED_LENGTH; i < length; i++) {
                if (buffer[i] == END_BYTE) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean receive() throws IOException {
        int b = 0;
        int msgStart, msgEnd;

        for (;;) {

            while ((msgStart = messageStartIndex()) >= 0 && (msgEnd = messageEndIndex(msgStart)) >= 0) {
                // log.info("msgStart="+msgStart+" msgEnd="+msgEnd+" buffer[msgStart]="+Integer.toHexString(buffer[msgStart])+" buffer[msgEnd]="+Integer.toHexString(buffer[msgEnd]));
                receiveMessage(buffer, msgStart, msgEnd - msgStart);
                length -= (msgEnd+1);
                if(length > 0) {
                    System.arraycopy(buffer, msgEnd+1, buffer, 0, length);
                }
            }

            // Read some bytes into the buffer
            b = inputStream.read(buffer, length, buffer.length - length);
            if (b < 0) {
                log.info("Received EOF reading AnsarB stream");
                return false;
            } else {
                length += b;
            }
        }
    }


    protected void receiveHeartRate(DeviceClock.Reading timeStamp, Integer value, String label, String alarm) {

    }

    protected void receiveRespiratoryRate(DeviceClock.Reading timeStamp, Integer value, String label, String alarm) {

    }

    protected void receiveEndTidalCO2(DeviceClock.Reading timeStamp, Integer value, String label, String alarm) {

    }

    protected void receiveSpO2(DeviceClock.Reading timeStamp, Integer value, String label, Integer pulseRate, String alarm) {

    }

    protected void receivePressure1(DeviceClock.Reading timeStamp, Integer systolic, Integer diastolic, Integer mean, String label, String alarm) {

    }

    protected void receiveNIBP(DeviceClock.Reading timeStamp, Integer systolic, Integer diastolic, Integer mean, Integer pulse, String label, String alarm) {

    }

    protected void receivePressure2(DeviceClock.Reading timeStamp, Integer systolic, Integer diastolic, Integer mean, String label, String alarm) {

    }

    protected void receiveTemperature1(DeviceClock.Reading timeStamp, Float value, String label, String alarm) {

    }

    protected void receiveTemperature2(DeviceClock.Reading timeStamp, Float value, String label, String alarm) {

    }

    protected void receiveECGWave(DeviceClock.Reading timeStamp, Float[] data, int count, int frequency, String label) {

    }

    protected void receiveRespWave(DeviceClock.Reading timeStamp, Float[] data, int count, int frequency) {

    }

    protected void receivePlethWave(DeviceClock.Reading timeStamp, Float[] data, int count, int frequency) {

    }

    protected void receiveP1Wave(DeviceClock.Reading timeStamp, Float[] data, int count, int frequency) {

    }

    protected void receiveP2Wave(DeviceClock.Reading timeStamp, Float[] data, int count, int frequency) {

    }

    protected void receiveLine(DeviceClock.Reading timeStamp, String line) {
        String[] fields = line.split(";", -1);
        if (fields.length > 0) {
            String[] name = fields[0].split("=");
            if (name.length > 0 && fields.length > 3) {
                if ("HR".equals(name[0])) {
                    receiveHeartRate(timeStamp, parseIntOrNull(fields[1]), ecgLabel = fields[2], fields[3]);
                } else if ("RR".equals(name[0])) {
                    receiveRespiratoryRate(timeStamp, parseIntOrNull(fields[1]), fields[2], fields[3]);
                } else if ("ETCO2".equals(name[0])) {
                    receiveEndTidalCO2(timeStamp, parseIntOrNull(fields[1]), fields[2], fields[3]);
                } else if ("SPO2".equals(name[0])) {
                    String[] label = fields[2].split("=");
                    receiveSpO2(timeStamp, parseIntOrNull(fields[1]), fields[2], label.length > 1 ? parseIntOrNull(label[1]) : null, fields[3]);
                } else if ("NIBP".equals(name[0]) && fields.length > 6) {
                    receiveNIBP(timeStamp, parseIntOrNull(fields[1]), parseIntOrNull(fields[2]), parseIntOrNull(fields[3]), parseIntOrNull(fields[4]),
                            fields[5], fields[6]);
                } else if ("P1".equals(name[0]) && fields.length > 5) {
                    receivePressure1(timeStamp, parseIntOrNull(fields[1]), parseIntOrNull(fields[2]), parseIntOrNull(fields[3]),
                            fields[4], fields[5]);
                } else if ("P2".equals(name[0]) && fields.length > 3) {
                    receivePressure2(timeStamp, parseIntOrNull(fields[1]), parseIntOrNull(fields[2]), parseIntOrNull(fields[3]),
                            fields[4], fields[5]);
                } else if ("T1".equals(name[0]) && fields.length > 1) {
                    receiveTemperature1(timeStamp, parseFloatOrNull(fields[1]), fields[2], fields[3]);
                } else if ("T2".equals(name[0]) && fields.length > 1) {
                    receiveTemperature2(timeStamp, parseFloatOrNull(fields[1]), fields[2], fields[3]);
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
        if (s.isEmpty()) {
            return null;
        }
        try {

            return Float.parseFloat(s);
        } catch (NumberFormatException nfe) {
            log.warn("Badly formatted number:" + s);
            return null;
        }
    }

    private static final Integer parseIntOrNull(String s) {
        s = s.trim();
        if (s.isEmpty()) {
            return null;
        }
        try {

            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            log.warn("Badly formatted number:" + s);
            return null;
        }
    }

    private static final void offset(Float[] dest, byte[] source, int source_offset, int data_offset, int length, float scale) {
        for (int i = 0; i < length; i++) {
            dest[i] = scale * ((0xFF & source[source_offset + i]) - data_offset);
        }
    }

    public boolean receiveMessage(byte[] message, int off, int len) throws IOException {

        DeviceClock.Reading timeStamp = deviceClock.instant();

        int last = off + FIXED_LENGTH;
        for (int i = off + FIXED_LENGTH; i < (len - 1); i++) {
            if (message[i] == '\r') {
                // Now there is a line from last to (i - last - 1)
                // We've put off decoding it long enough
                String line = new String(message, last, i - last, ASCII).intern();
                receiveLine(timeStamp, line);
                last = i + 1;
            }
        }

        // ECG Wave
        offset(wavedata, message, off + ANSAR_B.length + 1 + ECG_OFF, ECG_OFFSET, ECG_PTS, 0.02f);
        receiveECGWave(timeStamp, wavedata, ECG_PTS, ECG_FREQUENCY, ecgLabel);
        offset(wavedata, message, off + ANSAR_B.length + 1 + RESP_OFF, RESP_OFFSET, RESP_PTS, 1f);
        receiveRespWave(timeStamp, wavedata, RESP_PTS, RESP_FREQUENCY);
        offset(wavedata, message, off + ANSAR_B.length + 1 + PLETH_OFF, PLETH_OFFSET, PLETH_PTS, 1f);
        receivePlethWave(timeStamp, wavedata, PLETH_PTS, PLETH_FREQUENCY);
        offset(wavedata, message, off + ANSAR_B.length + 1 + P1_OFF, P1_OFFSET, P1_PTS, 1f);
        receiveP1Wave(timeStamp, wavedata, P1_PTS, P1_FREQUENCY);
        offset(wavedata, message, off + ANSAR_B.length + 1 + P2_OFF, P2_OFFSET, P2_PTS, 1f);
        receiveP2Wave(timeStamp, wavedata, P2_PTS, P2_FREQUENCY);

        return true;

    }

}
