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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ModifiableObservableListBase;
import javafx.util.Callback;

import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;

/**
 * @author Jeff Plourde
 *
 */
public class VitalModelImpl extends ModifiableObservableListBase<Vital> implements VitalModel, ListChangeListener<Value> {
    private final Callback<Vital, Observable[]> extractor = new Callback<Vital, Observable[]>() {

        @Override
        public Observable[] call(Vital param) {
            return new Observable[] {
//                    param.anyOutOfBoundsProperty(),
//                    param.countOutOfBoundsProperty(),
                    param.criticalHighProperty(),
                    param.criticalLowProperty(),
                    param.warningHighProperty(),
                    param.warningLowProperty(),
                    param.ignoreZeroProperty(),
                    param.noValueWarningProperty(),
                    param.valueMsWarningHighProperty(),
                    param.valueMsWarningLowProperty()
            };
        }
        
    };
    
    
    
    private final List<Vital> vitals = Collections.synchronizedList(new ArrayList<Vital>());

    protected Publisher publisher;
    protected EventLoop eventLoop;
    private ObjectProperty<State> state = new SimpleObjectProperty<State>(this, "state", State.Normal);

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(VitalModelImpl.class);

    @Override
    public void removeNumeric(final String udi, final String metric_id, final int instance_id) {
        if(Platform.isFxApplicationThread()) {
            _removeNumeric(udi, metric_id, instance_id);
        } else {
            Platform.runLater(new Runnable() {
                public void run() {
                    _removeNumeric(udi, metric_id, instance_id);
                }
            });
        }
    }
    
