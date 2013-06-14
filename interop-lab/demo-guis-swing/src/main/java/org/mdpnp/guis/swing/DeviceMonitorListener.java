package org.mdpnp.guis.swing;

import ice.DeviceConnectivity;
import ice.DeviceIdentity;
import ice.Numeric;
import ice.SampleArray;

public interface DeviceMonitorListener {
    void deviceIdentity(DeviceIdentity di);
    void deviceConnectivity(DeviceConnectivity dc);
    void numeric(Numeric n);
    void sampleArray(SampleArray sampleArray);
    void addNumeric(int name);
    void removeNumeric(int name);
    void addSampleArray(int name);
    void removeSampleArray(int name);
}
