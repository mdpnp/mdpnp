/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp.vital;

import java.awt.Color;

import org.mdpnp.apps.testapp.DeviceIcon;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

public interface VitalModel {
    enum State {
        Alarm, Warning, Normal
    };

    int getCount();

    Vital getVital(int i);

    Vital addVital(String label, String units, String[] names, Float low, Float high, Float criticalLow, Float criticalHigh, float minimum,
            float maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color);

    boolean removeVital(Vital vital);

    Vital removeVital(int i);

    void addListener(VitalModelListener vitalModelListener);

    boolean removeListener(VitalModelListener vitalModelListener);

    ice.DeviceIdentity getDeviceIdentity(String udi);

    ice.DeviceConnectivity getDeviceConnectivity(String udi);

    DeviceIcon getDeviceIcon(String udi);

    ice.GlobalAlarmSettingsObjectiveDataWriter getWriter();

    void start(Subscriber subscriber, Publisher publisher, EventLoop eventLoop);

    void stop();

    void setCountWarningsBecomeAlarm(int countWarningsBecomeAlarm);

    /**
     * This many concurrent warnings will trigger an alarm
     * 
     * @return
     */
    int getCountWarningsBecomeAlarm();

    /**
     * Get the current state of the VitalModel
     * 
     * @return
     */
    State getState();

    /**
     * Get any current warning text
     * 
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
     * 
     * @return
     */
    String getInterlockText();
}