    private void _removeNumeric(final String udi, final String metric_id, final int instance_id) {
        for (Vital v : this) {
            if (v != null) {
                for (String x : v.getMetricIds()) {
                    if (x.equals(metric_id)) {
                        ListIterator<Value> li = v.listIterator();
                        while (li.hasNext()) {
                            Value va = li.next();
                            if (va.getUniqueDeviceIdentifier().equals(udi) && va.getInstanceId() == instance_id) {
                                li.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    private ice.GlobalAlarmSettingsObjectiveDataWriter writer;
    private Topic globalAlarmSettingsTopic;

    public ice.GlobalAlarmSettingsObjectiveDataWriter getWriter() {
        return writer;
    }

    public DeviceListModel getDeviceListModel() {
        return deviceListModel;
    }

    @Override
    public void updateNumeric(final String unique_device_identifier, final String metric_id,
            final int instance_id, final long timestamp, final float value) {
        if(Platform.isFxApplicationThread()) {
            _updateNumeric(unique_device_identifier, metric_id, instance_id, timestamp, value);
        } else {
            Platform.runLater(new Runnable() {
                public void run() {
                    _updateNumeric(unique_device_identifier, metric_id, instance_id, timestamp, value);
                }
            });
        }
    }
    
    private void _updateNumeric(final String unique_device_identifier, final String metric_id,
            final int instance_id, final long timestamp, final float value) {
        // TODO linear search? Query Condition should be vital specific
        // or maybe these should be hashed because creating myriad
        // QueryConditions is not advisable
        for (Vital v : this) {
            if (v != null) {
                for (String x : v.getMetricIds()) {
                    // Change to this vital from a source
                    if (x.equals(metric_id)) {
                        boolean updated = false;
                        for (Value va : v) {
                            if (va.getInstanceId() == instance_id && va.getMetricId().equals(metric_id)
                                    && va.getUniqueDeviceIdentifier().equals(unique_device_identifier)) {
                                va.updateFrom(timestamp, value);
                                updated = true;
                                break;
                            }
                        }
                        if (!updated) {
                            final Value va = new ValueImpl(unique_device_identifier, metric_id, instance_id, v);
                            va.updateFrom(timestamp, value);
                            v.add(va);
                            
                        }
                    }
                }
            }
        }
    }

    @Override
    public Vital addVital(String label, String units, String[] names, Double low, Double high, Double criticalLow, Double criticalHigh, double minimum,
            double maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color) {
        final Vital v = new VitalImpl(this, label, units, names, low, high, criticalLow, criticalHigh, minimum, maximum, valueMsWarningLow,
                valueMsWarningHigh, color);
        add(v);
        return v;
    }

    public Device getDevice(String udi) {
        if (null == udi) {
            return null;
        }
        DeviceListModel deviceListModel = this.deviceListModel;
        return null == deviceListModel ? null : deviceListModel.getByUniqueDeviceIdentifier(udi);
    }

    @Override
    public void start(final Publisher publisher, final EventLoop eventLoop) {
        this.eventLoop = eventLoop;
        final CountDownLatch latch = new CountDownLatch(1);
        eventLoop.doLater(() -> {
            VitalModelImpl.this.publisher = publisher;
            DomainParticipant participant = publisher.get_participant();

            ice.GlobalAlarmSettingsObjectiveTypeSupport.register_type(participant, ice.GlobalAlarmSettingsObjectiveTypeSupport.get_type_name());

            globalAlarmSettingsTopic = participant.create_topic(ice.GlobalAlarmSettingsObjectiveTopic.VALUE,
                    ice.GlobalAlarmSettingsObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null,
                    StatusKind.STATUS_MASK_NONE);
            writer = (ice.GlobalAlarmSettingsObjectiveDataWriter) publisher.create_datawriter_with_profile(globalAlarmSettingsTopic, QosProfiles.ice_library,
                    QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        if(eventLoop != null) {
            eventLoop.doLater(new Runnable() {
                public void run() {
                    publisher.delete_datawriter(writer);
    
                    VitalModelImpl.this.eventLoop = null;
                }
            });

        }
    }

    private static final String DEFAULT_INTERLOCK_TEXT = "";

    private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private String[] advisories = new String[0];
    private final Date now = new Date();
    private final StringBuilder warningTextBuilder = new StringBuilder();
    private StringProperty warningText = new SimpleStringProperty(this, "warningText", "");
    private StringProperty interlockText = new SimpleStringProperty(this, "interlockText", DEFAULT_INTERLOCK_TEXT);
    private BooleanProperty interlock = new SimpleBooleanProperty(this, "interlock", false);
   
    @Override
    public ReadOnlyBooleanProperty isInfusionStoppedProperty() {
        return interlock;
    }


    private final void updateState() {
        // This used to be synchronized but now we do this instead
        if(!Platform.isFxApplicationThread()) {
            throw new IllegalThreadStateException("Must be on the Fx App Thread");
        }
        
        int N = size();

        while (advisories.length < N) {
            advisories = new String[2 * N + 1];
        }
        now.setTime(System.currentTimeMillis());
        String time = timeFormat.format(now);

        int countWarnings = 0;

        for (int i = 0; i < N; i++) {
            Vital vital = get(i);
            advisories[i] = null;
            if (vital.isNoValueWarning() && vital.isEmpty()) {
                countWarnings++;
                advisories[i] = "- no source of " + vital.getLabel() + "\r\n";
            } else {
                for (Value val : vital) {
                    if (val.isAtOrBelowLow()) {
                        countWarnings++;
                        advisories[i] = "- low " + vital.getLabel() + " " + val.getValue() + " " + vital.getUnits() + "\r\n";
                    }
                    if (val.isAtOrAboveHigh()) {
                        countWarnings++;
                        advisories[i] = "- high " + vital.getLabel() + " " + val.getValue() + " " + vital.getUnits() + "\r\n";
                    }
                }
            }
        }

        // Advisory processing
        if (countWarnings > 0) {
            warningTextBuilder.delete(0, warningTextBuilder.length());
            for (int i = 0; i < N; i++) {
                if (null != advisories[i]) {
                    warningTextBuilder.append(advisories[i]);
                }
            }
            warningTextBuilder.append("at ").append(time);
            warningText.set(warningTextBuilder.toString());
            state.set(State.Warning);
        } else {
            warningText.set("");
            state.set(State.Normal);
        }

        if (countWarnings >= getCountWarningsBecomeAlarm()) {
            state.set(State.Alarm);
            stopInfusion("Pump Stopped\r\n" + warningText.get() + "\r\nnurse alerted");
        } else {
            for (int i = 0; i < N; i++) {
                Vital vital = get(i);
                for (Value val : vital) {
                    if (val.isAtOrBelowCriticalLow()) {
                        state.set(State.Alarm);
                        stopInfusion("Pump Stopped\r\n- low " + vital.getLabel() + " " + val.getValue() + " " + vital.getUnits() + "\r\nat "
                                + time + "\r\nnurse alerted");
                        break;
                    } else if (val.isAtOrAboveCriticalHigh()) {
                        state.set(State.Alarm);
                        stopInfusion("Pump Stopped\r\n- high " + vital.getLabel() + " " + +val.getValue() + " " + vital.getUnits()
                                + "\r\nat " + time + "\r\nnurse alerted");
                        break;
                    }
                }
            }
        }

    }

    private final void stopInfusion(String str) {
        if (!interlock.get()) {
            interlock.set(true);
            interlockText.set(str);
        }
    }
    
    @Override
    public ReadOnlyObjectProperty<State> stateProperty() {
        return state;
    }

    @Override
    public State getState() {
        return state.get();
    }
    
    @Override
    public ReadOnlyStringProperty interlockTextProperty() {
        return interlockText;
    }

    @Override
    public String getInterlockText() {
        return interlockText.get();
    }

    @Override
    public ReadOnlyStringProperty warningTextProperty() {
        return warningText;
    }
    @Override
    public String getWarningText() {
        return warningText.get();
    }

    @Override
    public void resetInfusion() {
        interlock.set(false);
        interlockText.set(DEFAULT_INTERLOCK_TEXT);
    }

    @Override
    public boolean isInfusionStopped() {
        return interlock.get();
    }

    private IntegerProperty countWarningsBecomeAlarm = new SimpleIntegerProperty(this, "countWarningsBecomeAlarm", 2);

    @Override
    public IntegerProperty countWarningsBecomeAlarmProperty() {
        return countWarningsBecomeAlarm;
    }
    
    @Override
    public void setCountWarningsBecomeAlarm(int countWarningsBecomeAlarm) {
        this.countWarningsBecomeAlarm.set(countWarningsBecomeAlarm);
    }

    @Override
    public int getCountWarningsBecomeAlarm() {
        return countWarningsBecomeAlarm.get();
    }

    final DeviceListModel deviceListModel;
    private final ElementObserver<Vital> elementObserver;
    
    public VitalModelImpl(DeviceListModel deviceListModel) {
        this.deviceListModel = deviceListModel;
        this.elementObserver = new ElementObserver<Vital>(extractor, new Callback<Vital, InvalidationListener>() {

            @Override
            public InvalidationListener call(final Vital e) {
                return new InvalidationListener() {

                    @Override
                    public void invalidated(Observable observable) {
                        beginChange();
                        int i = 0;
                        final int size = size();
                        for (; i < size; ++i) {
                            if (get(i) == e) {
                                nextUpdate(i);
                            }
                        }
                        endChange();
                    }
                };
            }
        }, this);
        
        addListener(new ListChangeListener<Vital>() {

            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Vital> c) {
                updateState();
            }
            
        });
    }

    @Override
    public Vital get(int index) {
        return vitals.get(index);
    }

    @Override
    public int size() {
        return vitals.size();
    }

    @Override
    protected void doAdd(int index, Vital element) {
        element.addListener(this);
        elementObserver.attachListener(element);
        vitals.add(index, element);
    }

    @Override
    protected Vital doSet(int index, Vital element) {
        Vital removed =  vitals.set(index, element);
        removed.removeListener(this);
        elementObserver.detachListener(removed);
        elementObserver.attachListener(element);
        removed.addListener(this);
        return removed;
    }

    @Override
    protected Vital doRemove(int index) {
        Vital v = vitals.remove(index);
        elementObserver.detachListener(v);
        v.removeListener(this);
        if(null != v) {
            v.destroy();
        }
        return v;
    }
    @Override
    public void clear() {
        if (elementObserver != null) {
            final int sz = size();
            for (int i = 0; i < sz; ++i) {
                get(i).removeListener(this);
                elementObserver.detachListener(get(i));
                
            }
        }
        if (hasListeners()) {
            beginChange();
            nextRemove(0, this);
        }
        vitals.clear();
        ++modCount;
        if (hasListeners()) {
            endChange();
        }
    }

    @Override
    public void onChanged(javafx.collections.ListChangeListener.Change<? extends Value> c) {
        updateState();
    }    
}
