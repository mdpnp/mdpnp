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
package org.mdpnp.devices.zephyr.biopatch;

import ice.ConnectionState;
import ice.NumericSQI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.FlowControl;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

/**
 * @author Jeff Plourde
 *
 */
public class DemoBioPatch extends AbstractDelegatingSerialDevice<BioPatch> {

    private static final Logger log = LoggerFactory.getLogger(DemoBioPatch.class);

    public DemoBioPatch(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop, BioPatch.class);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Zephyr";
        deviceIdentity.model = "BioPatch";
        writeDeviceIdentity();

    }


    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState, String transitionNote) {
        super.stateChanged(newState, oldState, transitionNote);
        if (ConnectionState.Connected.equals(oldState) && !ConnectionState.Connected.equals(newState)) {

        }
        
        if (ice.ConnectionState.Connected.equals(newState) && !ice.ConnectionState.Connected.equals(oldState)) {
            startEmitSignOfLife();
        }
        if (!ice.ConnectionState.Connected.equals(newState) && ice.ConnectionState.Connected.equals(oldState)) {
            stopEmitSignOfLife();
        }
    }

    private class BioPatchExt extends BioPatch {

        public BioPatchExt(DeviceClock referenceClock, InputStream in, OutputStream out) {
            super(referenceClock, in, out);
        }
        
        @Override
        protected void receiveGeneralDataPacket(DeviceClock.Reading timeofday, int sequenceNumber, Integer heartrate, Float resprate, Float skintemp) {
            reportConnected("Received General Data Packet");
            log.warn("sequenceNumber="+sequenceNumber+" timeofday="+timeofday+" heartrate="+heartrate+" respirationRate="+resprate+" skinTemp="+skintemp);
            heartRate = numericSample(heartRate, heartrate, new NumericSQI(), rosetta.MDC_ECG_HEART_RATE.VALUE, "", rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, timeofday);
            respirationRate = numericSample(respirationRate, resprate, new NumericSQI(), rosetta.MDC_TTHOR_RESP_RATE.VALUE, "", rosetta.MDC_DIM_DIMLESS.VALUE, timeofday);
            skinTemperature = numericSample(skinTemperature, skintemp, new NumericSQI(), rosetta.MDC_TEMP_SKIN.VALUE, "", rosetta.MDC_DIM_DEGC.VALUE, timeofday);
        }
        
        @Override
        protected void receiveECGDataPacket(DeviceClock.Reading timeofday, Number[] values) {
            ecgTrace = sampleArraySample(ecgTrace, values,  new NumericSQI(), ice.MDC_ECG_LEAD_I.VALUE, "", rosetta.MDC_DIM_DIMLESS.VALUE, 250, timeofday);
        }
        
        @Override
        protected void receiveGetSerialNumber(String s) {
            deviceIdentity.serial_number = s;
            writeDeviceIdentity();
        }
    }

    protected InstanceHolder<ice.Numeric> heartRate, respirationRate, skinTemperature;
    protected InstanceHolder<ice.SampleArray> ecgTrace;
    
    @Override
    protected BioPatch buildDelegate(int idx, InputStream in, OutputStream out) {
        return new BioPatchExt(getClockProvider(), in, out);
    }

    
    @Override
    public void doInitCommands(int idx) throws IOException {
        super.doInitCommands(idx);
        getDelegate().sendGetSerialNumber();
        
        getDelegate().sendSetGeneralDataPacketTransmitState(true);
        getDelegate().sendSetECGWaveformPacketTransmitState(true);
//        getDelegate().sendSetBreathingPacketTransmitState(true);
    }

    @Override
    protected boolean delegateReceive(int idx, BioPatch delegate) throws IOException {
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
        return "biopatch.png";
    }

    @Override
    public SerialProvider getSerialProvider(int idx) {
        SerialProvider serialProvider = super.getSerialProvider(idx);
        serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One, FlowControl.None);
        return serialProvider;
    }

    protected static boolean response = false;

    private class EmitSignOfLife implements Runnable {
        public void run() {
            if (ice.ConnectionState.Connected.equals(getState())) {
                try {
                    getDelegate().sendLifesign();
                } catch (IOException e) {
                    log.warn("Error sending LifeSign indication", e);
                }
            }
        }
    }
    private ScheduledFuture<?> emitSignOfLife;
    
    private synchronized void stopEmitSignOfLife() {
        if (null != emitSignOfLife) {
            emitSignOfLife.cancel(false);
            emitSignOfLife = null;
            log.trace("Canceled sign of life");
        } else {
            log.trace("sign of life request already canceled");
        }
    }

    private synchronized void startEmitSignOfLife() {
        if (null == emitSignOfLife) {
            emitSignOfLife = executor.scheduleWithFixedDelay(new EmitSignOfLife(), 5000L, 5000L, TimeUnit.MILLISECONDS);
            log.trace("Scheduled sign of life");
        } else {
            log.trace("sign of life request already scheduled");
        }
    }

}
