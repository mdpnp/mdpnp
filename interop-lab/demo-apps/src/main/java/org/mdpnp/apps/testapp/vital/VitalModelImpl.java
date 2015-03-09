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

import ice.Numeric;
import ice.NumericDataReader;
import ice.NumericSeq;
import ice.NumericTopic;
import ice.NumericTypeSupport;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

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
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicDescription;

/**
 * @author Jeff Plourde
 *
 */
public class VitalModelImpl extends ModifiableObservableListBase<Vital> implements VitalModel {
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

    protected NumericDataReader numericReader;
    
    protected Subscriber subscriber;
    protected Publisher publisher;
    protected EventLoop eventLoop;
    private ObjectProperty<State> state = new SimpleObjectProperty<State>(this, "state", State.Normal);

    private static final Logger log = LoggerFactory.getLogger(VitalModelImpl.class);

    private final EventLoop.ConditionHandler numericHandler = new EventLoop.ConditionHandler() {
        private final NumericSeq num_seq = new NumericSeq();
        private final SampleInfoSeq info_seq = new SampleInfoSeq();

        @Override
        public void conditionChanged(Condition condition) {
            try {
                for (;;) {
                    try {
                        numericReader.read(num_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                        final int size = info_seq.size();
                        for (int i = 0; i < size; i++) {

                            SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);

                            if (0 != (sampleInfo.instance_state & InstanceStateKind.NOT_ALIVE_INSTANCE_STATE)) {
                                Numeric keyHolder = new Numeric();
                                numericReader.get_key_value(keyHolder, sampleInfo.instance_handle);
                                log.debug("Numeric NOT ALIVE:" + keyHolder.unique_device_identifier + " " + keyHolder.metric_id + " " + keyHolder.instance_id);
                                removeNumeric(keyHolder.unique_device_identifier, keyHolder.metric_id, keyHolder.instance_id);
                            } else {
                                if (sampleInfo.valid_data) {
                                    Numeric n = (Numeric) num_seq.get(i);
                                    updateNumeric(n, sampleInfo);
                                } else {
                                    Numeric n = new Numeric();
                                    numericReader.get_key_value(n, sampleInfo.instance_handle);
                                    log.warn("Numeric ALIVE (WITH NO VALID DATA):" + n.unique_device_identifier + " " + n.metric_id + " " + n.instance_id);
                                }
                            }
                        }
                    } finally {
                        numericReader.return_loan(num_seq, info_seq);
                    }
                }
            } catch (RETCODE_NO_DATA noData) {

            } finally {

            }
        }
    };

