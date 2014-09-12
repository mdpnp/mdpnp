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

import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceIcon;
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
public class VitalModelImpl implements VitalModel {

    private final List<Vital> vitals = Collections.synchronizedList(new ArrayList<Vital>());

    private VitalModelListener[] listeners = new VitalModelListener[0];

    protected NumericDataReader numericReader;

    protected Subscriber subscriber;
    protected Publisher publisher;
    protected EventLoop eventLoop;
    private State state = State.Normal;

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
                        for (int i = 0; i < info_seq.size(); i++) {
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

    protected void removeNumeric(String udi, String metric_id, int instance_id) {
        Vital[] vitals = vitalBuffer.get();
        vitals = this.vitals.toArray(vitals);
        vitalBuffer.set(vitals);
        for (Vital v : vitals) {
            boolean updated = false;
            if (v != null) {
                for (String x : v.getMetricIds()) {
                    if (x.equals(metric_id)) {
                        ListIterator<Value> li = v.getValues().listIterator();
                        while (li.hasNext()) {
                            Value va = li.next();
                            if (va.getUniqueDeviceIdentifier().equals(udi) && va.getInstanceId() == instance_id) {
                                li.remove();
                                updated = true;
                            }
                        }
                    }
                }
                if (updated) {
                    fireVitalChanged(v);
                }
            }
        }
    }

    ThreadLocal<Vital[]> vitalBuffer = new ThreadLocal<Vital[]>() {
        @Override
        protected Vital[] initialValue() {
            return new Vital[0];
        }
    };

    private ice.GlobalAlarmSettingsObjectiveDataWriter writer;
    private Topic globalAlarmSettingsTopic;

    public ice.GlobalAlarmSettingsObjectiveDataWriter getWriter() {
        return writer;
    }

    protected void updateNumeric(Numeric n, SampleInfo si) {
        Vital[] vitals = vitalBuffer.get();
        vitals = this.vitals.toArray(vitals);
        vitalBuffer.set(vitals);
        // TODO linear search? Query Condition should be vital specific
        // or maybe these should be hashed because creating myriad
        // QueryConditions is not advisable
        for (Vital v : vitals) {
            if (v != null) {
                for (String x : v.getMetricIds()) {
                    // Change to this vital from a source
                    if (x.equals(n.metric_id)) {
                        boolean updated = false;
                        for (Value va : v.getValues()) {
                            if (va.getInstanceId() == n.instance_id && va.getMetricId().equals(n.metric_id)
                                    && va.getUniqueDeviceIdentifier().equals(n.unique_device_identifier)) {
                                va.updateFrom(n, si);
                                updated = true;
                            }
                        }
                        if (!updated) {
                            Value va = new ValueImpl(n.unique_device_identifier, n.metric_id, n.instance_id, v);
                            va.updateFrom(n, si);
                            v.getValues().add(va);
                        }
                        fireVitalChanged(v);
                    }
                }
            }
        }
    }

    @Override
    public int getCount() {
        return vitals.size();
    }

    @Override
    public Vital getVital(int i) {
        return vitals.get(i);
    }

    @Override
    public Vital addVital(String label, String units, String[] names, Float low, Float high, Float criticalLow, Float criticalHigh, float minimum,
            float maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color) {
        Vital v = new VitalImpl(this, label, units, names, low, high, criticalLow, criticalHigh, minimum, maximum, valueMsWarningLow,
                valueMsWarningHigh, color);
        vitals.add(v);
//        addQueryConditions(v);
        fireVitalAdded(v);
        return v;
    }

    @Override
    public boolean removeVital(Vital vital) {
        boolean r = vitals.remove(vital);
        if (r) {
//            removeQueryConditions(vital);

            ListIterator<Value> li = vital.getValues().listIterator();
            while (li.hasNext()) {
                Value v = li.next();
                li.remove();
            }
            vital.destroy();
            fireVitalRemoved(vital);
        }
        return r;
    }

    @Override
    public Vital removeVital(int i) {
        Vital v = vitals.remove(i);
        if (v != null) {
//            removeQueryConditions(v);

            ListIterator<Value> li = v.getValues().listIterator();
            while (li.hasNext()) {
                Value va = li.next();
                li.remove();
            }
            v.destroy();
            fireVitalRemoved(v);
        }
        return v;
    }

    @Override
    public synchronized void addListener(VitalModelListener vitalModelListener) {
        VitalModelListener[] oldListeners = this.listeners;
        VitalModelListener[] newListeners = new VitalModelListener[oldListeners.length + 1];
        System.arraycopy(oldListeners, 0, newListeners, 0, oldListeners.length);
        newListeners[newListeners.length - 1] = vitalModelListener;
        this.listeners = newListeners;
    }

    @Override
    public synchronized boolean removeListener(VitalModelListener vitalModelListener) {
        VitalModelListener[] oldListeners = this.listeners;
        List<VitalModelListener> newListeners = new ArrayList<VitalModelListener>();
        boolean found = false;
        for (VitalModelListener vml : oldListeners) {
            if (vitalModelListener.equals(vml)) {
                found = true;
            } else {
                newListeners.add(vml);
            }
        }

        this.listeners = newListeners.toArray(new VitalModelListener[0]);
        return found;
    }

    protected void fireVitalAdded(Vital v) {
        updateState();
        VitalModelListener[] listeners = this.listeners;
        for (VitalModelListener vml : listeners) {
            vml.vitalAdded(this, v);
        }
    }

    protected void fireVitalRemoved(Vital v) {
        updateState();
        VitalModelListener[] listeners = this.listeners;
        for (VitalModelListener vml : listeners) {
            vml.vitalRemoved(this, v);
        }
    }

    protected void fireVitalChanged(Vital v) {
        updateState();
        VitalModelListener[] listeners = this.listeners;
        for (VitalModelListener vml : listeners) {
            vml.vitalChanged(this, v);
        }
    }

    public Device getDevice(String udi) {
        if (null == udi) {
            return null;
        }
        DeviceListModel deviceListModel = this.deviceListModel;
        return null == deviceListModel ? null : deviceListModel.getByUniqueDeviceIdentifier(udi);
    }

    public DeviceIcon getDeviceIcon(String udi) {
        Device device = getDevice(udi);
        return null == device ? null : device.getIcon();
    }

    @Override
    public DeviceIdentity getDeviceIdentity(String udi) {
        Device device = getDevice(udi);
        return null == device ? null : device.getDeviceIdentity();
    }

    @Override
    public DeviceConnectivity getDeviceConnectivity(String udi) {
        Device device = getDevice(udi);
        return null == device ? null : device.getDeviceConnectivity();
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
                        removeVital(0);
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

        vm.addListener(new VitalModelListener() {

            @Override
            public void vitalRemoved(VitalModel model, Vital vital) {
                System.out.println("Removed:" + vital);
            }

            @Override
            public void vitalChanged(VitalModel model, Vital vital) {
                System.out.println(new Date() + " Changed:" + vital);
            }

            @Override
            public void vitalAdded(VitalModel model, Vital vital) {
                System.out.println("Added:" + vital);
            }
        });
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
    private String warningText = "", interlockText = DEFAULT_INTERLOCK_TEXT;
    private boolean interlock = false;

    // TODO I synchronized this because I saw a transient concurrent mod
    // exception
    // but it's unclear to me which thread other than the EventLoopHandler
    // should be calling it
    // am I mixing AWT and ELH calls?
    private final synchronized void updateState() {
        int N = getCount();

        while (advisories.length < N) {
            advisories = new String[2 * N + 1];
        }
        now.setTime(System.currentTimeMillis());
        String time = timeFormat.format(now);

        int countWarnings = 0;

        for (int i = 0; i < N; i++) {
            Vital vital = getVital(i);
            advisories[i] = null;
            if (vital.isNoValueWarning() && vital.getValues().isEmpty()) {
                countWarnings++;
                advisories[i] = "- no source of " + vital.getLabel() + "\r\n";
            } else {
                for (Value val : vital.getValues()) {
                    if (val.isAtOrBelowLow()) {
                        countWarnings++;
                        advisories[i] = "- low " + vital.getLabel() + " " + val.getNumeric().value + " " + vital.getUnits() + "\r\n";
                    }
                    if (val.isAtOrAboveHigh()) {
                        countWarnings++;
                        advisories[i] = "- high " + vital.getLabel() + " " + val.getNumeric().value + " " + vital.getUnits() + "\r\n";
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
            warningText = warningTextBuilder.toString();
            state = State.Warning;
        } else {
            warningText = "";
            state = State.Normal;
        }

        if (countWarnings >= countWarningsBecomeAlarm) {
            state = State.Alarm;
            stopInfusion("Pump Stopped\r\n" + warningText + "\r\nnurse alerted");
        } else {
            for (int i = 0; i < N; i++) {
                Vital vital = getVital(i);
                for (Value val : vital.getValues()) {
                    if (val.isAtOrBelowCriticalLow()) {
                        state = State.Alarm;
                        stopInfusion("Pump Stopped\r\n- low " + vital.getLabel() + " " + val.getNumeric().value + " " + vital.getUnits() + "\r\nat "
                                + time + "\r\nnurse alerted");
                        break;
                    } else if (val.isAtOrAboveCriticalHigh()) {
                        state = State.Alarm;
                        stopInfusion("Pump Stopped\r\n- high " + vital.getLabel() + " " + +val.getNumeric().value + " " + vital.getUnits()
                                + "\r\nat " + time + "\r\nnurse alerted");
                        break;
                    }
                }
            }
        }

    }

    private final void stopInfusion(String str) {
        if (!interlock) {
            interlock = true;
            interlockText = str;
        }
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public String getInterlockText() {
        return interlockText;
    }

    @Override
    public String getWarningText() {
        return warningText;
    }

    @Override
    public void resetInfusion() {
        interlock = false;
        interlockText = DEFAULT_INTERLOCK_TEXT;
        fireVitalChanged(null);
    }

    @Override
    public boolean isInfusionStopped() {
        return interlock;
    }

    private int countWarningsBecomeAlarm = 2;

    @Override
    public void setCountWarningsBecomeAlarm(int countWarningsBecomeAlarm) {
        this.countWarningsBecomeAlarm = countWarningsBecomeAlarm;
        fireVitalChanged(null);
    }

    @Override
    public int getCountWarningsBecomeAlarm() {
        return countWarningsBecomeAlarm;
    }

    final DeviceListModel deviceListModel;

    public VitalModelImpl(DeviceListModel deviceListModel) {
        this.deviceListModel = deviceListModel;
    }
}
