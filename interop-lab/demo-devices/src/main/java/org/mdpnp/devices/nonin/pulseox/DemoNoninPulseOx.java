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

import ice.ConnectionState;
import ice.SampleArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

import org.mdpnp.devices.io.util.HexUtil;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.FlowControl;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Time_t;

/**
 * @author Jeff Plourde
 *
 */
public class DemoNoninPulseOx extends AbstractDelegatingSerialDevice<NoninPulseOx> {

    private static final Logger log = LoggerFactory.getLogger(DemoNoninPulseOx.class);

    public enum Perfusion {
        Red, Green, Yellow
    }

    public enum Bool {
        True, False
    }

    protected void failAll() {
        synchronized (stateLock) {
            currentPhase = phases[0];
            currentState = State.Failure;
            stateLock.notifyAll();
        }
    }

    public DemoNoninPulseOx(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop, NoninPulseOx.class);

        failAll();

        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Nonin";
        writeDeviceIdentity();

    }

    protected static final byte[][] WRISTOX = new byte[][] { {0x18, 0x05, 0x09} };
    protected static final byte[][] ONYX = new byte[][] {{ (byte) 0x9E, 0x09 }, {(byte)0x93, 0x06} };

    protected static final boolean equals(byte[] a, int offa, int lena, byte[] b, int offb, int lenb) {
        if (lena != lenb) {
            return false;
        }
        for (int i = 0; i < lena; i++) {
            if (a[offa + i] != b[offb + i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState, String transitionNote) {
        super.stateChanged(newState, oldState, transitionNote);
        if (ConnectionState.Connected.equals(oldState) && !ConnectionState.Connected.equals(newState)) {
            failAll();
        }
    }

    private class MyNoninPulseOx extends NoninPulseOx {

        public MyNoninPulseOx(InputStream in, OutputStream out) {
            super(in, out);
        }

        @Override
        protected synchronized void receiveSerialNumber(String serial) {
            super.receiveSerialNumber(serial);

            log.info("Received serial number:" + serial);
            deviceIdentity.serial_number = serial;
            writeDeviceIdentity();

            received(Phase.GetSerial, true);
        }

        @Override
        protected synchronized void recvOperation(byte opCode, byte[] source, int off, int len) {
            boolean found = false;
            switch (opCode) {
            case (byte) 0xF3:
                for(byte[] WO : WRISTOX) {
                    if (!found && DemoNoninPulseOx.equals(source, off, len, WO, 0, WO.length)) {
                        System.err.println("THIS IS A WRIST OX?");
                        found = true;
                        iconOrBlank("WristOx2", "3150.png");
                    }
                }
                for(byte[] ON : ONYX) {
                    if(!found && DemoNoninPulseOx.equals(source, off, len, ON, 0, ON.length)) {
                        System.err.println("THIS IS An ONYX?");
                        found = true;
                        iconOrBlank("Onyx II", "9650.png");
                    }
                }
                
                if(!found) {
                    log.warn("Unrecognized response to 0x73:" + HexUtil.dump(ByteBuffer.wrap(source, off, len)));
                }

                received(Phase.GetDeviceType, true);

                break;
            default:
                super.recvOperation(opCode, source, off, len);
                break;
            }
        }

        @Override
        protected synchronized void recvAcknowledged(boolean success) {
            super.recvAcknowledged(success);
            received(null, success);
        }

        @Override
        protected synchronized void receiveDateTime(Date date) {
            super.receiveDateTime(date);
            received(Phase.GetTime, true);
        }

        private float[] plethBuffer = new float[Packet.FRAMES];
        
        private final Time_t updateTime = new Time_t(0,0);
        private long lastUpdate = 0L;

        private static final long MS_PER_PACKET = (long)( Packet.FRAMES * NoninPulseOx.MILLISECONDS_PER_SAMPLE );
        
        @Override
        public void receivePacket(Packet currentPacket) {
            // Changed because data will begin flowing even when negotiation is
            // incomplete
            // Instead reportConnected() when all phases have succeeded
            // reportConnected();

            for (int i = 0; i < Packet.FRAMES; i++) {
                plethBuffer[i] = currentPacket.getPleth(i);
            }
            
            
            // Complex way of finding the nearest millisecond on an even second
            // or 333ms or 666ms into the second
            long now = System.currentTimeMillis();
            long nearest_second = 1000L * (now / 1000L);
            long mod = (now-nearest_second) / MS_PER_PACKET;
            now = nearest_second + mod * MS_PER_PACKET;
            if((now%1000L)==999L) {
                now+=1L;
            }

            if(now <= lastUpdate) { 
                now = lastUpdate + MS_PER_PACKET;
                if((now%1000L)==999L) {
                    now+=1L;
                }
            }
            lastUpdate = now;
            
            updateTime.sec = (int) (now / 1000L);
            updateTime.nanosec = (int) ((now % 1000L) * 1000000L);
            
            pleth = sampleArraySample(pleth, plethBuffer, plethBuffer.length, 
                    rosetta.MDC_PULS_OXIM_PLETH.VALUE, 0, 
                    rosetta.MDC_DIM_DIMLESS.VALUE,
                    NoninPulseOx.FREQUENCY, updateTime);
            Status status = getCurrentPacket().getCurrentStatus();
            Perfusion perfusion = perfusion(status);
            writeTechnicalAlert("Perfusion", null != perfusion ? perfusion.toString() : null);
            writeTechnicalAlert("Artifact", status.isArtifact()?"Artifact":null);
            writeTechnicalAlert("Out Of Track", status.isOutOfTrack()?"Out Of Track":null);
            writeTechnicalAlert("Sensor Alarm", status.isSensorAlarm()?"Sensor Alarm":null);
            writeTechnicalAlert("Battery", getCurrentPacket().isLowBattery()?"Low Battery":null);
            writeTechnicalAlert("Smart Point", getCurrentPacket().isSmartPoint()?"Smart Point":null);
            writeTechnicalAlert("Firmware", Integer.toString(getCurrentPacket().getFirmwareRevision()));
            
            
            if (currentPacket.getCurrentStatus().isArtifact() || currentPacket.getCurrentStatus().isSensorAlarm()
                    || currentPacket.getCurrentStatus().isOutOfTrack()) {
                pulse = numericSample(pulse, (Integer) null, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, 
                        rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, null);
                SpO2 = numericSample(SpO2, (Integer) null, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, 
                        rosetta.MDC_DIM_PERCENT.VALUE, null);
                avgHeartRateFourBeat = numericSample(avgHeartRateFourBeat, (Integer) null, "NONIN_AVG_HR_4BEAT", 
                        rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, null);
                avgSpO2FourBeat = numericSample(avgSpO2FourBeat, (Integer) null, "NONIN_AVG_SPO2_4BEAT", 
                        rosetta.MDC_DIM_PERCENT.VALUE, null);
                avgSpO2FourBeatFast = numericSample(avgSpO2FourBeatFast, (Integer) null, "NONIN_SPO2_4BEAT_FAST", 
                        rosetta.MDC_DIM_PERCENT.VALUE, null);
                SpO2BeatToBeat = numericSample(SpO2BeatToBeat, (Integer) null, "NONIN_SPO2_BTB", 
                        rosetta.MDC_DIM_PERCENT.VALUE, null);
                avgHeartRateEightBeat = numericSample(avgHeartRateEightBeat, (Integer)null, "NONIN_AVG_HR_8BEAT",
                        rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, null);
                avgSpO2EightBeat = numericSample(avgSpO2EightBeat, (Integer)null, "NONIN_SPO2_8BEAT", 
                        rosetta.MDC_DIM_PERCENT.VALUE, null);
                avgSpO2EightBeatForDisplay = numericSample(avgSpO2EightBeatForDisplay, (Integer) null, "NONIN_SPO2_8BEAT_FOR_DISPLAY", 
                        rosetta.MDC_DIM_PERCENT.VALUE, null);
                avgHeartRateFourBeatForDisplay = numericSample(avgHeartRateFourBeatForDisplay, (Integer) null, "NONIN_HR_4BEAT_FOR_DISPLAY", 
                        rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, null);
                avgHeartRateEightBeatForDisplay = numericSample(avgHeartRateEightBeatForDisplay, (Integer) null, "NONIN_HR_8BEAT_FOR_DISPLAY",
                        rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, null);
            } else {
                pulse = numericSample(pulse, maxOut(getHeartRate(), MAX_HR), rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, 
                        rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, null);
                SpO2 = numericSample(SpO2, maxOut(getSpO2(), MAX_SPO2), rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, 
                        rosetta.MDC_DIM_PERCENT.VALUE, null);
                avgHeartRateFourBeat = numericSample(avgHeartRateFourBeat, maxOut(getAvgHeartRateFourBeat(), MAX_HR), "NONIN_AVG_HR_4BEAT",
                        rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, null);
                avgSpO2FourBeat = numericSample(avgSpO2FourBeat, maxOut(getAvgSpO2FourBeat(), MAX_SPO2), "NONIN_AVG_SPO2_4BEAT", 
                        rosetta.MDC_DIM_PERCENT.VALUE, null);
                avgSpO2FourBeatFast = numericSample(avgSpO2FourBeatFast, maxOut(getAvgSpO2FourBeatFast(), MAX_SPO2), "NONIN_SPO2_4BEAT_FAST",
                        rosetta.MDC_DIM_PERCENT.VALUE, null);
                SpO2BeatToBeat = numericSample(SpO2BeatToBeat, maxOut(getSpO2BeatToBeat(), MAX_SPO2), "NONIN_SPO2_BTB", 
                        rosetta.MDC_DIM_PERCENT.VALUE, null);
                avgHeartRateEightBeat = numericSample(avgHeartRateEightBeat, maxOut(getAvgHeartRateEightBeat(), MAX_HR), "NONIN_AVG_HR_8BEAT",
                        rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, null);
                avgSpO2EightBeat = numericSample(avgSpO2EightBeat, maxOut(getAvgSpO2EightBeat(), MAX_SPO2), "NONIN_SPO2_8BEAT",
                        rosetta.MDC_DIM_PERCENT.VALUE, null);
                avgSpO2EightBeatForDisplay = numericSample(avgSpO2EightBeatForDisplay, maxOut(getAvgSpO2EightBeatForDisplay(), MAX_SPO2), "NONIN_SPO2_8BEAT_FOR_DISPLAY", 
                        rosetta.MDC_DIM_PERCENT.VALUE, null);
                avgHeartRateFourBeatForDisplay = numericSample(avgHeartRateFourBeatForDisplay, maxOut(getAvgHeartRateFourBeatForDisplay(), MAX_HR), "NONIN_HR_4BEAT_FOR_DISPLAY", 
                        rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, null);
                avgHeartRateEightBeatForDisplay = numericSample(avgHeartRateEightBeatForDisplay, maxOut(getAvgHeartRateEightBeatForDisplay(), MAX_HR), "NONIN_HR_8BEAT_FOR_DISPLAY", 
                        rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, null);
            }

        }
    }
    private static final int MAX_HR = 511, MAX_SPO2 = 101;
    
    private static final Integer maxOut(Integer i, int max) {
        return i != null ? (i < max ? i : null) : null;
    }

    public static final Perfusion perfusion(Status status) {
        if (status.isGreenPerfusion()) {
            return Perfusion.Green;
        } else if (status.isYellowPerfusion()) {
            return Perfusion.Yellow;
        } else if (status.isRedPerfusion()) {
            return Perfusion.Red;
        } else {
            return null;
        }
    }

    @Override
    protected NoninPulseOx buildDelegate(int idx, InputStream in, OutputStream out) {
        return new MyNoninPulseOx(in, out);
    }

    protected InstanceHolder<ice.Numeric> pulse, SpO2;
    protected InstanceHolder<SampleArray> pleth;
    protected InstanceHolder<ice.Numeric> avgHeartRateFourBeat, 
        avgSpO2FourBeat, avgSpO2FourBeatFast, SpO2BeatToBeat, avgHeartRateEightBeat, 
        avgSpO2EightBeat, avgSpO2EightBeatForDisplay, avgHeartRateFourBeatForDisplay,
        avgHeartRateEightBeatForDisplay;
    
    enum Phase {
        GetSerial, GetDeviceType, SetFormat, GetTime,
    }

    enum State {
        Issued, Success, Failure
    }

    // Negotiation must proceed through these phases
    protected static final Phase[] phases = new Phase[] { Phase.SetFormat, Phase.GetSerial, Phase.GetDeviceType };

    // The current phase we are negotiating
    protected Phase currentPhase = phases[0];
    // The state of the current phase of negotiation
    protected State currentState = State.Failure;
    protected long issuedTime = 0L;
    // Synchronized accesses to currentPhase/currentState
    protected final Object stateLock = new Object();

    protected void received(Phase phase, boolean success) {
        synchronized (stateLock) {
            if (!State.Issued.equals(currentState)) {
                log.warn("Received a response but not in issued state");
            }
            if (success) {
                currentState = State.Success;
            } else {
                currentState = State.Failure;
            }
            log.debug("Received:" + phase + " " + currentState);
            stateLock.notifyAll();
        }
    }

    protected final static Phase nextPhase(Phase currentPhase) {
        for (int i = 0; i < phases.length; i++) {
            if (currentPhase.equals(phases[i])) {
                if ((i + 1) < phases.length) {
                    return phases[i + 1];
                } else {
                    return null;
                }
            }
        }
        log.error("Shouldn't ever be in phase:" + currentPhase);
        return null;
    }

    protected void send() throws IOException {
        boolean reportConnected = false;
        Phase issueConnectForPhase = null;
        synchronized (stateLock) {
            Phase lastPhase = currentPhase;
            switch (currentState) {
            case Failure:
                issueConnectForPhase = currentPhase;
                currentState = State.Issued;
                issuedTime = System.currentTimeMillis();
                setConnectionInfo("Issuing " + currentPhase);
                break;
            case Issued:
                if (System.currentTimeMillis() >= (issuedTime + 5000L)) {
                    log.warn("Reissuing:" + currentPhase);
                    setConnectionInfo("Re-Issuing " + currentPhase);
                    issueConnectForPhase = currentPhase;
                }
                break;
            case Success:
                issueConnectForPhase = currentPhase = nextPhase(currentPhase);
                if (null == issueConnectForPhase) {
                    reportConnected = true;
                    setConnectionInfo("");
                } else {
                    currentState = State.Issued;
                    issuedTime = System.currentTimeMillis();
                    setConnectionInfo(lastPhase + " Success");
                }
            }
            stateLock.notifyAll();
        }
        if (null != issueConnectForPhase) {
            makeRequest(getDelegate(), issueConnectForPhase);
        }
        if (reportConnected) {
            log.info("Connection successfully negotiated");
            reportConnected("Connection successfully negotiated");
            getDelegate().readyFlag = true;
        }
    }

    protected static final void makeRequest(NoninPulseOx delegate, Phase phase) throws IOException {
        log.debug("makeRequest for " + phase);

        switch (phase) {
        case SetFormat:
            delegate.sendSetFormat(7, false, true);
            break;
        case GetDeviceType:
            delegate.sendOperation((byte) 0x73, new byte[0]);
            break;
        case GetTime:
            delegate.sendGetDateTime();
            break;
        case GetSerial:
            delegate.sendGetSerial(2);
            break;
        default:
        }

    }
    
    @Override
    public void doInitCommands(int idx) throws IOException {
        super.doInitCommands(idx);
        send();
    }

    @Override
    protected boolean delegateReceive(int idx, NoninPulseOx delegate) throws IOException {
        return delegate.receive();
    }

    @Override
    protected long getConnectInterval(int idx) {
        return 5000L;
    }

    @Override
    protected long getNegotiateInterval(int idx) {
        return 500L;
    }

    @Override
    // 3 packets per second mean the theoretical max quiet time is 333ms
    protected long getMaximumQuietTime(int idx) {
        return 3000L;
    }

    @Override
    protected String iconResourceName() {
        return "9650.png";
    }

    @Override
    public SerialProvider getSerialProvider(int idx) {
        SerialProvider serialProvider = super.getSerialProvider(idx);
        serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One, FlowControl.None);
        return serialProvider;
    }

    protected static boolean response = false;

    @Override
    protected boolean sampleArraySpecifySourceTimestamp() {
        return true;
    }
    
    // This main program resets the device to data type 13 (which is the
    // default)
    public static void main(String[] args) throws IOException {
        SerialProvider serialProvider = SerialProviderFactory.getDefaultProvider();
        serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One, FlowControl.None);
        SerialSocket sock = serialProvider.connect(args[0], 5000L);

        final NoninPulseOx pulseox = new NoninPulseOx(sock.getInputStream(), sock.getOutputStream()) {
            protected synchronized void recvAcknowledged(boolean success) {
                System.out.println(success ? "ACK" : "NAK");
                synchronized (DemoNoninPulseOx.class) {
                    response = true;
                    DemoNoninPulseOx.class.notifyAll();
                }
                super.recvAcknowledged(success);
            };
        };
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    for (;;) {
                        pulseox.receive();
                    }
                } catch (IOException e) {
                    if (!response) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();

        System.out.println("Requesting dataformat 13...");
        pulseox.sendSetFormat(13, true, true);
        long waitUntil = System.currentTimeMillis() + 5000L;
        synchronized (DemoNoninPulseOx.class) {
            while (!response) {
                try {
                    DemoNoninPulseOx.class.wait(500L);
                    if (!response && System.currentTimeMillis() >= waitUntil) {
                        // try again
                        System.out.println("Re-issuing dataformat 13");
                        pulseox.sendSetFormat(13, true, true);
                        waitUntil = System.currentTimeMillis() + 5000L;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Response received, exit...");
        }
        sock.close();
        System.exit(0);
    }
}
