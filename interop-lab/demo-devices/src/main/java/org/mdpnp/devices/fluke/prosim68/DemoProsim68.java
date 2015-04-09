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
package org.mdpnp.devices.fluke.prosim68;

import ice.ConnectionState;
import ice.GlobalSimulationObjective;
import ice.GlobalSimulationObjectiveDataReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.fluke.prosim8.FlukeProSim8;
import org.mdpnp.devices.fluke.prosim8.FlukeProSim8.Wave;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.FlowControl;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.devices.simulation.GlobalSimulationObjectiveListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.GlobalSimulationObjectiveInstanceModel;
import org.mdpnp.rtiapi.data.GlobalSimulationObjectiveInstanceModelImpl;
import org.mdpnp.rtiapi.data.GlobalSimulationObjectiveInstanceModelListener;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.QosPolicy;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;

/**
 * @author Jeff Plourde
 *
 */
public class DemoProsim68 extends AbstractDelegatingSerialDevice<FlukeProSim8> implements GlobalSimulationObjectiveInstanceModelListener {

    private final static Logger log = LoggerFactory.getLogger(DemoProsim68.class);

    private class MyFlukeProSim8 extends FlukeProSim8 {

        public MyFlukeProSim8(InputStream in, OutputStream out) {
            super(in, out);
        }

        @Override
        public void receiveString(String line) {
            log.debug("Received:" + line);
        }
    }

    protected final GlobalSimulationObjectiveInstanceModel monitor;

    public DemoProsim68(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop, FlukeProSim8.class);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Fluke";
        deviceIdentity.model = "Prosim 6 / 8";
        writeDeviceIdentity();

        monitor = new GlobalSimulationObjectiveInstanceModelImpl(ice.GlobalSimulationObjectiveTopic.VALUE);
        monitor.addListener(this);
        monitor.startReader(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
    }

    private ScheduledFuture<?> linkIsActive;
    @Override
    public SerialProvider getSerialProvider(int idx) {
        SerialProvider serialProvider = super.getSerialProvider(idx);
        serialProvider.setDefaultSerialSettings(115200, DataBits.Eight, Parity.None, StopBits.One, FlowControl.Hardware);
        return serialProvider;
    }

    @Override
    protected FlukeProSim8 buildDelegate(int idx, InputStream in, OutputStream out) {
        return new MyFlukeProSim8(in, out);
    }

    @Override
    protected boolean delegateReceive(int idx, FlukeProSim8 delegate) throws IOException {
        delegate.receiveCommand();

        return true;
    }

    protected void pollTime() {
        try {
            if (ice.ConnectionState.Connected.equals(getState())) {
                Date date = getDelegate().getRealTimeClock();
            }
        } catch (IOException e) {
            log.error("Error polling time; disconnected?", e);
        } catch (ParseException e) {
            log.error("Cannot parse GETRTC response", e);
        }
    }