    protected void removeNumeric(final String udi, final String metric_id, final int instance_id) {
        Platform.runLater(new Runnable() {
            public void run() {
                for (Vital v : vitals) {
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
        });
    }

    private ice.GlobalAlarmSettingsObjectiveDataWriter writer;
    private Topic globalAlarmSettingsTopic;

    public ice.GlobalAlarmSettingsObjectiveDataWriter getWriter() {
        return writer;
    }

    public DeviceListModel getDeviceListModel() {
        return deviceListModel;
    }
    
    protected void updateNumeric(final Numeric _n, final SampleInfo _si) {
        final Numeric n = new Numeric(_n);
        final SampleInfo si = new SampleInfo();
        si.copy_from(_si);
//        final Device device = deviceListModel.getByUniqueDeviceIdentifier(n.unique_device_identifier);
        Platform.runLater(new Runnable() {
            public void run() {
                // TODO linear search? Query Condition should be vital specific
                // or maybe these should be hashed because creating myriad
                // QueryConditions is not advisable
                for (Vital v : vitals) {
                    if (v != null) {
                        for (String x : v.getMetricIds()) {
                            // Change to this vital from a source
                            if (x.equals(n.metric_id)) {
                                boolean updated = false;
                                for (Value va : v) {
                                    if (va.getInstanceId() == n.instance_id && va.getMetricId().equals(n.metric_id)
                                            && va.getUniqueDeviceIdentifier().equals(n.unique_device_identifier)) {
                                        va.updateFrom(n, si);
                                        updated = true;
                                        break;
                                    }
                                }
                                if (!updated) {
                                    final Value va = new ValueImpl(n.unique_device_identifier, n.metric_id, n.instance_id, v);
                                    va.updateFrom(n, si);
                                    v.add(va);
                                    
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public Vital addVital(String label, String units, String[] names, Double low, Double high, Double criticalLow, Double criticalHigh, double minimum,
            double maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color) {
        final Vital v = new VitalImpl(this, label, units, names, low, high, criticalLow, criticalHigh, minimum, maximum, valueMsWarningLow,
                valueMsWarningHigh, color);
        Platform.runLater(new Runnable() {
            public void run() {
                add(v);
            }
        });
        
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
    public void start(final Subscriber subscriber, final Publisher publisher, final EventLoop eventLoop) {
        eventLoop.doLater(new Runnable() {
            public void run() {
                VitalModelImpl.this.subscriber = subscriber;
                VitalModelImpl.this.publisher = publisher;
                VitalModelImpl.this.eventLoop = eventLoop;
                DomainParticipant participant = subscriber.get_participant();

                ice.GlobalAlarmSettingsObjectiveTypeSupport.register_type(participant, ice.GlobalAlarmSettingsObjectiveTypeSupport.get_type_name());

                globalAlarmSettingsTopic = participant.create_topic(ice.GlobalAlarmSettingsObjectiveTopic.VALUE,
                        ice.GlobalAlarmSettingsObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null,
                        StatusKind.STATUS_MASK_NONE);
                writer = (ice.GlobalAlarmSettingsObjectiveDataWriter) publisher.create_datawriter_with_profile(globalAlarmSettingsTopic, QosProfiles.ice_library,
                        QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

                NumericTypeSupport.register_type(participant, NumericTypeSupport.get_type_name());
                TopicDescription nTopic = TopicUtil.lookupOrCreateTopic(participant, NumericTopic.VALUE, NumericTypeSupport.class);
                numericReader = (NumericDataReader) subscriber.create_datareader_with_profile(nTopic, QosProfiles.ice_library,
                        QosProfiles.numeric_data, null, StatusKind.STATUS_MASK_NONE);

                

                eventLoop.addHandler(numericReader.get_statuscondition(), numericHandler);
                numericReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
            }
        });
    }

    @Override
    public void stop() {
        if(eventLoop != null) {
            eventLoop.doLater(new Runnable() {
                public void run() {
                    while (!vitals.isEmpty()) {
                        remove(0);
                    }
                    eventLoop.removeHandler(numericReader.get_statuscondition());
                    publisher.delete_datawriter(writer);
                    subscriber.get_participant().delete_topic(globalAlarmSettingsTopic);
                    numericReader.delete_contained_entities();
                    subscriber.delete_datareader(numericReader);
    
                    VitalModelImpl.this.subscriber = null;
                    VitalModelImpl.this.eventLoop = null;
                }
            });

        }
    }

    public static void main(String[] args) {
        DomainParticipant p = DomainParticipantFactory.get_instance().create_participant(0, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null,
                StatusKind.STATUS_MASK_NONE);
        Subscriber s = p.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        Publisher pub = p.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        VitalModel vm = new VitalModelImpl(null);
        vm.addListener(new ListChangeListener<Vital>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Vital> c) {
                while(c.next()) {
                    if(c.wasPermutated()) {
                        
                    }
                    if(c.wasUpdated()) {
                        
                    }
                    if(c.wasRemoved()) {
                        
                    }
                    if(c.wasAdded()) {
                        
                    }
                }
            }
        });
//        vm.addListener(new VitalModelListener() {
//
//            @Override
//            public void vitalRemoved(VitalModel model, Vital vital) {
//                System.out.println("Removed:" + vital);
//            }
//
//            @Override
//            public void vitalChanged(VitalModel model, Vital vital) {
//                System.out.println(new Date() + " Changed:" + vital);
//            }
//
//            @Override
//            public void vitalAdded(VitalModel model, Vital vital) {
//                System.out.println("Added:" + vital);
//            }
//        });
        // vm.addVital("Heart Rate", "bpm", new int[] {
        // ice.MDC_PULS_OXIM_PULS_RATE.VALUE }, 20, 200, 10, 210, 0, 200);
        EventLoop eventLoop = new EventLoop();

        new EventLoopHandler(eventLoop);

        vm.start(s, pub, eventLoop);
        
    }

    private static final String DEFAULT_INTERLOCK_TEXT = "Drug: Morphine\r\nRate: 4cc / hour";

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

    // TODO I synchronized this because I saw a transient concurrent mod
    // exception
    // but it's unclear to me which thread other than the EventLoopHandler
    // should be calling it
    // am I mixing AWT and ELH calls?
    private final synchronized void updateState() {
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
            stopInfusion("Pump Stopped\r\n" + warningText + "\r\nnurse alerted");
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
        elementObserver.attachListener(element);
        vitals.add(index, element);
    }

    @Override
    protected Vital doSet(int index, Vital element) {
        Vital removed =  vitals.set(index, element);
        elementObserver.detachListener(removed);
        elementObserver.attachListener(element);
        return removed;
    }

    @Override
    protected Vital doRemove(int index) {
        Vital v = vitals.remove(index);
        elementObserver.detachListener(v);
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
}
