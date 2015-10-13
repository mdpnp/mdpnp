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

import java.awt.*;
import java.awt.List;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.publication.Publisher;

public interface VitalModel extends ObservableList<Vital> {
    enum State {
        Alarm, Warning, Normal
    };

    DeviceListModel getDeviceListModel();

    Vital addVital(String label, String units, String[] names, Double low, Double high, Double criticalLow, Double criticalHigh, double minimum,
            double maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color);

    ice.GlobalAlarmLimitObjectiveDataWriter getWriter();

    void start(Publisher publisher, EventLoop eventLoop);

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
    ReadOnlyObjectProperty<StateChange> stateProperty();
    State getState();

    /**
     * Get any current warning text
     * 
     * @return
     */
    ReadOnlyStringProperty warningTextProperty();
    String getWarningText();

    void addNumeric(NumericFx numeric);
    void removeNumeric(NumericFx numeric);


    class StateChange {
        public final java.util.List<Advisory> advisories;
        public final State state;

        public StateChange(State state) {
            this(state, Collections.emptyList());
        }

        public StateChange(State state, java.util.List<Advisory> advisories) {
            this.advisories = advisories;
            this.state = state;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StateChange that = (StateChange) o;
            return state == that.state;
        }

        @Override
        public int hashCode() {
            return state.hashCode();
        }
    }

    class Advisory implements Comparable<Advisory> {
        public final State state;
        public final String cause;

        @Override
        public int compareTo(Advisory o) {
            return state.compareTo(o.state);
        }

        public Advisory(State state, String cause) {
            this.state = state;
            this.cause = cause;
        }

        @Override
        public String toString() {
            return "{" + state + "'" + cause + "'}";
        }
    }
}
