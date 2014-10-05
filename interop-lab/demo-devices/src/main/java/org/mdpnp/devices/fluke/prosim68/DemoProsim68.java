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

import ice.GlobalSimulationObjective;

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
import org.mdpnp.devices.simulation.GlobalSimulationObjectiveMonitor;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class DemoProsim68 extends AbstractDelegatingSerialDevice<FlukeProSim8> implements GlobalSimulationObjectiveListener {

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

    protected final GlobalSimulationObjectiveMonitor monitor;

    public DemoProsim68(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop, FlukeProSim8.class);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Fluke";
        deviceIdentity.model = "Prosim 6 / 8";
        writeDeviceIdentity();

        monitor = new GlobalSimulationObjectiveMonitor(this);
        monitor.register(domainParticipant, eventLoop);

        linkIsActive = executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                pollTime();
            }
        }, 4000L, 4000L, TimeUnit.MILLISECONDS);
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
    public void shutdown() {
        monitor.unregister();
        super.shutdown();
    }

    // TODO Bit of a hack.. no full lifecycle implemented for these instances
    private Integer invasiveSystolic, invasiveDiastolic;
    private Integer noninvasiveSystolic, noninvasiveDiastolic;

    @Override
    protected String iconResourceName() {
        return "prosim8.png";
    }

    private final void setInvasive() throws IOException {
        if (null != invasiveSystolic && null != invasiveDiastolic) {
            getDelegate().invasiveBloodPressureDynamic(1, invasiveSystolic, invasiveDiastolic);
            getDelegate().invasiveBloodPressureWave(1, Wave.Arterial);
        }
    }

    private final void setNoninvasive() throws IOException {
        if (null != noninvasiveSystolic && null != noninvasiveDiastolic) {
            getDelegate().nonInvasiveBloodPressureDynamic(noninvasiveSystolic, noninvasiveDiastolic);
        }
    }

    @Override
    public void simulatedNumeric(GlobalSimulationObjective gso) {
        try {
            if (rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(gso.metric_id)) {
                getDelegate().normalSinusRhythmAdult((int) gso.value);
            } else if (rosetta.MDC_PULS_OXIM_SAT_O2.VALUE.equals(gso.metric_id)) {
                getDelegate().saturation((int) gso.value);
            } else if (rosetta.MDC_CO2_RESP_RATE.VALUE.equals(gso.metric_id)) {
                // TODO this isn't really apt since fluke cannot emit CO2 measures
                getDelegate().respirationRate((int) gso.value);
            } else if(rosetta.MDC_TTHOR_RESP_RATE.VALUE.equals(gso.metric_id)) {
                getDelegate().respirationRate((int) gso.value);
            } else if (rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE.equals(gso.metric_id)) {
                invasiveDiastolic = (int) gso.value;
                setInvasive();
            } else if (rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE.equals(gso.metric_id)) {
                invasiveSystolic = (int) gso.value;
                setInvasive();
            } else if (rosetta.MDC_PRESS_BLD_NONINV_DIA.VALUE.equals(gso.metric_id)) {
                noninvasiveDiastolic = (int) gso.value;
                setNoninvasive();
            } else if (rosetta.MDC_PRESS_BLD_NONINV_SYS.VALUE.equals(gso.metric_id)) {
                noninvasiveSystolic = (int) gso.value;
                setNoninvasive();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
