package org.mdpnp.guis.swing;

import com.rti.dds.subscription.SampleInfoSeq;

public interface DeviceMonitorListener {
    void deviceIdentity(ice.DeviceIdentityDataReader reader, ice.DeviceIdentitySeq di, SampleInfoSeq sampleInfo);
    void deviceConnectivity(ice.DeviceConnectivityDataReader reader, ice.DeviceConnectivitySeq dc, SampleInfoSeq sampleInfo);
    void numeric(ice.NumericDataReader reader, ice.NumericSeq n, SampleInfoSeq sampleInfo);
    void sampleArray(ice.SampleArrayDataReader reader, ice.SampleArraySeq sampleArray, SampleInfoSeq sampleInfo);
//    void addNumeric(int name);
//    void removeNumeric(int name);
//    void addSampleArray(int name);
//    void removeSampleArray(int name);
}
