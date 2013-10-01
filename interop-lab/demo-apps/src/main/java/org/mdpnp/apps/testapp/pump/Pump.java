package org.mdpnp.apps.testapp.pump;

import com.rti.dds.subscription.SampleInfo;

public interface Pump {
    ice.InfusionStatus getInfusionStatus();
    SampleInfo getSampleInfo();
    PumpModel getParent();
    void setStop(boolean stop);

}
