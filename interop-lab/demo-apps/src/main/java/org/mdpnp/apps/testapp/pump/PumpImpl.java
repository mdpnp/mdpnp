package org.mdpnp.apps.testapp.pump;

import com.rti.dds.subscription.SampleInfo;

import ice.InfusionStatus;

public class PumpImpl implements Pump {
    private final PumpModel parent;
    private final ice.InfusionStatus infusionStatus = new ice.InfusionStatus();
    private final SampleInfo sampleInfo = new SampleInfo();
    
    @Override
    public String toString() {
        return infusionStatus.universal_device_identifier;
    }
    
    public PumpImpl(PumpModel parent, ice.InfusionStatus infusionStatus, SampleInfo sampleInfo) {
        this.parent = parent;
        this.infusionStatus.copy_from(infusionStatus);
        this.sampleInfo.copy_from(sampleInfo);
    }
    
    public ice.InfusionStatus getInfusionStatus() {
        return infusionStatus;
    }
    public SampleInfo getSampleInfo() {
        return sampleInfo;
    }

    @Override
    public PumpModel getParent() {
        return parent;
    }

    @Override
    public void setStop(boolean stop) {
        parent.setStop(this, stop);
    }
}
