package org.mdpnp.devices.simulation.pulseox;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;

public class MultiSimPulseOximeter extends AbstractSimulatedConnectedDevice {

    protected final InstanceHolder<ice.SampleArray> pleth[];

    private class MySimulatedPulseOximeter extends SimulatedPulseOximeter {
        private final int ordinal;

        public MySimulatedPulseOximeter(final int ordinal) {
            this.ordinal = ordinal;
        }

        @Override
        protected void receivePulseOx(long timestamp, int heartRate, int SpO2, Number[] plethValues, double msPerSample) {
            sampleArraySample(pleth[ordinal], plethValues, (int) msPerSample, null);
        }
    }

    private final MySimulatedPulseOximeter pulseox[];


    @Override
    public void connect(String str) {
        for(int i = 0; i < pulseox.length; i++) {
            pulseox[i].connect(executor);
        }
        super.connect(str);
    }

    @Override
    public void disconnect() {
        for(int i = 0; i < pulseox.length; i++) {
            pulseox[i].disconnect();
        }
        super.disconnect();
    }

    private final static int N = 10000;

    @SuppressWarnings("unchecked")
    public MultiSimPulseOximeter(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);



        this.pulseox = new MySimulatedPulseOximeter[N];
        this.pleth = new InstanceHolder[N];

        for(int i = 0; i < pleth.length; i++) {
            this.pulseox[i] = new MySimulatedPulseOximeter(i);
            pleth[i] = createSampleArrayInstance(rosetta.MDC_PULS_OXIM_PLETH.VALUE, i);
        }

        deviceIdentity.model = "Pulse Ox (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return "pulseox.png";
    }
}
