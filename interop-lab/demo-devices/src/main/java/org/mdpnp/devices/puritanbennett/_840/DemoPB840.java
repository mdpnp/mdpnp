package org.mdpnp.devices.puritanbennett._840;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.mdpnp.devices.AbstractDevice.InstanceHolder;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;

public class DemoPB840 extends AbstractDelegatingSerialDevice<PB840> {

    private InstanceHolder<ice.SampleArray> flowSampleArray, pressureSampleArray;
    
    private class MyPB840 extends PB840 {

        public MyPB840(InputStream input, OutputStream output) {
            super(input, output);
        }
        @Override
        public void receiveBreath(Collection<Number> flow, Collection<Number> pressure) {
            reportConnected("Breath Info Received");
            flowSampleArray = sampleArraySample(flowSampleArray, flow, rosetta.MDC_FLOW_AWAY.VALUE, 0, rosetta.MDC_DIM_L_PER_MIN.VALUE, 50, null);
            pressureSampleArray = sampleArraySample(pressureSampleArray, pressure, rosetta.MDC_PRESS_AWAY.VALUE, 0, rosetta.MDC_DIM_CM_H2O.VALUE, 50, null);
        }
    }
    
    public DemoPB840(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop, PB840.class);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Puritan Bennett";
        deviceIdentity.model = "840";
        writeDeviceIdentity();
    }

    @Override
    protected PB840 buildDelegate(int idx, InputStream in, OutputStream out) {
        return new MyPB840(in, out);
    }

    @Override
    protected boolean delegateReceive(int idx, PB840 delegate) throws IOException {
        delegate.receive();
        return true;
    }
    
    @Override
    public SerialProvider getSerialProvider(int idx) {
        SerialProvider serialProvider = super.getSerialProvider(idx);
        serialProvider.setDefaultSerialSettings(38400, DataBits.Eight, Parity.None, StopBits.One);
        return serialProvider;
    }
    
    @Override
    protected long getMaximumQuietTime(int idx) {
        return 5000L;
    }
    
    @Override
    protected String iconResourceName() {
        return "pb840.png";
    }

}
