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
package org.mdpnp.devices;

import ice.Alert;
import ice.DeviceIdentity;
import ice.DeviceIdentityDataWriter;
import ice.DeviceIdentityTypeSupport;
import ice.LocalAlarmLimitObjectiveDataWriter;
import ice.Numeric;
import ice.NumericDataWriter;
import ice.NumericTypeSupport;
import ice.SampleArray;
import ice.SampleArrayDataWriter;
import ice.SampleArrayTypeSupport;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.management.JMException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.EventLoop.ConditionHandler;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicDescription;

public abstract class AbstractDevice implements ThreadFactory, AbstractDeviceMBean {
    protected final ThreadGroup threadGroup;
    protected final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(this);
    protected final EventLoop eventLoop;

    private static final Logger log = LoggerFactory.getLogger(AbstractDevice.class);

    protected final DomainParticipant domainParticipant;
    protected final Publisher publisher;
    protected final Subscriber subscriber;
    protected TimeManager timeManager;
    protected final Topic deviceIdentityTopic;
    private final DeviceIdentityDataWriter deviceIdentityWriter;
    protected final DeviceIdentity deviceIdentity;
    private InstanceHandle_t deviceIdentityHandle;

    private PartitionAssignmentController partitionAssignmentController;

    protected final Topic numericTopic;
    protected final NumericDataWriter numericDataWriter;

    protected final Topic sampleArrayTopic;
    protected final SampleArrayDataWriter sampleArrayDataWriter;

    private final DeviceClock timestampFactory;

    protected final Topic alarmLimitTopic;
    protected final ice.AlarmLimitDataWriter alarmLimitDataWriter;

    protected final Topic alarmLimitObjectiveTopic;
    protected final ice.LocalAlarmLimitObjectiveDataWriter alarmLimitObjectiveWriter;
    
    protected Topic deviceAlertConditionTopic;
    protected ice.DeviceAlertConditionDataWriter deviceAlertConditionWriter;
    
    protected Topic patientAlertTopic;
    protected ice.AlertDataWriter patientAlertWriter;
    
    protected Topic technicalAlertTopic;
    protected ice.AlertDataWriter technicalAlertWriter;

    protected ice.GlobalAlarmLimitObjectiveDataReader alarmLimitObjectiveReader;
    protected ReadCondition alarmLimitObjectiveCondition;
    
    protected InstanceHolder<ice.DeviceAlertCondition> deviceAlertConditionInstance;


    public Subscriber getSubscriber() {
        return subscriber;
    }

    public static class InstanceHolder<T> {
        public T data;
        public InstanceHandle_t handle;

        public InstanceHolder() {

        }

        public InstanceHolder(T t, InstanceHandle_t handle) {
            this.data = t;
            this.handle = handle;
        }

        @Override
        public String toString() {
            return "[data=" + data + ",handle=" + handle + "]";
        }
    }


    public DeviceIdentity getDeviceIdentity() {
        return deviceIdentity;
    }

    public DomainParticipant getParticipant() {
        return domainParticipant;
    }

    protected InstanceHolder<Numeric> createNumericInstance(String metric_id, String vendor_metric_id) {
        return createNumericInstance(metric_id, vendor_metric_id, 0);
    }
    
    protected InstanceHolder<Numeric> createNumericInstance(String metric_id, String vendor_metric_id, int instance_id) {
        return createNumericInstance(metric_id, vendor_metric_id, instance_id, rosetta.MDC_DIM_DIMLESS.VALUE);
    }

    protected InstanceHolder<Numeric> createNumericInstance(String metric_id, String vendor_metric_id, int instance_id, String unit_id) {
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("Please populate deviceIdentity.unique_device_identifier before calling createNumericInstance");
        }

        InstanceHolder<Numeric> holder = new InstanceHolder<Numeric>();
        holder.data = new Numeric();
        holder.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
        holder.data.metric_id = metric_id;
        holder.data.vendor_metric_id = vendor_metric_id;
        holder.data.instance_id = instance_id;
        holder.data.unit_id = unit_id;
        holder.handle = numericDataWriter.register_instance(holder.data);

