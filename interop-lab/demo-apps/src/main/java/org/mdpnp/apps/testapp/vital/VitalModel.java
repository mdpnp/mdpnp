package org.mdpnp.apps.testapp.vital;

import java.awt.Color;

import org.mdpnp.apps.testapp.DeviceIcon;
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
    Vital addVital(String label, String units, String[] names, Float low, Float high, Float criticalLow, Float criticalHigh, float minimum, float maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color);
    boolean removeVital(Vital vital);
    Vital removeVital(int i);

    void addListener(VitalModelListener vitalModelListener);
    boolean removeListener(VitalModelListener vitalModelListener);

    ice.DeviceIdentity getDeviceIdentity(String udi);
    ice.DeviceConnectivity getDeviceConnectivity(String udi);
    DeviceIcon getDeviceIcon(String udi);

    ice.GlobalAlarmSettingsObjectiveDataWriter getWriter();

    void start(Subscriber subscriber, EventLoop eventLoop);
    void stop();

    void setCountWarningsBecomeAlarm(int countWarningsBecomeAlarm);

    /**
     * This many concurrent warnings will trigger an alarm
     * @return
     */
    int getCountWarningsBecomeAlarm();



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
