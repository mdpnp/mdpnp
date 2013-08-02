package org.mdpnp.apps.testapp.vital;

import org.mdpnp.devices.EventLoop;

import com.rti.dds.subscription.Subscriber;


public interface VitalModel {
    int getCount();
    Vital getVital(int i);
    Vital addVital(String label, int[] names, int minimum, int maximum);
    boolean removeVital(Vital vital);
    Vital removeVital(int i);
    
    void addListener(VitalModelListener vitalModelListener);
    boolean removeListener(VitalModelListener vitalModelListener);
    
    ice.DeviceIdentity getDeviceIdentity(String udi);
    ice.DeviceConnectivity getDeviceConnectivity(String udi);
    
    void start(Subscriber subscriber, EventLoop eventLoop);
    void stop();
}