        registeredNumericInstances.add(holder);
        return holder;
    }

    protected InstanceHolder<ice.AlarmLimit> createAlarmLimitInstance(String metric_id, ice.LimitType limit_type ) {
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("Please populate deviceIdentity.unique_device_identifier before calling createAlarmInstance");
        }

        InstanceHolder<ice.AlarmLimit> holder = new InstanceHolder<ice.AlarmLimit>();
        holder.data = new ice.AlarmLimit();
        holder.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
        holder.data.metric_id = metric_id;
        holder.data.limit_type = limit_type;
        holder.handle = alarmLimitDataWriter.register_instance(holder.data);
        registeredAlarmLimitInstances.add(holder);
        return holder;
    }

    //XXX Diego: should I use ice.LimitType or String?
    protected InstanceHolder<ice.LocalAlarmLimitObjective> createAlarmLimitObjectiveInstance(String metric_id, ice.LimitType limit_type) {
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("Please populate deviceIdentity.unique_device_identifier before calling createAlarmInstance");
        }

        InstanceHolder<ice.LocalAlarmLimitObjective> holder = new InstanceHolder<ice.LocalAlarmLimitObjective>();
        holder.data = new ice.LocalAlarmLimitObjective();
        holder.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
        holder.data.metric_id = metric_id;
        holder.handle = alarmLimitObjectiveWriter.register_instance(holder.data);
        registeredAlarmLimitObjectiveInstances.add(holder);
        return holder;
    }

    protected void unregisterAllInstances() {
        unregisterAllNumericInstances();
        unregisterAllSampleArrayInstances();
        unregisterAllAlarmLimitInstances();
        unregisterAllAlarmLimitObjectiveInstances();
        unregisterAllPatientAlertInstances();
        unregisterAllTechnicalAlertInstances();
    }
    
    private void unregisterAllAlertInstances(Set<String> old, Map<String, InstanceHolder<ice.Alert>> map, ice.AlertDataWriter writer) {
        for(String key : map.keySet().toArray(new String[0])) {
            writeAlert(old, map, writer, key, null);
        }
    }
    protected void unregisterAllPatientAlertInstances() {
        unregisterAllAlertInstances(oldPatientAlertInstances, patientAlertInstances, patientAlertWriter);
    }
    
    protected void unregisterAllTechnicalAlertInstances() {
        unregisterAllAlertInstances(oldTechnicalAlertInstances, technicalAlertInstances, technicalAlertWriter);
    }

    protected void unregisterAllAlarmLimitObjectiveInstances() {
        while (!registeredAlarmLimitObjectiveInstances.isEmpty()) {
            unregisterAlarmLimitObjectiveInstance(registeredAlarmLimitObjectiveInstances.get(0));
        }
    }

    protected void unregisterAllAlarmLimitInstances() {
        while (!registeredAlarmLimitInstances.isEmpty()) {
            unregisterAlarmLimitInstance(registeredAlarmLimitInstances.get(0));
        }
    }

    protected void unregisterAllNumericInstances() {
        while (!registeredNumericInstances.isEmpty()) {
            unregisterNumericInstance(registeredNumericInstances.get(0));
        }
    }

    protected void unregisterAllSampleArrayInstances() {
        while (!registeredSampleArrayInstances.isEmpty()) {
            unregisterSampleArrayInstance(registeredSampleArrayInstances.get(0));
        }
    }

    protected void unregisterNumericInstance(InstanceHolder<Numeric> holder) {
        if (null != holder) {
            registeredNumericInstances.remove(holder);
            numericDataWriter.unregister_instance(holder.data, holder.handle);
        }
    }

    protected void unregisterSampleArrayInstance(InstanceHolder<SampleArray> holder) {

        registeredSampleArrayInstances.remove(holder);

        sampleArrayDataWriter.unregister_instance(holder.data, holder.handle);
    }

    protected void unregisterAlarmLimitInstance(InstanceHolder<ice.AlarmLimit> holder) {
        registeredAlarmLimitInstances.remove(holder);
        alarmLimitDataWriter.unregister_instance(holder.data, holder.handle);
    }

    protected void unregisterAlarmLimitObjectiveInstance(InstanceHolder<ice.LocalAlarmLimitObjective> holder) {
        registeredAlarmLimitObjectiveInstances.remove(holder);
        alarmLimitObjectiveWriter.unregister_instance(holder.data, holder.handle);
    }

    private final List<InstanceHolder<SampleArray>> registeredSampleArrayInstances = new ArrayList<InstanceHolder<SampleArray>>();
    private final List<InstanceHolder<Numeric>> registeredNumericInstances = new ArrayList<InstanceHolder<Numeric>>();
    private final List<InstanceHolder<ice.AlarmLimit>> registeredAlarmLimitInstances = new ArrayList<InstanceHolder<ice.AlarmLimit>>();
    private final List<InstanceHolder<ice.LocalAlarmLimitObjective>> registeredAlarmLimitObjectiveInstances = new ArrayList<InstanceHolder<ice.LocalAlarmLimitObjective>>();
   private final Map<String, InstanceHolder<ice.Alert>> patientAlertInstances = new HashMap<String, InstanceHolder<ice.Alert>>();
    private final Map<String, InstanceHolder<ice.Alert>> technicalAlertInstances = new HashMap<String, InstanceHolder<ice.Alert>>();
    private final Set<String> oldPatientAlertInstances = new HashSet<String>();
    private final Set<String> oldTechnicalAlertInstances = new HashSet<String>();


    protected InstanceHolder<SampleArray> createSampleArrayInstance(String metric_id, String vendor_metric_id, int instance_id, String unit_id, int frequency) {
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("Please populate deviceIdentity.unique_device_identifier before calling createSampleArrayInstance");
        }

        InstanceHolder<SampleArray> holder = new InstanceHolder<SampleArray>();
        holder.data = new SampleArray();
        holder.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
        holder.data.metric_id = metric_id;
        holder.data.vendor_metric_id = vendor_metric_id;
        holder.data.instance_id = instance_id;
        holder.data.unit_id = unit_id;
        holder.data.frequency = frequency;

        holder.handle = sampleArrayDataWriter.register_instance(holder.data);

        if(holder.handle.is_nil()) {
            log.warn("Unable to register instance " + holder.data);
            holder.handle = null;
        } else {
            registeredSampleArrayInstances.add(holder);
        }
        return holder;
    }

    protected void numericSample(InstanceHolder<Numeric> holder, float newValue, DeviceClock.Reading time) {
        holder.data.value = newValue;
        if(time.hasDeviceTime()) {
            Time_t t = DomainClock.toDDSTime(time.getDeviceTime());
            holder.data.device_time.sec = t.sec;
            holder.data.device_time.nanosec = t.nanosec;
        } else {
            holder.data.device_time.sec = 0;
            holder.data.device_time.nanosec = 0;
        }
        
        Time_t t = DomainClock.toDDSTime(time.getTime());
        holder.data.presentation_time.sec = t.sec;
        holder.data.presentation_time.nanosec = t.nanosec;
        
        numericDataWriter.write(holder.data, holder.handle);
    }

    protected void alarmLimitSample(InstanceHolder<ice.AlarmLimit> holder, String unit_id, Float newValue) {
    	//XXX Should we avoid the comparison w/ the unitID for now?...
        if(0 != Float.compare(newValue, holder.data.value)  /*||  !unit_id.equals(holder.data.unit_identifier) */) {
            holder.data.value = newValue;
            holder.data.unit_identifier = unit_id;
            alarmLimitDataWriter.write(holder.data, holder.handle);
        }
    }

    protected void alarmLimitObjectiveSample(InstanceHolder<ice.LocalAlarmLimitObjective> holder, String unit_id, Float newValue) {
        if(0 != Float.compare(newValue, holder.data.value) ||  !unit_id.equals(holder.data.unit_identifier)) {
            holder.data.value = newValue;
            holder.data.unit_identifier = unit_id;
            alarmLimitObjectiveWriter.write(holder.data, holder.handle);
        }
    }

    protected InstanceHolder<ice.AlarmLimit> alarmLimitSample(InstanceHolder<ice.AlarmLimit> holder, String unit_id, Float newValue,
            String metric_id, ice.LimitType limit_type) {
    	// this should check all key values
        if (holder != null && (!holder.data.metric_id.equals(metric_id) || !holder.data.limit_type.equals(limit_type))) {
            unregisterAlarmLimitInstance(holder);
            holder = null;
        }
        if (null == holder) {
            holder = createAlarmLimitInstance(metric_id, limit_type);
        }
        alarmLimitSample(holder, unit_id, newValue);

        return holder;
    }

    protected InstanceHolder<ice.LocalAlarmLimitObjective> alarmLimitObjectiveSample(InstanceHolder<ice.LocalAlarmLimitObjective> holder,
            Float newValue, String unit_id, String metric_id, ice.LimitType limit_type) {
        if (holder != null && !holder.data.metric_id.equals(metric_id)) {//XXX correct comparison here? should compare w/ limit type too, the other key
            unregisterAlarmLimitObjectiveInstance(holder);
            holder = null;
        }
        if (null != newValue) {
            if (null == holder) {
                holder = createAlarmLimitObjectiveInstance(metric_id, limit_type);
            }
            alarmLimitObjectiveSample(holder, newValue, unit_id, metric_id, limit_type);
        } else {
            if (null != holder) {
                unregisterAlarmLimitObjectiveInstance(holder);
                holder = null;
            }

        }

        return holder;
    }

    
    // For convenience
    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Integer newValue, String metric_id, String vendor_metric_id, String unit_id, DeviceClock.Reading time) {
        return numericSample(holder, null == newValue ? null : newValue.floatValue(), metric_id, vendor_metric_id, unit_id, time);
    }

    // For convenience
    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Integer newValue, String metric_id, String vendor_metric_id, int instance_id, DeviceClock.Reading time) {
        return numericSample(holder, null == newValue ? null : newValue.floatValue(), metric_id, vendor_metric_id, instance_id, rosetta.MDC_DIM_DIMLESS.VALUE, time);
    }
    
    // For convenience
    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Integer newValue, String metric_id, String vendor_metric_id, int instance_id, String unit_id, DeviceClock.Reading time) {
        return numericSample(holder, null == newValue ? null : newValue.floatValue(), metric_id, vendor_metric_id, instance_id, unit_id, time);
    }

    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Float newValue, String metric_id, String vendor_metric_id, String unit_id, DeviceClock.Reading time) {
        return numericSample(holder, newValue, metric_id, vendor_metric_id, 0, unit_id, time);
    }

    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Float newValue, String metric_id, String vendor_metric_id, int instance_id, DeviceClock.Reading time) {
        return numericSample(holder, newValue, metric_id, vendor_metric_id, instance_id, rosetta.MDC_DIM_DIMLESS.VALUE, time);
    }

    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Float newValue, String metric_id, String vendor_metric_id, int instance_id, String unit_id, DeviceClock.Reading time) {

        if (holder != null && (!holder.data.metric_id.equals(metric_id) || !holder.data.vendor_metric_id.equals(vendor_metric_id) || holder.data.instance_id != instance_id || !holder.data.unit_id.equals(unit_id))) {
            unregisterNumericInstance(holder);
            holder = null;
        }
        if (null != newValue) {
            if (null == holder) {
                holder = createNumericInstance(metric_id, vendor_metric_id, instance_id, unit_id);
            }
            numericSample(holder, newValue, time);
        } else {
            if (null != holder) {
                unregisterNumericInstance(holder);
                holder = null;
            }
        }
        return holder;
    }

    protected void writeDeviceAlert(String alertState) {
        alertState = null == alertState ? "" : alertState;
        if(null != deviceAlertConditionInstance) {
            if(!alertState.equals(deviceAlertConditionInstance.data.alert_state)) {
                deviceAlertConditionInstance.data.alert_state = alertState;
                deviceAlertConditionWriter.write(deviceAlertConditionInstance.data, deviceAlertConditionInstance.handle);
            }
        } else {
            throw new IllegalStateException("No deviceAlertCondition; have you called writeDeviceIdentity?");
        }
    }
    
    private void writeAlert(Set<String> old, Map<String, InstanceHolder<ice.Alert> > map, ice.AlertDataWriter writer, String key, String value) {
        InstanceHolder<ice.Alert> alert = map.get(key);
        if(null == value) {
            if(null != alert) {
                writer.unregister_instance(alert.data, alert.handle);
                map.remove(key);
            }
        } else {
            if (null == alert) {
                alert = new InstanceHolder<ice.Alert>();
                alert.data = (Alert) ice.Alert.create();
                alert.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
                alert.data.identifier = key;
                alert.handle = writer.register_instance(alert.data);
                map.put(key, alert);
            }
            old.remove(key);
            if(!value.equals(alert.data.text)) { 
                alert.data.text = value;
                writer.write(alert.data, alert.handle);
            }
        }
    }
    
    protected void markOldPatientAlertInstances() {
        oldPatientAlertInstances.clear();
        oldPatientAlertInstances.addAll(patientAlertInstances.keySet());
    }
    
    protected void markOldTechnicalAlertInstances() {
        oldTechnicalAlertInstances.clear();
        oldTechnicalAlertInstances.addAll(technicalAlertInstances.keySet());
    }
    
    protected void clearOldPatientAlertInstances() {
        for(String key : oldPatientAlertInstances) {
            writePatientAlert(key, null);
        }
    }
    
    protected void clearOldTechnicalAlertInstances() {
        for(String key : oldTechnicalAlertInstances) {
            writeTechnicalAlert(key, null);
        }
    }
    
    protected void writePatientAlert(String key, String value) {
        writeAlert(oldPatientAlertInstances, patientAlertInstances, patientAlertWriter, key, value);
    }
    protected void writeTechnicalAlert(String key, String value) {
        writeAlert(oldTechnicalAlertInstances, technicalAlertInstances, technicalAlertWriter, key, value);
    }


    protected InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder,
                                                            Number[] newValues,
                                                            String metric_id, String vendor_metric_id, String unit_id, int frequency,
                                                            DeviceClock.Reading timestamp) {
        return sampleArraySample(holder, newValues, metric_id, vendor_metric_id, 0, unit_id, frequency, timestamp);
    }


    protected InstanceHolder<ice.SampleArray> sampleArraySample(InstanceHolder<ice.SampleArray> holder,
                                                                Number[] newValues, int len,
                                                                String metric_id, String vendor_metric_id, int instance_id, String unit_id, int frequency,
                                                                DeviceClock.Reading timestamp) {
        return sampleArraySample(holder, new ArrayContainer<>(newValues, len), metric_id, vendor_metric_id, instance_id, unit_id, frequency, timestamp);
    }

    protected InstanceHolder<ice.SampleArray> sampleArraySample(InstanceHolder<ice.SampleArray> holder,
                                                                Number[] newValues,
                                                                String metric_id, String vendor_metric_id, int instance_id, String unit_id, int frequency,
                                                                DeviceClock.Reading timestamp) {
        return sampleArraySample(holder, new ArrayContainer<>(newValues), metric_id, vendor_metric_id, instance_id, unit_id, frequency, timestamp);
    }

    protected InstanceHolder<ice.SampleArray> sampleArraySample(InstanceHolder<ice.SampleArray> holder,
                                                                Collection<Number> newValues,
                                                                String metric_id, String vendor_metric_id, int instance_id, String unit_id, int frequency,
                                                                DeviceClock.Reading timestamp) {
        return sampleArraySample(holder, new CollectionContainer<>(newValues), metric_id, vendor_metric_id, instance_id, unit_id, frequency, timestamp);
    }

    private InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder,
                                                          NullSaveContainer<Number> newValues,
                                                          String metric_id, String vendor_metric_id, int instance_id, String unit_id, int frequency,
                                                          DeviceClock.Reading timestamp) {

        holder = ensureHolderConsistency(holder, metric_id, vendor_metric_id, instance_id, unit_id, frequency);

        if (!newValues.isNull()) {
            // Call this now so that resolution of instance registration timestamp
            // is reduced
            timestamp = timestamp.refineResolutionForFrequency(frequency, newValues.size());
            if (null == holder) {
                holder = createSampleArrayInstance(metric_id, vendor_metric_id, instance_id, unit_id, frequency);
            }
            sampleArraySample(holder, newValues, timestamp);
        } else {
            if (holder != null) {
                unregisterSampleArrayInstance(holder);
                holder = null;
            }
        }
        return holder;
    }

    protected void sampleArraySample(InstanceHolder<SampleArray> holder, Number[] newValues, DeviceClock.Reading timestamp) {
        sampleArraySample(holder, new ArrayContainer<>(newValues), timestamp);
    }

    protected void sampleArraySample(InstanceHolder<SampleArray> holder, Collection<Number>  newValues, DeviceClock.Reading timestamp) {
        sampleArraySample(holder, new CollectionContainer<>(newValues), timestamp);
    }

    private void sampleArraySample(InstanceHolder<ice.SampleArray> holder, NullSaveContainer<Number> newValues, DeviceClock.Reading deviceTimestamp) {
        fill(holder, newValues);
        publish(holder, deviceTimestamp);
    }

    private void fill(InstanceHolder<SampleArray> holder, NullSaveContainer<Number> newValues) {
        holder.data.values.userData.clear();
        if(!newValues.isNull()) {
            Iterator<Number> iter = newValues.iterator();
            while (iter.hasNext()) {
                Number n = iter.next();
                holder.data.values.userData.addFloat(n.floatValue());
            }
        }
    }

    private void publish(InstanceHolder<ice.SampleArray> holder, DeviceClock.Reading deviceTimestamp) {

        if (deviceTimestamp.hasDeviceTime()) {
            Time_t t = DomainClock.toDDSTime(deviceTimestamp.getDeviceTime());
            holder.data.device_time.sec = t.sec;
            holder.data.device_time.nanosec = t.nanosec;
        } else {

            holder.data.device_time.sec = 0;
            holder.data.device_time.nanosec = 0;
        }

        DeviceClock.Reading adjusted = deviceTimestamp.refineResolutionForFrequency(holder.data.frequency,
                                                                                    holder.data.values.userData.size());
        
        DomainClock.toDDSTime(adjusted.getTime(), holder.data.presentation_time);

        sampleArrayDataWriter.write(holder.data,
                                                holder.handle==null?InstanceHandle_t.HANDLE_NIL:holder.handle);
    }

    private InstanceHolder<SampleArray> ensureHolderConsistency(InstanceHolder<SampleArray> holder,
                                                                String metric_id, String vendor_metric_id, int instance_id, 
                                                                String unit_id, int frequency) {

        if (null != holder && (!holder.data.metric_id.equals(metric_id) ||
                               !holder.data.vendor_metric_id.equals(vendor_metric_id) ||
                                holder.data.instance_id != instance_id ||
                                holder.data.frequency != frequency ||
                               !holder.data.unit_id.equals(unit_id))) {

            unregisterSampleArrayInstance(holder);
            holder = null;
        }
        return holder;
    }

    protected String iconResourceName() {
        return null;
    }


    private int threadOrdinal = 0;

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(threadGroup, r, "AbstractDevice-" + (++threadOrdinal));
        t.setDaemon(true);
        return t;
    }

    public void shutdown() {
        // TODO there is nothing preventing inheritors from interacting with
        // these objects as (and after) they are destroyed.
        // TODO there isn't a coherent way to dispose of the instances an
        // inheritor may have registered... perhaps they should be responsible
        // in their override of shutdown?

        if(timeManager!=null) {
            timeManager.stop();
        }

        partitionAssignmentController.shutdown();

        if (null != alarmLimitObjectiveCondition) {
            eventLoop.removeHandler(alarmLimitObjectiveCondition);
            alarmLimitObjectiveReader.delete_readcondition(alarmLimitObjectiveCondition);
            alarmLimitObjectiveCondition = null;
        }

        subscriber.delete_datareader(alarmLimitObjectiveReader);

        publisher.delete_datawriter(alarmLimitObjectiveWriter);
        domainParticipant.delete_topic(alarmLimitObjectiveTopic);
        ice.LocalAlarmLimitObjectiveTypeSupport.unregister_type(domainParticipant, ice.LocalAlarmLimitObjectiveTypeSupport.get_type_name());

        publisher.delete_datawriter(alarmLimitDataWriter);
        domainParticipant.delete_topic(alarmLimitTopic);
        ice.AlarmLimitTypeSupport.unregister_type(domainParticipant, ice.AlarmLimitTypeSupport.get_type_name());

        publisher.delete_datawriter(sampleArrayDataWriter);
        domainParticipant.delete_topic(sampleArrayTopic);
        SampleArrayTypeSupport.unregister_type(domainParticipant, SampleArrayTypeSupport.get_type_name());

        publisher.delete_datawriter(numericDataWriter);
        domainParticipant.delete_topic(numericTopic);
        NumericTypeSupport.unregister_type(domainParticipant, NumericTypeSupport.get_type_name());

        publisher.delete_datawriter(deviceIdentityWriter);
        domainParticipant.delete_topic(deviceIdentityTopic);
        DeviceIdentityTypeSupport.unregister_type(domainParticipant, DeviceIdentityTypeSupport.get_type_name());
        
        publisher.delete_datawriter(deviceAlertConditionWriter);
        domainParticipant.delete_topic(deviceAlertConditionTopic);
        ice.DeviceAlertConditionTypeSupport.unregister_type(domainParticipant, ice.DeviceAlertConditionTypeSupport.get_type_name());

        publisher.delete_datawriter(patientAlertWriter);
        domainParticipant.delete_topic(patientAlertTopic);
        
        publisher.delete_datawriter(technicalAlertWriter);
        domainParticipant.delete_topic(technicalAlertTopic);
        ice.AlertTypeSupport.unregister_type(domainParticipant, ice.AlertTypeSupport.get_type_name());

        domainParticipant.delete_publisher(publisher);
        domainParticipant.delete_subscriber(subscriber);

        domainParticipant.delete_contained_entities();

        DomainParticipantFactory.get_instance().delete_participant(domainParticipant);

        executor.shutdown();
        log.info("AbstractDevice shutdown complete");
    }

    public AbstractDevice(int domainId, EventLoop eventLoop) {

        deviceIdentity  = (new DeviceIdentityBuilder()).osName().softwareRev().withIcon(this, iconResourceName()).build();

        DomainParticipantQos pQos = new DomainParticipantQos();
        DomainParticipantFactory.get_instance().get_default_participant_qos(pQos);
        pQos.participant_name.name = "Device";
        
        domainParticipant = DomainParticipantFactory.get_instance().create_participant(domainId, pQos, null, StatusKind.STATUS_MASK_NONE);
        publisher = domainParticipant.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        subscriber = domainParticipant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        timestampFactory = new DomainClock(domainParticipant);

        partitionAssignmentController = new PartitionAssignmentController(deviceIdentity, eventLoop, publisher, subscriber);
        partitionAssignmentController.start();

        DeviceIdentityTypeSupport.register_type(domainParticipant, DeviceIdentityTypeSupport.get_type_name());
        deviceIdentityTopic = domainParticipant.create_topic(ice.DeviceIdentityTopic.VALUE, DeviceIdentityTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        deviceIdentityWriter = (DeviceIdentityDataWriter) publisher.create_datawriter_with_profile(deviceIdentityTopic, QosProfiles.ice_library,
                QosProfiles.device_identity, null, StatusKind.STATUS_MASK_NONE);
        if (null == deviceIdentityWriter) {
            throw new RuntimeException("deviceIdentityWriter not created");
        }

        NumericTypeSupport.register_type(domainParticipant, NumericTypeSupport.get_type_name());
        numericTopic = domainParticipant.create_topic(ice.NumericTopic.VALUE, NumericTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        numericDataWriter = (NumericDataWriter) publisher.create_datawriter_with_profile(numericTopic, QosProfiles.ice_library,
                QosProfiles.numeric_data, null, StatusKind.STATUS_MASK_NONE);
        if (null == numericDataWriter) {
            throw new RuntimeException("numericDataWriter not created");
        }

        SampleArrayTypeSupport.register_type(domainParticipant, SampleArrayTypeSupport.get_type_name());
        sampleArrayTopic = domainParticipant.create_topic(ice.SampleArrayTopic.VALUE, SampleArrayTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        sampleArrayDataWriter = (SampleArrayDataWriter) publisher.create_datawriter_with_profile(sampleArrayTopic, QosProfiles.ice_library,
                QosProfiles.waveform_data, null, StatusKind.STATUS_MASK_NONE);
        if (null == sampleArrayDataWriter) {
            throw new RuntimeException("sampleArrayDataWriter not created");
        }

        ice.AlarmLimitTypeSupport.register_type(domainParticipant, ice.AlarmLimitTypeSupport.get_type_name());
        alarmLimitTopic = domainParticipant.create_topic(ice.AlarmLimitTopic.VALUE, ice.AlarmLimitTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        alarmLimitDataWriter = (ice.AlarmLimitDataWriter) publisher.create_datawriter_with_profile(alarmLimitTopic, QosProfiles.ice_library,
                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        ice.LocalAlarmLimitObjectiveTypeSupport.register_type(domainParticipant, ice.LocalAlarmLimitObjectiveTypeSupport.get_type_name());
        alarmLimitObjectiveTopic = domainParticipant.create_topic(ice.LocalAlarmLimitObjectiveTopic.VALUE,
                ice.LocalAlarmLimitObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        alarmLimitObjectiveWriter = (LocalAlarmLimitObjectiveDataWriter) publisher.create_datawriter_with_profile(alarmLimitObjectiveTopic,
                QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        TopicDescription alarmLimitObjectiveTopic = TopicUtil.lookupOrCreateTopic(domainParticipant, ice.GlobalAlarmLimitObjectiveTopic.VALUE,
                ice.GlobalAlarmLimitObjectiveTypeSupport.class);
        alarmLimitObjectiveReader = (ice.GlobalAlarmLimitObjectiveDataReader) subscriber.create_datareader_with_profile(
                alarmLimitObjectiveTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
       
        ice.DeviceAlertConditionTypeSupport.register_type(domainParticipant, ice.DeviceAlertConditionTypeSupport.get_type_name());
        deviceAlertConditionTopic = domainParticipant.create_topic(ice.DeviceAlertConditionTopic.VALUE,
                ice.DeviceAlertConditionTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        deviceAlertConditionWriter = (ice.DeviceAlertConditionDataWriter) publisher.create_datawriter_with_profile(deviceAlertConditionTopic,
                QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        ice.AlertTypeSupport.register_type(domainParticipant, ice.AlertTypeSupport.get_type_name());
        patientAlertTopic = domainParticipant.create_topic(ice.PatientAlertTopic.VALUE, ice.AlertTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        patientAlertWriter = (ice.AlertDataWriter) publisher.create_datawriter_with_profile(patientAlertTopic, QosProfiles.ice_library,
                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        
        technicalAlertTopic = domainParticipant.create_topic(ice.TechnicalAlertTopic.VALUE, ice.AlertTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        technicalAlertWriter = (ice.AlertDataWriter) publisher.create_datawriter_with_profile(technicalAlertTopic, QosProfiles.ice_library,
                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        
        
        
        threadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup(), "AbstractDevice") {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error("Thrown by " + t.toString(), e);
                super.uncaughtException(t, e);
            }
        };

        threadGroup.setDaemon(true);
        this.eventLoop = eventLoop;
        
        executor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                partitionAssignmentController.checkForPartitionFile();
            }
        }, 1000L, 5000L, TimeUnit.MILLISECONDS);
    }


    /**
     * @return an instance of the device clock that should be used in stamping messages. Fall-back implementation
     * will supply dds time. If device maintains its own notion of the clock, it could use DeviceClock.ComboClock wrapper
     * to provide clock reading that would contain multiple values.
     **/

    protected final DeviceClock getClockProvider()
    {
        return timestampFactory;
    }

    public void setAlarmLimit(ice.GlobalAlarmLimitObjective obj) {
    }

    public void unsetAlarmLimit(String metricId) {
    }

    private Map<InstanceHandle_t, String> instanceMetrics = new HashMap<InstanceHandle_t, String>();

    protected void writeDeviceIdentity() {
        if (null == deviceIdentity.unique_device_identifier || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("cannot write deviceIdentity without a UDI");
        }
        registerForManagement();
        
        if(null == timeManager) {
            timeManager = new TimeManager(publisher, subscriber, deviceIdentity.unique_device_identifier, "Device");
            timeManager.start();
        }

        if (null == deviceIdentityHandle) {
            deviceIdentityHandle = deviceIdentityWriter.register_instance(deviceIdentity);
        }
        deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
        
        ice.DeviceAlertCondition alertCondition = (ice.DeviceAlertCondition) ice.DeviceAlertCondition.create();
        alertCondition.unique_device_identifier = deviceIdentity.unique_device_identifier;
        alertCondition.alert_state = "";
        InstanceHandle_t deviceAlertHandle = deviceAlertConditionWriter.register_instance(alertCondition);
        deviceAlertConditionInstance = new InstanceHolder<ice.DeviceAlertCondition>(alertCondition, deviceAlertHandle);

        if (null == alarmLimitObjectiveCondition) {
            final SampleInfoSeq info_seq = new SampleInfoSeq();
            final ice.GlobalAlarmLimitObjectiveSeq data_seq = new ice.GlobalAlarmLimitObjectiveSeq();
            eventLoop.addHandler(
            		alarmLimitObjectiveCondition = alarmLimitObjectiveReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
                            ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE), new ConditionHandler() {
                        @Override
                        public void conditionChanged(Condition condition) {
                            try {
                            	alarmLimitObjectiveReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                        (ReadCondition) condition);
                                for (int i = 0; i < data_seq.size(); i++) {
                                    SampleInfo si = (SampleInfo) info_seq.get(i);
                                    ice.GlobalAlarmLimitObjective obj = (ice.GlobalAlarmLimitObjective) data_seq.get(i);

                                    if (0 != (si.view_state & ViewStateKind.NEW_VIEW_STATE) && si.valid_data) {
                                        log.debug("Handle for metric_id=" + obj.metric_id + " is " + si.instance_handle);
                                        instanceMetrics.put(new InstanceHandle_t(si.instance_handle), obj.metric_id);
                                    }

                                    if (0 != (si.instance_state & InstanceStateKind.ALIVE_INSTANCE_STATE)) {
                                        if (si.valid_data) {
                                            log.warn("Limit " + obj.metric_id + " "+obj.limit_type+" limit changed to [ " + obj.value + "  " + obj.unit_identifier + "]");
                                            setAlarmLimit(obj);
                                        }
                                    } else {
                                        obj = new ice.GlobalAlarmLimitObjective();
                                        log.warn("Unsetting handle " + si.instance_handle);
                                        // TODO 1-Oct-2013 JP This call to
                                        // get_key_value fails consistently on
                                        // ARM platforms
                                        // so I'm tracking instances externally
                                        // for the time being
                                        // alarmSettingsObjectiveReader.get_key_value(obj,
                                        // si.instance_handle);
                                        String metricId = instanceMetrics.get(si.instance_handle);
                                        log.debug("Unsetting alarm limit " + metricId);
                                        if (null != metricId) {
                                        	unsetAlarmLimit(metricId);
                                        }

                                    }
                                }
                            } catch (RETCODE_NO_DATA noData) {

                            } finally {
                            	alarmLimitObjectiveReader.return_loan(data_seq, info_seq);
                            }
                        }
                    });
        }
    }
    
    @Override
    public String getManufacturer() {
        return null == deviceIdentity ? null : deviceIdentity.manufacturer;
    }

    @Override
    public String getModel() {
        return null == deviceIdentity ? null : deviceIdentity.model;
    }

    @Override
    public String getUniqueDeviceIdentifier() {
        return null == deviceIdentity ? null : deviceIdentity.unique_device_identifier;
    }

    private ObjectInstance objInstance;

    private void registerForManagement() {
        if (null == objInstance) {
            try {
                objInstance = ManagementFactory.getPlatformMBeanServer().registerMBean(
                        this,
                        new ObjectName(AbstractDevice.class.getPackage().getName() + ":type=" + AbstractDevice.class.getSimpleName() + ",name="
                                + getUniqueDeviceIdentifier()));
            } catch (JMException e) {
                log.warn("Unable to register with JMX", e);
            }
        }
    }

    @Override
    public String[] getPartition() {
        return partitionAssignmentController.getPartition();
    }

    @Override
    public void addPartition(String partition) {
        partitionAssignmentController.addPartition(partition);
    }

    @Override
    public void removePartition(String partition) {
        partitionAssignmentController.removePartition(partition);
    }

    @Override
    public void setPartition(String[] partition) {
        partitionAssignmentController.setPartition(partition);
    }

    protected void iconOrBlank(String model, String icon) {
        (new DeviceIdentityBuilder(deviceIdentity)).withIcon(this, icon).model(model).build();
        writeDeviceIdentity();
    }

    static interface NullSaveContainer<T>
    {
        boolean isNull();
        Iterator<T> iterator();
        int size();
    }

    static class CollectionContainer<T> implements NullSaveContainer<T>
    {
        CollectionContainer(Collection<T> dt) {
            this.dt = dt;
        }

        private final Collection<T> dt;

        public boolean isNull() { return dt == null; }
        public Iterator<T> iterator() { return dt==null? Collections.<T>emptyIterator() : dt.iterator(); }
        public int size() { return dt == null ? 0 : dt.size(); }
    }

    static class ArrayContainer<T> implements NullSaveContainer<T>
    {
        ArrayContainer(T[] dt) {
            this(dt, dt == null ? 0 : dt.length);
        }

        ArrayContainer(T[] dt, int l) {
            this.dt = dt;
            this.l = l;
        }

        private final T[] dt;
        private final int l;

        public boolean isNull() { return dt == null; }
        public Iterator<T> iterator() { return new ArrayIterator<T>(dt, l); }
        public int size() { return l; }
    }

    static class ArrayIterator<T> implements Iterator<T>
    {
        final T[] data;
        final int l;

        int currentIdx=0;

        public ArrayIterator(T[] data, int l) {
            this.data = data;
            this.l = l;
        }

        @Override
        public boolean hasNext() {
            return currentIdx<l;
        }

        @Override
        public T next() {
            return data[currentIdx++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            throw new UnsupportedOperationException();
        }
    }
}
