package org.mdpnp.devices.fluke.prosim68;

import ice.GlobalSimulationObjective;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.fluke.prosim8.FlukeProSim8;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.FlowControl;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.devices.simulation.GlobalSimulationObjectiveListener;
import org.mdpnp.devices.simulation.GlobalSimulationObjectiveMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoProsim68 extends AbstractDelegatingSerialDevice<FlukeProSim8> implements GlobalSimulationObjectiveListener {



    private final static Logger log = LoggerFactory.getLogger(DemoProsim68.class);

    private class MyFlukeProSim8 extends FlukeProSim8 {

        public MyFlukeProSim8(InputStream in, OutputStream out) {
            super(in, out);
        }

        @Override
        public void receiveString(String line) {
            log.debug("Received:"+line);
        }
    }

    protected final GlobalSimulationObjectiveMonitor monitor;

    public DemoProsim68(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
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

    public SerialProvider getSerialProvider() {
        SerialProvider serialProvider =  super.getSerialProvider();
        serialProvider.setDefaultSerialSettings(115200, DataBits.Eight, Parity.None, StopBits.One, FlowControl.Hardware);
        return serialProvider;
    }
    @Override
    protected FlukeProSim8 buildDelegate(InputStream in, OutputStream out) {
        return new MyFlukeProSim8(in, out);
    }
    @Override
    protected boolean delegateReceive(FlukeProSim8 delegate) throws IOException {
        delegate.receiveCommand();

        return true;
    }

    protected void pollTime() {
        try {
            if(ice.ConnectionState.Connected.equals(getState())) {
                Date date = getDelegate().getRealTimeClock();
            }
        } catch (IOException e) {
            log.error("Error polling time; disconnected?", e);
        } catch (ParseException e) {
            log.error("Cannot parse GETRTC response", e);
        }
    }

    @Override
    protected void doInitCommands() throws IOException {
        log.debug("Ident");
        String identifier = getDelegate().ident();
        if(null == identifier) {
            return;
        }
        String s[] = identifier.split(",");
        deviceIdentity.model = s[0];
        writeDeviceIdentity();

//        log.debug("localModel");
//        getDelegate().localMode();
        log.debug("validationOn");
        if(null == getDelegate().validationOn()) {
            return;
        }

//        log.debug("Press SpO2 key");
//        getDelegate().sendKey(KeyCode.SpO2, 100);
        log.debug("Remote mode");
        if(null == getDelegate().remoteMode()) {
            return;
        }

//        try {
//            log.debug("GETRTC="+getDelegate().getRealTimeClock());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        log.debug("SETRTC="+getDelegate().setRealTimeClock(new Date()));
//
//
//        try {
//            log.debug("GETRTC="+getDelegate().getRealTimeClock());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        reportConnected();
        super.doInitCommands();
    }

    @Override
    protected long getMaximumQuietTime() {
        return 5000L;
    }

    @Override
    protected long getConnectInterval() {
        return 3000L;
    }

    @Override
    protected long getNegotiateInterval() {
        return 1000L;
    }



    @Override
    public void disconnect() {
        boolean shouldSend = false;
        synchronized(stateMachine) {
            shouldSend = ice.ConnectionState.Connected.equals(getState());
        }
        if(shouldSend) {
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

    @Override
    protected String iconResourceName() {
        return "prosim8.png";
    }
    @Override
    public void simulatedNumeric(GlobalSimulationObjective gso) {
        try {
            if(rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(gso.metric_id.userData)) {
                getDelegate().normalSinusRhythmAdult((int) gso.value);
            } else if(rosetta.MDC_PULS_OXIM_SAT_O2.VALUE.equals(gso.metric_id.userData)) {
                getDelegate().saturation((int) gso.value);
            } else if(rosetta.MDC_RESP_RATE.VALUE.equals(gso.metric_id.userData)) {
                getDelegate().respirationRate((int)gso.value);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
