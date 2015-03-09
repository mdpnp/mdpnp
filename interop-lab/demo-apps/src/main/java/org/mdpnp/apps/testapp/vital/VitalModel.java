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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;

import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

public interface VitalModel extends ObservableList<Vital> {
    enum State {
        Alarm, Warning, Normal
    };
    DeviceListModel getDeviceListModel();

    Vital addVital(String label, String units, String[] names, Double low, Double high, Double criticalLow, Double criticalHigh, double minimum,
            double maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color);

    ice.GlobalAlarmSettingsObjectiveDataWriter getWriter();

    void start(Subscriber subscriber, Publisher publisher, EventLoop eventLoop);

    void stop();

    IntegerProperty countWarningsBecomeAlarmProperty();
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
    ReadOnlyObjectProperty<State> stateProperty();
    State getState();

    /**
     * Get any current warning text
     * 
     * @return
     */
    ReadOnlyStringProperty warningTextProperty();
    String getWarningText();

    /**
     * Reset the infusion pump interlock
     */
    void resetInfusion();

    /**
     * Has the infusion been stopped?
     */
    ReadOnlyBooleanProperty isInfusionStoppedProperty();
    boolean isInfusionStopped();

    /**
     * Why has the infusion been stopped
     * 
     * @return
     */
    ReadOnlyStringProperty interlockTextProperty();
    String getInterlockText();

    void updateNumeric(final String unique_device_identifier, final String metric_id, final int instance_id, final long timestamp, final float value);

    void removeNumeric(final String udi, final String metric_id, final int instance_id);
}
