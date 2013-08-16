/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.oridion.capnostream;

import ice.ConnectionState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.oridion.capnostream.Capnostream.Command;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoCapnostream20 extends AbstractDelegatingSerialDevice<Capnostream> {

    // public static final Numeric FAST_STATUS = new
    // NumericImpl(DemoCapnostream20.class, "FAST_STATUS");
    // public static final Numeric SLOW_STATUS = new
    // NumericImpl(DemoCapnostream20.class, "SLOW_STATUS");
    // public static final Numeric CO2_ACTIVE_ALARMS = new
    // NumericImpl(DemoCapnostream20.class, "FAST_STATUS");
    // public static final Numeric SPO2_ACTIVE_ALARMS = new
    // NumericImpl(DemoCapnostream20.class, "FAST_STATUS");
    // public static final Enumeration CAPNOSTREAM_UNITS = new
    // EnumerationImpl(DemoCapnostream20.class, "CAPNOSTREAM_UNITS");
    // public static final Numeric EXTENDED_CO2_STATUS = new
    // NumericImpl(DemoCapnostream20.class, "EXTENDED_CO2_STATUS");

    @Override
    protected void stateChanging(ConnectionState newState, ConnectionState oldState) {
        if(ice.ConnectionState.Disconnected.equals(newState)) {
            unregisterAllNumericInstances();
            unregisterAllSampleArrayInstances();
        }
    }
    
    @Override
    protected long getMaximumQuietTime() {
        return 5000L;
    }

    @Override
    protected long getConnectInterval() {
        return 2000L;
    }

    @Override
    protected String iconResourceName() {
        return "capnostream.png";
    }

    protected InstanceHolder<ice.SampleArray> co2;
    protected InstanceHolder<ice.Numeric> spo2;
    protected InstanceHolder<ice.Numeric> pulserate;
 
    protected InstanceHolder<ice.Numeric> rr;
    protected InstanceHolder<ice.Numeric> etco2;
    protected InstanceHolder<ice.Numeric> fastStatus; // = new
                                                      // MutableNumericUpdateImpl(DemoCapnostream20.FAST_STATUS);
    protected InstanceHolder<ice.Numeric> slowStatus; // = new
                                                      // MutableNumericUpdateImpl(DemoCapnostream20.SLOW_STATUS);
    protected InstanceHolder<ice.Numeric> co2ActiveAlarms; // = new
                                                           // MutableNumericUpdateImpl(DemoCapnostream20.CO2_ACTIVE_ALARMS);
    protected InstanceHolder<ice.Numeric> spo2ActiveAlarms; // = new
                                                            // MutableNumericUpdateImpl(DemoCapnostream20.SPO2_ACTIVE_ALARMS);
    protected InstanceHolder<ice.Numeric> extendedCO2Status; // = new
                                                             // MutableNumericUpdateImpl(DemoCapnostream20.EXTENDED_CO2_STATUS);
    // protected final MutableEnumerationUpdate capnostreamUnits = new
    // MutableEnumerationUpdateImpl(DemoCapnostream20.CAPNOSTREAM_UNITS);

    public DemoCapnostream20(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        deviceIdentity.manufacturer = "Oridion";
        deviceIdentity.model = "Capnostream20";
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentityHandle = deviceIdentityWriter.register_instance(deviceIdentity);
        deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);

        deviceConnectivity.universal_device_identifier = deviceIdentity.universal_device_identifier;
        deviceConnectivityHandle = deviceConnectivityWriter.register_instance(deviceConnectivity);
        deviceConnectivityWriter.write(deviceConnectivity, deviceConnectivityHandle);

    }

    public DemoCapnostream20(int domainId, EventLoop eventLoop, SerialSocket serialSocket) {
        super(domainId, eventLoop, serialSocket);
        deviceIdentity.manufacturer = "Oridion";
        deviceIdentity.model = "Capnostream20";
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentityHandle = deviceIdentityWriter.register_instance(deviceIdentity);
        deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);

        deviceConnectivity.universal_device_identifier = deviceIdentity.universal_device_identifier;
        deviceConnectivityHandle = deviceConnectivityWriter.register_instance(deviceConnectivity);
        deviceConnectivityWriter.write(deviceConnectivity, deviceConnectivityHandle);

    }

    private static final int BUFFER_SAMPLES = 10;
    private final Number[] realtimeBuffer = new Number[BUFFER_SAMPLES];
    private int realtimeBufferCount = 0;

    protected volatile boolean connected = false;

    public class MyCapnostream extends Capnostream {
        public MyCapnostream(InputStream in, OutputStream out) {
            super(in, out);
        }

        @Override
        public boolean receiveNumerics(long date, int etCO2, int FiCO2, int respiratoryRate, int spo2, int pulserate,
                int slowStatus, int CO2ActiveAlarms, int SpO2ActiveAlarms, int noBreathPeriodSeconds,
                int etCo2AlarmHigh, int etCo2AlarmLow, int rrAlarmHigh, int rrAlarmLow, int fico2AlarmHigh,
                int spo2AlarmHigh, int spo2AlarmLow, int pulseAlarmHigh, int pulseAlarmLow, CO2Units units,
                int extendedCO2Status) {

            // We have an SpO2 value

            DemoCapnostream20.this.spo2 = numericSample(DemoCapnostream20.this.spo2, 0xFF == spo2 ? null : spo2,
                    ice.Physio.MDC_PULS_OXIM_SAT_O2.value());

            rr = numericSample(rr, 0xFF == respiratoryRate ? null : respiratoryRate, ice.Physio.MDC_RESP_RATE.value());

            etco2 = numericSample(etco2, 0xFF == etCO2 ? null : etCO2, ice.Physio.MDC_AWAY_CO2_EXP.value());

            DemoCapnostream20.this.pulserate = numericSample(DemoCapnostream20.this.pulserate, 0xFF == pulserate ? null
                    : pulserate, ice.Physio.MDC_PULS_OXIM_PULS_RATE.value());

            DemoCapnostream20.this.extendedCO2Status = numericSample(DemoCapnostream20.this.extendedCO2Status,
                    0xFF == extendedCO2Status ? null : extendedCO2Status, oridion.MDC_EXTENDED_CO2_STATUS.VALUE);

            DemoCapnostream20.this.slowStatus = numericSample(DemoCapnostream20.this.slowStatus,
                    0xFF == slowStatus ? null : slowStatus, oridion.MDC_SLOW_STATUS.VALUE);

            DemoCapnostream20.this.co2ActiveAlarms = numericSample(DemoCapnostream20.this.co2ActiveAlarms,
                    0xFF == CO2ActiveAlarms ? null : CO2ActiveAlarms, oridion.MDC_CO2_ACTIVE_ALARMS.VALUE);

            DemoCapnostream20.this.spo2ActiveAlarms = numericSample(DemoCapnostream20.this.spo2ActiveAlarms,
                    0xFF == SpO2ActiveAlarms ? null : SpO2ActiveAlarms, oridion.MDC_SPO2_ACTIVE_ALARMS.VALUE);

            return true;
        }

        @Override
        public boolean receiveCO2Wave(int messageNumber, double co2, int status) {
            DemoCapnostream20.this.fastStatus = numericSample(DemoCapnostream20.this.fastStatus,
                    status, oridion.MDC_FAST_STATUS.VALUE);

            if (0 != (0x01 & status)) {
                log.warn("invalid CO2 value ignored " + co2 + " with fast status " + Integer.toHexString(status));
                return true;
            }
            if(0 != (0x02 & status)) {
                log.warn("Initialization " + co2 + " with fast status " + Integer.toHexString(status));
                return true;
            }
            if(0 != (0x04 & status)) {
                log.warn("occlusion " + co2 + " with fast status " + Integer.toHexString(status));
                return true;
            }
            if(0 != (0x08 & status)) {
//                log.debug("End of breath");
            }
            if(0 != (0x10 & status)) {
                // SFM in progress
                log.warn("SFM in progress " + co2 + " with fast status " + Integer.toHexString(status));
                return true;
            }
            if(0 != (0x20 & status)) {
                // purge in progress
                log.warn("purge in progress " + co2 + " with fast status " + Integer.toHexString(status));
                return true;
            }
            if(0 != (0x40 & status)) {
                // filter line not connected
//                if(null != DemoCapnostream20.this.co2) {
//                    unregisterSampleArrayInstance(DemoCapnostream20.this.co2);
//                    DemoCapnostream20.this.co2 = null;
//                }
                log.warn("Filterline indicates disconnected " + co2 + " with fast status " + Integer.toHexString(status));
                return true;
            }
            if(0 != (0x80 & status)) {
                log.warn("CO2 malfunction " + co2 + " with fast status " + Integer.toHexString(status));
                return true;
            }

            realtimeBuffer[realtimeBufferCount++] = co2;
            if (realtimeBufferCount == realtimeBuffer.length) {
                realtimeBufferCount = 0;
                DemoCapnostream20.this.co2 = sampleArraySample(DemoCapnostream20.this.co2, realtimeBuffer, 50,
                        ice.MDC_CAPNOGRAPH.VALUE);

            }
            return true;
        }

        @Override
        public boolean receiveDeviceIdSoftwareVersion(String softwareVersion, Date softwareReleaseDate,
                PulseOximetry pulseOximetry, String revision, String serial_number) {
            deviceIdentity.serial_number = serial_number;
            deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
            return true;

        }

        @Override
        public boolean receiveDeviceIdSoftwareVersion(String s) {
            connected = true;
            return super.receiveDeviceIdSoftwareVersion(s);
        }

    }

    private final Logger log = LoggerFactory.getLogger(DemoCapnostream20.class);

    @Override
    protected Capnostream buildDelegate(InputStream in, OutputStream out) {
        return new MyCapnostream(in, out);
    }

    private ScheduledFuture<?> linkIsActive;

    @Override
    protected boolean doInitCommands(OutputStream outputStream) throws IOException {
        super.doInitCommands(outputStream);

        long giveup = System.currentTimeMillis() + 10000L;

        connected = false;

        while (System.currentTimeMillis() < giveup && !connected) {
            getDelegate().sendCommand(Command.EnableComm);
            try {
                Thread.sleep(400L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        getDelegate().sendCommand(Command.LinkIsActive);
        getDelegate().sendHostMonitoringId("ICE");

        // JP ... need to test this
        linkIsActive = executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {

                    getDelegate().sendCommand(Command.LinkIsActive);
                    // getDelegate().sendHostMonitoringId("ICE");
                    // log.debug("Sent host id and command");
                } catch (IOException e) {
                    log.error("Error sending link is active message", e);
                }
            }
        }, 5000L, 5000L, TimeUnit.MILLISECONDS);

        getDelegate().sendCommand(Command.StartRTComm);
        return connected;
    }

    @Override
    public void disconnect() {
        if (null != linkIsActive) {
            linkIsActive.cancel(false);
            linkIsActive = null;
        }

        Capnostream capnostream = getDelegate(false);

        if (null != capnostream) {
            try {
                capnostream.sendCommand(Command.StopRTComm);
                capnostream.sendCommand(Command.DisableComm);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.trace(" was already null in disconnect");
        }
        super.disconnect();
    }

    @Override
    public SerialProvider getSerialProvider() {
        SerialProvider serialProvider = super.getSerialProvider();
        serialProvider.setDefaultSerialSettings(115200, DataBits.Eight, Parity.None, StopBits.One);
        return serialProvider;
    }

    @Override
    protected boolean delegateReceive(Capnostream delegate) throws IOException {
        return delegate.receive();
    }

}
