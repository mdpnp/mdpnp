/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.nonin.pulseox;

import ice.ConnectionState;
import ice.SampleArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.io.util.HexUtil;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.FlowControl;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class DemoNoninPulseOx extends AbstractDelegatingSerialDevice<NoninPulseOx> {

    private static final Logger log = LoggerFactory.getLogger(DemoNoninPulseOx.class);

    public enum Perfusion {
        Red,
        Green,
        Yellow
    }

    public enum Bool {
        True,
        False
    }

    protected void failAll() {
        synchronized(stateLock) {
            currentPhase = phases[0];
            currentState = State.Failure;
//            for(int i = 0; i < states.length; i++) {
//                states[i] = State.Failure;
//            }
            stateLock.notifyAll();
        }
    }

    public DemoNoninPulseOx(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);

        failAll();

        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Nonin";
        writeDeviceIdentity();

    }

    protected static final byte[] WRISTOX = new byte[] { 0x18, 0x05, 0x09 };
    protected static final byte[] ONYX = new byte[] { (byte) 0x9E, 0x09 };

    protected static final boolean equals(byte[] a, int offa, int lena, byte[] b, int offb, int lenb) {
        if(lena != lenb) {
            return false;
        }
        for(int i = 0; i < lena; i++) {
            if(a[offa+i]!=b[offb+i]) {
                return false;
            }
        }
        return true;
    }

    protected void iconOrBlank(String model, String icon) {
        deviceIdentity.model = model;
        try {
            iconFromResource(deviceIdentity, icon);
        } catch (IOException e) {
            log.error("Error loading icon resource", e);
            deviceIdentity.icon.raster.userData.clear();
            deviceIdentity.icon.height = 0;
            deviceIdentity.icon.width = 0;
        }
        writeDeviceIdentity();
    }



    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState) {
        super.stateChanged(newState, oldState);
        if(ConnectionState.Connected.equals(oldState) && !ConnectionState.Connected.equals(newState)) {
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

            log.info("Received serial number:"+serial);
            deviceIdentity.serial_number = serial;
            writeDeviceIdentity();

            received(Phase.GetSerial, true);
        }

        @Override
        protected synchronized void recvOperation(byte opCode, byte[] source, int off, int len) {
            switch(opCode) {
            case (byte) 0xF3:
                if(DemoNoninPulseOx.equals(source, off, len, WRISTOX, 0, WRISTOX.length)) {
                    iconOrBlank("WristOx2", "3150.png");
                } else if(DemoNoninPulseOx.equals(source, off, len, ONYX, 0, ONYX.length)) {
                    iconOrBlank("Onyx II", "9650.png");
                } else {
                    log.warn("Unrecognized response to 0x73:"+HexUtil.dump(ByteBuffer.wrap(source, off, len)));
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

        private int[] plethBuffer = new int[Packet.FRAMES];

        @Override
        public void receivePacket(Packet currentPacket) {
//            reportConnected();

            for(int i = 0; i < Packet.FRAMES; i++) {
                plethBuffer[i] = currentPacket.getPleth(i);
            }
            pleth = sampleArraySample(pleth, plethBuffer, plethBuffer.length, (int)NoninPulseOx.MILLISECONDS_PER_SAMPLE, rosetta.MDC_PULS_OXIM_PLETH.VALUE);

            if(currentPacket.getCurrentStatus().isArtifact()||currentPacket.getCurrentStatus().isSensorAlarm()||currentPacket.getCurrentStatus().isOutOfTrack()) {
                pulse = numericSample(pulse, (Integer)null, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, null);
                SpO2 = numericSample(SpO2, (Integer)null, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, null);
            } else {
                Integer heartRate = getHeartRate();
                Integer spo2 = getSpO2();
                pulse = numericSample(pulse, heartRate != null ? (heartRate < 511 ? heartRate : null) : null, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, null);
                SpO2 = numericSample(SpO2, spo2 != null ? (spo2 <= 100 ? spo2 : null) : null, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, null);
            }


        }
    }
    public static final Perfusion perfusion(Status status) {
        if(status.isGreenPerfusion()) {
            return Perfusion.Green;
        } else if(status.isYellowPerfusion()) {
            return Perfusion.Yellow;
        } else if(status.isRedPerfusion()) {
            return Perfusion.Red;
        } else {
            return null;
        }
    }
    @Override
    protected NoninPulseOx buildDelegate(InputStream in, OutputStream out) {
        return new MyNoninPulseOx(in, out);
    }
    protected InstanceHolder<ice.Numeric> pulse;
    protected InstanceHolder<ice.Numeric> SpO2;
    protected InstanceHolder<SampleArray> pleth;

    enum Phase {
        GetSerial,
        GetDeviceType,
        SetFormat,
        GetTime,
    }

    enum State {
        Issued,
        Success,
        Failure
    }



    protected static final Phase[] phases = new Phase[] { Phase.SetFormat, Phase.GetSerial, Phase.GetDeviceType };
    protected Phase currentPhase = phases[0];
//    protected final State[] states = new State[phases.length];
    protected State currentState = State.Failure;

    protected final Object stateLock = new Object();


    private int ordinal(Phase phase) {
        for(int i = 0; i < phases.length; i++) {
            if(phase.equals(phases[i])) {
                return i;
            }
        }
        return -1;
    }

    protected void received(Phase phase, boolean success) {
        synchronized(stateLock) {
            if(!State.Issued.equals(currentState)) {
                log.warn("Received a response but not in issued state");
            }
            if(success) {
                currentState = State.Success;
            } else {
                currentState = State.Failure;
            }
            log.debug("Received:" + phase + " " + currentState);
            stateLock.notifyAll();
        }
    }

    protected final static Phase nextPhase(Phase currentPhase) {
        for(int i = 0; i < phases.length; i++) {
            if(currentPhase.equals(phases[i])) {
                if( (i+1)<phases.length) {
                    return phases[i+1];
                } else {
                    return null;
                }
            }
        }
        log.error("Shouldn't ever be in phase:"+currentPhase);
        return null;
    }

    protected void send() throws IOException {
        boolean reportConnected = false;
        Phase issueConnectForPhase = null;
//        List<Phase> issueConnectForPhase = new ArrayList<Phase>();
        synchronized(stateLock) {
            switch(currentState) {
            case Failure:
                issueConnectForPhase = currentPhase;
                currentState = State.Issued;
                break;
            case Issued:
                break;
            case Success:
                issueConnectForPhase = currentPhase = nextPhase(currentPhase);
                if(null == issueConnectForPhase) {
                    reportConnected = true;
                } else {
                    currentState = State.Issued;
                }
            }
//            for(int i = 0; i < states.length; i++) {
//                switch(states[i]) {
//                case Issued:
//                    // Do nothing .. eventually maybe retry?
//                    reportConnected = false;
//                    break;
//                case Success:
//                    // This is already done
//                    break;
//                case Failure:
//                    if(issueConnectForPhase.isEmpty()) {
//                        issueConnectForPhase.add(phases[i]);
//                        states[i] = State.Issued;
//                    }
//                    reportConnected = false;
//                    break;
//                }
//            }
            stateLock.notifyAll();
        }
//        for(Phase p : issueConnectForPhase) {
        if(null != issueConnectForPhase) {
            makeRequest(getDelegate(), issueConnectForPhase);
        }
//        }
        if(reportConnected) {
            log.info("Connection negotiated");
            reportConnected();
            getDelegate().readyFlag = true;
        }
    }

    protected static final void makeRequest(NoninPulseOx delegate, Phase phase) throws IOException {
        log.debug("makeRequest for " + phase);

        switch(phase) {
        case SetFormat:
            delegate.sendSetFormat(0x07, false, true);
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

    public void doInitCommands() throws IOException {
        super.doInitCommands();
//        try {
//            Thread.sleep(1000L);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        send();
    }


    @Override
    protected boolean delegateReceive(NoninPulseOx delegate) throws IOException {
        return delegate.receive();
    }

    @Override
    protected long getConnectInterval() {
        return 5000L;
    }

    @Override
    protected long getNegotiateInterval() {
        return 500L;
    }


    @Override
    // 3 packets per second mean the theoretical max quiet time is 333ms
    protected long getMaximumQuietTime() {
        return 3000L;
    }

    @Override
    protected String iconResourceName() {
        return "3150.png";
    }

    @Override
    public SerialProvider getSerialProvider() {
        SerialProvider serialProvider =  super.getSerialProvider();
        serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One, FlowControl.None);
        return serialProvider;
    }
}
