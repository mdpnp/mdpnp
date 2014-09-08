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

import ice.DeviceConnectivity;
import ice.DeviceIdentity;

import java.awt.Color;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.mdpnp.apps.testapp.DeviceIcon;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

/**
 * @author Jeff Plourde
 *
 */
public class VitalListModelAdapterImpl implements VitalListModelAdapter {

    private final VitalModel model;

    public VitalListModelAdapterImpl(VitalModel model) {
        this.model = model;
    }

    @Override
    public int getCount() {
        return model.getCount();
    }

    @Override
    public Vital getVital(int i) {
        return model.getVital(i);
    }

    @Override
    public Vital addVital(String label, String units, String[] metric_ids, Float low, Float high, Float criticalLow, Float criticalHigh,
            float minimum, float maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color) {
        return model.addVital(label, units, metric_ids, low, high, criticalLow, criticalHigh, minimum, maximum, valueMsWarningLow,
                valueMsWarningHigh, color);
    }

    @Override
    public boolean removeVital(Vital vital) {
        return model.removeVital(vital);
    }

    @Override
    public Vital removeVital(int i) {
        return model.removeVital(i);
    }

    @Override
    public void addListener(VitalModelListener vitalModelListener) {
        addListener(vitalModelListener);
    }

    @Override
    public boolean removeListener(VitalModelListener vitalModelListener) {
        return removeListener(vitalModelListener);
    }

    // @Override
    // public DeviceIdentity getDeviceIdentity(String udi) {
    // return model.getDeviceIdentity(udi);
    // }
    //
    // @Override
    // public DeviceConnectivity getDeviceConnectivity(String udi) {
    // return model.getDeviceConnectivity(udi);
    // }

    @Override
    public void start(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
        model.start(subscriber, publisher, eventLoop);
    }

    @Override
    public void stop() {
        model.stop();
    }

    @Override
    public int getSize() {
        return getCount();
    }

    @Override
    public Object getElementAt(int index) {
        return getVital(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        model.addListener(new VitalListModelListenerAdapter(l));
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        model.removeListener(new VitalListModelListenerAdapter(l));
    }

    @SuppressWarnings("serial")
    private static final class MutableListDataEvent extends ListDataEvent {
        private int type, index0, index1;

        public MutableListDataEvent(Object source, int type, int index0, int index1) {
            super(source, type, index0, index1);
            setRange(index0, index1);
            setType(type);
        }

        public void setRange(int index0, int index1) {
            this.index0 = Math.min(index0, index1);
            this.index1 = Math.max(index0, index1);
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setTypeAndRange(int type, int index0, int index1) {
            setType(type);
            setRange(index0, index1);
        }

        @Override
        public int getIndex0() {
            return index0;
        }

        @Override
        public int getIndex1() {
            return index1;
        }

        @Override
        public int getType() {
            return type;
        }
    }

    private class VitalListModelListenerAdapter implements VitalModelListener {
        private final ListDataListener listener;
        private final MutableListDataEvent event = new MutableListDataEvent(VitalListModelAdapterImpl.this, ListDataEvent.CONTENTS_CHANGED, 0, 0);

        public VitalListModelListenerAdapter(ListDataListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean equals(Object obj) {
            return listener.equals(obj);
        }

        @Override
        public int hashCode() {
            return listener.hashCode();
        }

        @Override
        public void vitalChanged(VitalModel model, Vital vital) {
            event.setTypeAndRange(ListDataEvent.CONTENTS_CHANGED, 0, getCount() - 1);
            listener.contentsChanged(event);
        }

        @Override
        public void vitalRemoved(VitalModel model, Vital vital) {
            // Cheat ... remove from the beginning and refresh
            event.setTypeAndRange(ListDataEvent.INTERVAL_REMOVED, 0, 0);
            listener.intervalRemoved(event);
            event.setTypeAndRange(ListDataEvent.CONTENTS_CHANGED, 0, getCount() - 1);
            listener.contentsChanged(event);
        }

        @Override
        public void vitalAdded(VitalModel model, Vital vital) {
            event.setTypeAndRange(ListDataEvent.INTERVAL_ADDED, 0, 0);
            listener.intervalAdded(event);
            event.setTypeAndRange(ListDataEvent.CONTENTS_CHANGED, 0, getCount() - 1);
            listener.contentsChanged(event);
        }
    }

    @Override
    public State getState() {
        return model.getState();
    }

    @Override
    public String getWarningText() {
        return model.getWarningText();
    }

    @Override
    public void resetInfusion() {
        model.resetInfusion();
    }

    @Override
    public boolean isInfusionStopped() {
        return model.isInfusionStopped();
    }

    @Override
    public String getInterlockText() {
        return model.getInterlockText();
    }

    @Override
    public void setCountWarningsBecomeAlarm(int countWarningsBecomeAlarm) {
        model.setCountWarningsBecomeAlarm(countWarningsBecomeAlarm);
    }

    @Override
    public int getCountWarningsBecomeAlarm() {
        return model.getCountWarningsBecomeAlarm();
    }

    @Override
    public DeviceIdentity getDeviceIdentity(String udi) {
        return model.getDeviceIdentity(udi);
    }

    @Override
    public DeviceConnectivity getDeviceConnectivity(String udi) {
        return model.getDeviceConnectivity(udi);
    }

    @Override
    public DeviceIcon getDeviceIcon(String udi) {
        return model.getDeviceIcon(udi);
    }

    @Override
    public ice.GlobalAlarmSettingsObjectiveDataWriter getWriter() {
        return model.getWriter();
    }

}
