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
package org.mdpnp.devices.nonin.pulseox;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;

import org.mdpnp.devices.io.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class NoninPulseOx {

    private final InputStream in;
    private final OutputStream out;
    private final Logger log = LoggerFactory.getLogger(NoninPulseOx.class);

    public NoninPulseOx(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;

        nextArrival = new Arrival();
        Arrival current = null, previous = nextArrival;
        for (int i = 1; i < SAMPLE_SIZE; i++) {
            current = new Arrival();
            previous.setNext(current);
            previous = current;
        }
        current.setNext(nextArrival);
    }

    public Boolean isArtifact() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getCurrentStatus().isArtifact();
    }

    public Boolean isRedPerfusion() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getCurrentStatus().isRedPerfusion();
    }

    public Boolean isGreenPerfusion() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getCurrentStatus().isGreenPerfusion();
    }

    public Boolean isYellowPerfusion() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getCurrentStatus().isYellowPerfusion();
    }

    public Integer getAvgHeartRateFourBeat() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getAvgHeartRateFourBeat();
    }

    public Integer getFirmwareRevision() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getFirmwareRevision();
    }

    public Integer getTimer() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getTimer();
    }

    public Boolean isSmartPoint() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.isSmartPoint();
    }

    public Boolean isLowBattery() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.isLowBattery();
    }

    public Integer getAvgSpO2FourBeat() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getAvgSpO2FourBeat();
    }

    public Integer getAvgSpO2FourBeatFast() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getAvgSpO2FourBeatFast();
    }

    public Integer getSpO2BeatToBeat() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getSpO2BeatToBeat();
    }

    public Integer getAvgHeartRateEightBeat() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getAvgHeartRateEightBeat();
    }

    public Integer getAvgSpO2EightBeat() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getAvgSpO2EightBeat();
    }

    public Integer getAvgSpO2EightBeatForDisplay() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getAvgSpO2EightBeatForDisplay();
    }

    public Integer getAvgHeartRateFourBeatForDisplay() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getAvgHeartRateFourBeatForDisplay();
    }

    public Integer getAvgHeartRateEightBeatForDisplay() {
        Packet packet = getCurrentPacket();
        return null == packet ? null : packet.getAvgHeartRateEightBeatForDisplay();
    }

    public static final int FREQUENCY = 3 * Packet.FRAMES;
    public static final double MILLISECONDS_PER_SAMPLE = 1000.0 / FREQUENCY;

    public Integer getFrequency() {
        return FREQUENCY;
    }

    private final Packet currentPacket = new Packet();

    private Arrival nextArrival;
    private static final int SAMPLE_SIZE = 30;

    private double packetsPerSecond;
    private final Status status = new Status();
    private final Date date = new Date();

    public Date getTimestamp() {
        date.setTime(currentPacket.getFrameTime());
        return date;
    }

    protected static final byte OPCODE_SETFORMAT = 0x70;
    protected static final byte OPCODE_SETDATETIME = 0x72;
    protected static final byte OPCODE_GETSERIAL = 0x74;
    protected static final byte OPCODE_SETBLUETOOTHTIMEOUT = 0x75;
    protected static final byte OPCODE_RECVDATETIME = (byte) 0xF2;
    protected static final byte OPCODE_RECVSERIAL = (byte) 0xF4;
    protected static final byte OPCODE_BLUETOOTHTIMEOUT_RESPONSE = (byte) 0xF5;

    protected void sendOperation(byte opCode, byte[] data) throws IOException {
        sendOperation(opCode, data, 0, data.length);
    }

    // public void sendAck(boolean success) throws IOException {
    // log.debug("Wrote:"+(success?"0x06":"0x15"));
    // out.write(success?0x06:0x15);
    // out.flush();
    // }

    protected void sendOperation(byte opCode, byte[] data, int off, int len) throws IOException {
        byte[] buf = new byte[4 + len];
        buf[0] = 0x02;
        buf[1] = opCode;
        buf[2] = (byte) len;
        System.arraycopy(data, off, buf, 3, len);
        buf[3 + len] = 0x03;

        if (log.isTraceEnabled()) {
            log.trace("Wrote:" + HexUtil.dump(buf));
        }
        out.write(buf);
        out.flush();
    }

    protected void sendSetBluetoothTimeout(int minutes) throws IOException {
        if (minutes < 0 || minutes > 255) {
            throw new IllegalArgumentException("Invalid minutes value:" + minutes);
        }
        sendOperation(OPCODE_SETBLUETOOTHTIMEOUT, new byte[] { 0x04, 0x00, (byte) (0xFF & minutes), (byte) (0x04 + 0x00 + minutes) });
    }

    protected void sendGetDateTime() throws IOException {
        sendOperation(OPCODE_SETDATETIME, new byte[0]);
    }

    protected void sendSetDateTime(Date date) throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        sendOperation(OPCODE_SETDATETIME,
                new byte[] { (byte) (cal.get(Calendar.YEAR) - 2000), (byte) (cal.get(Calendar.MONTH) + 1), (byte) cal.get(Calendar.DAY_OF_MONTH),
                        (byte) cal.get(Calendar.HOUR_OF_DAY), (byte) cal.get(Calendar.MINUTE), (byte) cal.get(Calendar.SECOND) });
    }

    protected void sendGetSerial() throws IOException {
        sendGetSerial(2);
    }

    protected void sendGetSerial(int id) throws IOException {
        sendOperation(OPCODE_GETSERIAL, new byte[] { (byte) id, (byte) id });
    }

    protected void sendSetFormat(int format, boolean spotCheckMode, boolean bluetoothEnabledAtPowerOn) throws IOException {
        byte[] msg = new byte[] { 0x02, (byte) format, 0x01, (byte) (OPCODE_SETFORMAT + 4 + 2 + format) };
        msg[2] |= spotCheckMode ? 0x40 : 0x00;
        msg[2] |= bluetoothEnabledAtPowerOn ? 0x20 : 0x00;
        msg[3] += msg[2];
        sendOperation(OPCODE_SETFORMAT, msg);
    }

    protected void sendSetFormat(int format) throws IOException {
        byte[] msg = new byte[] { 0x02, (byte) format };
        sendOperation(OPCODE_SETFORMAT, msg);
    }

    public boolean readyFlag = false;

    private static class Arrival {
        private Arrival next;
        private long tm;

        public void setNext(Arrival next) {
            this.next = next;
        }

        public void setTm(long tm) {
            this.tm = tm;
        }

        public Arrival getNext() {
            return next;
        }

        public long getTm() {
            return tm;
        }
    }

    protected synchronized void recvAcknowledged(boolean success) {
        log.info(success ? "ACK" : "NAK");
    }

    protected synchronized void receiveBluetoothTimeoutResponse(boolean success) {
        log.info(success ? "Bluetooth Timeout Success" : "Bluetooth Timeout Failure");

    }

    protected synchronized void receiveSerialNumber(String serial) {
        log.info("Serial Number Received:" + serial);
    }

    protected synchronized void receiveDateTime(Date date) {
        log.info("DateTime:" + date);
    }

    protected synchronized void recvOperation(byte opCode, byte[] source, int off, int len) {
        switch (opCode) {
        case OPCODE_RECVSERIAL:
            receiveSerialNumber(new String(source, off + 1, 9));
            break;
        case OPCODE_BLUETOOTHTIMEOUT_RESPONSE:
            receiveBluetoothTimeoutResponse(source[off + 1] == 0);
            break;
        case OPCODE_RECVDATETIME: {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2000 + source[0]);
            cal.set(Calendar.MONTH, source[1] - 1);
            cal.set(Calendar.DAY_OF_MONTH, source[2]);
            cal.set(Calendar.HOUR_OF_DAY, source[3]);
            cal.set(Calendar.MINUTE, source[4]);
            cal.set(Calendar.SECOND, source[5]);
            cal.set(Calendar.MILLISECOND, 0);
            receiveDateTime(cal.getTime());
        }
            break;
        default:
            log.warn("Unknown incoming operation code:" + Integer.toHexString(0xFF & opCode));
        }
    }

    protected void frameError(String msg) {
        if (readyFlag) {
            log.error("frameError:" + msg);
        }
    }

    private final boolean consumeControl(byte[] buffer, int[] len) throws IOException {
        // Special control characters
        switch (buffer[0]) {
        case 0x02:
            // we don't yet know the size
            if (len[0] < 3) {
                return true;
            }
            int oplen = buffer[2] + 4;
            // we know the size and need more bytes
            if (len[0] < oplen) {
                return true;
            }
            // receive the operation
            recvOperation(buffer[1], buffer, 3, buffer[2]);

            System.arraycopy(buffer, oplen, buffer, 0, len[0] - oplen);
            len[0] -= oplen;
            return false;
        case 0x06:
            recvAcknowledged(true);
            System.arraycopy(buffer, 1, buffer, 0, len[0] - 1);
            len[0]--;
            return false;
        case 0x15:
            System.arraycopy(buffer, 1, buffer, 0, len[0] - 1);
            len[0]--;
            recvAcknowledged(false);
            return false;
        default:
            return false;
        }
    }

    public double getPacketsPerSecond() {
        return packetsPerSecond;
    }

    public Integer getHeartRate() {
        Boolean sensorDetached = isSensorAlarm();
        if (null == sensorDetached) {
            return null;
        } else {
            return sensorDetached ? null : currentPacket.getAvgHeartRateFourBeat();
        }
    }

    public Integer getSpO2() {
        Boolean sensorDetached = isSensorAlarm();
        if (null == sensorDetached) {
            return null;
        } else {
            return sensorDetached ? null : (int) currentPacket.getAvgSpO2FourBeat();
        }
    }

    public Boolean isOutOfTrack() {
        Packet packet = currentPacket;
        return null == packet ? null : packet.getCurrentStatus().isOutOfTrack();
    }

    public Boolean isSensorAlarm() {
        Packet packet = currentPacket;
        return null == packet ? null : packet.getCurrentStatus().isSensorAlarm();
    }

    public Packet getCurrentPacket() {
        return currentPacket;
    }

    public void receivePacket(Packet packet) {

    }

    private final boolean consumeFrame(byte[] buffer, int[] len) throws IOException {
        if (len[0] < Packet.FRAME_LENGTH) {
            return true;
        }

        if (expectNewPacket && !status.set(buffer[0]).isSync()) {
            frameError("RESYNC");
            System.arraycopy(buffer, Packet.FRAME_LENGTH, buffer, 0, len[0] - Packet.FRAME_LENGTH);
            len[0] -= Packet.FRAME_LENGTH;
            return false;
        }

        if (readyFlag) {
            if (Packet.validChecksum(buffer, 0)) {

                boolean packetComplete = currentPacket.setFrame(buffer, 0);

                if (packetComplete) {
                    expectNewPacket = true;

                    receivePacket(currentPacket);

                    long now = System.currentTimeMillis();

                    nextArrival.setTm(now);

                    Arrival next = nextArrival.getNext();
                    long nextTime = next.getTm();
                    if (0L != nextTime) {
                        double elapsed = (now - nextTime) / 1000.0;
                        packetsPerSecond = 1.0 * SAMPLE_SIZE / elapsed;
                    } else {
                        packetsPerSecond = 0;
                    }

                    nextArrival = nextArrival.getNext();
                } else {
                    expectNewPacket = false;
                }
            } else {
                frameError("invalid checksum");
            }
        }

        len[0] -= Packet.FRAME_LENGTH;
        System.arraycopy(buffer, Packet.FRAME_LENGTH, buffer, 0, len[0]);

        return false;

    }

    private final void consume(byte[] buffer, int[] len) throws IOException {
        while (len[0] > 0) {
            switch (buffer[0]) {
            case 0x02:
            case 0x06:
            case 0x15:
                // return true to indicate more data are needed!
                if (consumeControl(buffer, len)) {
                    return;
                }
                break;
            default:
                if (consumeFrame(buffer, len)) {
                    return;
                }
                break;
            }
        }
    }

    private boolean expectNewPacket = false;

    private byte[] buffer = new byte[Packet.LENGTH * 3];
    private int b;
    private int[] len = new int[] { 0 };

    protected boolean receive() throws IOException {
        int startPos = len[0];
        b = in.read(buffer, len[0], buffer.length - len[0]);

        // Read EOF, we're done
        if (b < 0) {
            readyFlag = false;
            return false;
        } else {
            len[0] += b;
            if (log.isTraceEnabled()) {
                log.trace("Read:" + HexUtil.dump(ByteBuffer.wrap(buffer, startPos, b)));
            }
        }

        consume(buffer, len);

        return true;
    }
}
