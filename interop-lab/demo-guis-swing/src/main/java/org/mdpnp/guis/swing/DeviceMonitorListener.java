package org.mdpnp.guis.swing;

import com.rti.dds.subscription.SampleInfo;

import ice.DeviceConnectivity;
import ice.DeviceIdentity;
import ice.Numeric;
import ice.SampleArray;

public interface DeviceMonitorListener {
    void deviceIdentity(DeviceIdentity di, SampleInfo sampleInfo);
    void deviceConnectivity(DeviceConnectivity dc, SampleInfo sampleInfo);
    void numeric(Numeric n, SampleInfo sampleInfo);
    void sampleArray(SampleArray sampleArray, SampleInfo sampleInfo);
    void addNumeric(int name);
    void removeNumeric(int name);
    void addSampleArray(int name);
    void removeSampleArray(int name);
}
