package org.mdpnp.apps.testapp.vital;

import org.mdpnp.devices.EventLoop;

import com.rti.dds.subscription.Subscriber;


public interface VitalModel {
    enum State {
        Alarm,
        Warning,
        Normal
    };
    
    
    int getCount();
    Vital getVital(int i);
    Vital addVital(String label, String units, int[] names, float low, float high, float minimum, float maximum);
    boolean removeVital(Vital vital);
    Vital removeVital(int i);
    
    void addListener(VitalModelListener vitalModelListener);
    boolean removeListener(VitalModelListener vitalModelListener);
    
    ice.DeviceIdentity getDeviceIdentity(String udi);
    ice.DeviceConnectivity getDeviceConnectivity(String udi);
    
    void start(Subscriber subscriber, EventLoop eventLoop);
    void stop();

    /**
     * Get the current state of the VitalModel
     * @return
     */
    State getState();
    
    /**
     * Get any current warning text
     * @return
     */
    String getWarningText();
    
    
    /**
     * Reset the infusion pump interlock
     */
    void resetInfusion();

    /**
     * Has the infusion been stopped?
     */
    boolean isInfusionStopped();
    
    /**
     * Why has the infusion been stopped
     * @return
     */
    String getInterlockText();
}