    @Override
    protected void doInitCommands(int idx) throws IOException {
        log.debug("Ident");
        String identifier = getDelegate().ident();
        if (null == identifier) {
            return;
        }
        String s[] = identifier.split(",");
        deviceIdentity.model = s[0];
        writeDeviceIdentity();

        // log.debug("localModel");
        // getDelegate().localMode();
        log.debug("validationOn");
        if (null == getDelegate().validationOn()) {
            return;
        }

        // log.debug("Press SpO2 key");
        // getDelegate().sendKey(KeyCode.SpO2, 100);
        log.debug("Remote mode");
        if (null == getDelegate().remoteMode()) {
            return;
        }

        // try {
        // log.debug("GETRTC="+getDelegate().getRealTimeClock());
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }
        //
        // log.debug("SETRTC="+getDelegate().setRealTimeClock(new Date()));
        //
        //
        // try {
        // log.debug("GETRTC="+getDelegate().getRealTimeClock());
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }
        reportConnected("message received");
        super.doInitCommands(idx);
    }

    @Override
    protected long getMaximumQuietTime(int idx) {
        return 5000L;
    }

    @Override
    protected long getConnectInterval(int idx) {
        return 3000L;
    }

    @Override
    protected long getNegotiateInterval(int idx) {
        return 1000L;
    }

    @Override
    public void disconnect() {
        boolean shouldSend = false;
        synchronized (stateMachine) {
            shouldSend = ice.ConnectionState.Connected.equals(getState());
        }
        if (shouldSend) {
            try {
                getDelegate().validationOff();
                log.debug("Validation Off");
                getDelegate().localMode();
                log.debug("Local Mode");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.disconnect();
    }

    @Override
    public boolean connect(String address) {
        boolean b = super.connect(address);
        if(b) {
            linkIsActive = executor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    pollTime();
                }
            }, 4000L, 4000L, TimeUnit.MILLISECONDS);
        }
        return b;
    }

    @Override
    public void shutdown() {
        monitor.stopReader();
        super.shutdown();
    }

    @Override
    protected String iconResourceName() {
        return "prosim8.png";
    }
    
    private Number pulseRate, saturation, respRate, invasiveSystolic, invasiveDiastolic, noninvasiveSystolic, noninvasiveDiastolic;
    
    private class EmitData implements Runnable {

        @Override
        public void run() {
            log.info("Emitting data");
            try {
                Number n = pulseRate;
                if(null != n) {
                    int x = n.intValue();
                    getDelegate().normalSinusRhythmAdult(x);
                    log.info("pulseRate:"+x);
                }
                n = saturation;
                if(null != n) {
                    getDelegate().saturation(n.intValue());
                }
                n = respRate;
                if(null != n) {
                    getDelegate().respirationRate(n.intValue());
                }
                n = invasiveSystolic;
                Number o = invasiveDiastolic;
                if(null != n && null != o) {
                    getDelegate().invasiveBloodPressureDynamic(1, n.intValue(), o.intValue());
                    getDelegate().invasiveBloodPressureWave(1, Wave.Arterial);
                }
                n = noninvasiveSystolic;
                o = noninvasiveDiastolic;
                if(null != n && null != o) {
                    getDelegate().nonInvasiveBloodPressureDynamic(n.intValue(), o.intValue());
                }
            } catch (IOException e) {
                log.error("Unable to send command", e);
            }
            
        }
        
    }
    private ScheduledFuture<?> emitData;
    private void startEmitData() {
        if (null == emitData) {
            emitData = executor.scheduleWithFixedDelay(new EmitData(), 1000L, 1000L, TimeUnit.MILLISECONDS);
            log.trace("Scheduled emit data task");
        } else {
            log.trace("emit data already scheduled");
        }
    }
    private void stopEmitData() {
        if (null != emitData) {
            emitData.cancel(false);
            emitData = null;
            log.trace("Canceled emit data task");
        } else {
            log.trace("emit data already canceled");
        }
    }
    
    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState, String transitionNote) {
        if (ice.ConnectionState.Connected.equals(newState) && !ice.ConnectionState.Connected.equals(oldState)) {
            startEmitData();
        }
        if (!ice.ConnectionState.Connected.equals(newState) && ice.ConnectionState.Connected.equals(oldState)) {
            stopEmitData();
        }
        super.stateChanged(newState, oldState, transitionNote);
    }

    @Override
    public void instanceAlive(ReaderInstanceModel<GlobalSimulationObjective, GlobalSimulationObjectiveDataReader> model,
            GlobalSimulationObjectiveDataReader reader, GlobalSimulationObjective data, SampleInfo sampleInfo) {
    }

    @Override
    public void instanceNotAlive(ReaderInstanceModel<GlobalSimulationObjective, GlobalSimulationObjectiveDataReader> model,
            GlobalSimulationObjectiveDataReader reader, GlobalSimulationObjective keyHolder, SampleInfo sampleInfo) {
    }

    @Override
    public void instanceSample(ReaderInstanceModel<GlobalSimulationObjective, GlobalSimulationObjectiveDataReader> model,
            GlobalSimulationObjectiveDataReader reader, GlobalSimulationObjective data, SampleInfo sampleInfo) {
        if(sampleInfo.valid_data) {
            
            if (rosetta.MDC_PULS_RATE.VALUE.equals(data.metric_id)) {
                pulseRate = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            } else if (rosetta.MDC_PULS_OXIM_SAT_O2.VALUE.equals(data.metric_id)) {
                saturation = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            } else if (rosetta.MDC_RESP_RATE.VALUE.equals(data.metric_id)) {
                respRate = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            } else if (rosetta.MDC_PRESS_BLD_SYS.VALUE.equals(data.metric_id)) {
                invasiveSystolic = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            } else if (rosetta.MDC_PRESS_BLD_DIA.VALUE.equals(data.metric_id)) {
                invasiveDiastolic = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            } else if (rosetta.MDC_PRESS_CUFF_DIA.VALUE.equals(data.metric_id)) {
                noninvasiveDiastolic = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            } else if (rosetta.MDC_PRESS_CUFF_SYS.VALUE.equals(data.metric_id)) {
                noninvasiveSystolic = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            }
        }
    }
}
