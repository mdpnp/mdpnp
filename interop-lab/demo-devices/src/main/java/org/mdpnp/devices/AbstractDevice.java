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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.EventLoop.ConditionHandler;
import org.mdpnp.sql.SQLLogging;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.rti.dds.domain.DomainParticipant;
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


import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;

@ManagedResource(description="MDPNP Device Driver")
public abstract class AbstractDevice {

    public static ThreadGroup threadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup(), "DeviceAdapter") {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("Thrown by " + t.toString(), e);
            super.uncaughtException(t, e);
        }
    };
    // TODO this is a good idea but the threadgroup gets destroyed when the last thread finishes
//    static
//    {
//        threadGroup.setDaemon(true);
//    }
    
    protected Connection dbconn;
    protected PreparedStatement numericStatement;
    protected PreparedStatement sampleStatement;
    protected PreparedStatement numericExportStatement;

    protected final EventLoop eventLoop;
    protected ScheduledExecutorService executor;

    private static final Logger log = LoggerFactory.getLogger(AbstractDevice.class);

    protected final DomainParticipant domainParticipant;
    protected final Publisher publisher;
    protected final Subscriber subscriber;
    protected final Topic deviceIdentityTopic;
    private final DeviceIdentityDataWriter deviceIdentityWriter;
    protected final DeviceIdentity deviceIdentity;
    private InstanceHandle_t deviceIdentityHandle;

    protected final Topic numericTopic;
    protected final NumericDataWriter numericDataWriter;

    protected final Topic sampleArrayTopic;
    protected final SampleArrayDataWriter sampleArrayDataWriter;

    private final DeviceClock timestampFactory;

    protected final Topic alarmLimitTopic;
    protected final ice.AlarmLimitDataWriter alarmLimitDataWriter;


    protected final Topic localAlarmLimitObjectiveTopic;
    protected final ice.LocalAlarmLimitObjectiveDataWriter alarmLimitObjectiveWriter;

    
    protected Topic deviceAlertConditionTopic;
    protected ice.DeviceAlertConditionDataWriter deviceAlertConditionWriter;
    
    protected Topic patientAlertTopic;
    protected ice.AlertDataWriter patientAlertWriter;
    
    protected Topic technicalAlertTopic;
    protected ice.AlertDataWriter technicalAlertWriter;

	protected Topic globalAlarmLimitObjectiveTopic;
    protected ice.GlobalAlarmLimitObjectiveDataReader alarmLimitObjectiveReader;
    protected ReadCondition alarmLimitObjectiveCondition;

    
    protected InstanceHolder<ice.DeviceAlertCondition> deviceAlertConditionInstance;
    
    private static final int DEFAULT_AVERAGING_TIME=60 * 1000;
    
    private int averagingTime;
    
    private AveragingThread averagingThread;
    
    private Hashtable<String, Averager> averagesByNumeric=new Hashtable<>();
    
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

    protected InstanceHolder<ice.LocalAlarmLimitObjective> createAlarmLimitObjectiveInstance(String metric_id, ice.LimitType limit_type) {
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("Please populate deviceIdentity.unique_device_identifier before calling createAlarmObjectiveInstance");
        }

        InstanceHolder<ice.LocalAlarmLimitObjective> holder = new InstanceHolder<ice.LocalAlarmLimitObjective>();
        holder.data = new ice.LocalAlarmLimitObjective();
        holder.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
        holder.data.metric_id = metric_id;
        holder.data.limit_type = limit_type;
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

    @SuppressWarnings("cast")
    protected void numericSample(InstanceHolder<Numeric> holder, float newValue, DeviceClock.Reading time) {
        holder.data.value = newValue;
        if(time.hasDeviceTime()) {
            Time_t t = DomainClock.toDDSTime(time.getDeviceTime());
            holder.data.device_time.sec = (int)(t.sec);
            holder.data.device_time.nanosec = (int)t.nanosec;
        } else {
            holder.data.device_time.sec = 0;
            holder.data.device_time.nanosec = 0;
        }
        
        Time_t t = DomainClock.toDDSTime(time.getTime());
        holder.data.presentation_time.sec = (int)(t.sec);
        holder.data.presentation_time.nanosec = (int)(t.nanosec);
        
        numericDataWriter.write(holder.data, holder.handle);
/*
        if(numericStatement!=null) {
	        try {
				numericStatement.setInt(1, t.sec);
				numericStatement.setInt(2, t.nanosec);
				numericStatement.setString(3, deviceIdentity.unique_device_identifier);
				numericStatement.setString(4, holder.data.metric_id);
				numericStatement.setFloat(5, newValue);
				boolean resType=numericStatement.execute();
			} catch (SQLException e) {
				//Because this is executing once per second, including the stack trace would
				//cause a very large log file.
				log.warn("Failed to execute numeric statement - "+e.getMessage());
			}
        }
*/
        if(averagesByNumeric.containsKey(holder.data.metric_id)) {
        	averagesByNumeric.get(holder.data.metric_id).add(newValue);
        } else {
        	Averager averager=new Averager();
        	averager.add(newValue);
        	averagesByNumeric.put(holder.data.metric_id, averager);
        }
    }

    protected void alarmLimitSample(InstanceHolder<ice.AlarmLimit> holder, String unit_id, Float newValue) {
        if(0 != Float.compare(newValue, holder.data.value) ||  
           !unit_id.equals(holder.data.unit_identifier)) {
            holder.data.value = newValue;
            holder.data.unit_identifier = unit_id;
            alarmLimitDataWriter.write(holder.data, holder.handle);
        }
    }

    protected void alarmLimitObjectiveSample(InstanceHolder<ice.LocalAlarmLimitObjective> holder, String unit_id, Float newValue) {
        if(0 != Float.compare(newValue, holder.data.value) || 
           !unit_id.equals(holder.data.unit_identifier)) {
            holder.data.value = newValue;
            holder.data.unit_identifier = unit_id;
            alarmLimitObjectiveWriter.write(holder.data, holder.handle);
        }
    }

    protected InstanceHolder<ice.AlarmLimit> alarmLimitSample(InstanceHolder<ice.AlarmLimit> holder, String unit_id, Float newValue,
            String metric_id, ice.LimitType limit_type) {
        if (holder != null && 
                (!holder.data.unique_device_identifier.equals(deviceIdentity.unique_device_identifier) ||
                 !holder.data.metric_id.equals(metric_id) || 
                 !holder.data.limit_type.equals(limit_type))) {
            unregisterAlarmLimitInstance(holder);
            holder = null;
        }
        if(null != newValue) {
            if (null == holder) {
                holder = createAlarmLimitInstance(metric_id, limit_type);
            }
            alarmLimitSample(holder, unit_id, newValue);
        } else {
            if(null != holder) {
                unregisterAlarmLimitInstance(holder);
                holder = null;
            }
        }

        return holder;
    }

    protected InstanceHolder<ice.LocalAlarmLimitObjective> alarmLimitObjectiveSample(InstanceHolder<ice.LocalAlarmLimitObjective> holder,
            Float newValue, String unit_id, String metric_id, ice.LimitType limit_type) {
        if ( holder != null && 
                (!holder.data.unique_device_identifier.equals(deviceIdentity.unique_device_identifier) ||
                 !holder.data.metric_id.equals(metric_id) ||
                 !holder.data.limit_type.equals(limit_type))) {
            unregisterAlarmLimitObjectiveInstance(holder);
            holder = null;
        }
        if (null != newValue) {
            if (null == holder) {
                holder = createAlarmLimitObjectiveInstance(metric_id, limit_type);
            }
            alarmLimitObjectiveSample(holder, unit_id, newValue);
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
            holder.data.device_time.sec = (int)(t.sec);
            holder.data.device_time.nanosec = (int)(t.nanosec);
        } else {

            holder.data.device_time.sec = 0;
            holder.data.device_time.nanosec = 0;
        }

        DeviceClock.Reading adjusted = deviceTimestamp.refineResolutionForFrequency(holder.data.frequency,
                                                                                    holder.data.values.userData.size());
        
        DomainClock.toDDSTime(adjusted.getTime(), holder.data.presentation_time);

        sampleArrayDataWriter.write(holder.data,
                                                holder.handle==null?InstanceHandle_t.HANDLE_NIL:holder.handle);
        //If we look at fill() we can see that it adds floats to the array.
        float[] floatsForDb=holder.data.values.userData.toArrayFloat(new float[0]);
        if(sampleStatement!=null) {
	        try {
				sampleStatement.setInt(1, holder.data.presentation_time.sec);
				sampleStatement.setInt(2, holder.data.presentation_time.nanosec);
				sampleStatement.setString(3, deviceIdentity.unique_device_identifier);
				sampleStatement.setString(4, holder.data.metric_id);
				/*
				 * This is not necessarily the best way of serialising floats - if it turns out to be too big of
				 * a performance hit, we can check out alternatives like 
				 * https://www.factual.com/blog/the-flotsam-project-insanely-fast-floating-point-number-serialization-for-java-and-javascript/
				 * and
				 * https://github.com/RuedigerMoeller/fast-serialization
				 */
				JsonArrayBuilder builder=Json.createArrayBuilder();
				for(int i=0;i<floatsForDb.length;i++) {
					builder.add(floatsForDb[i]);
				}
				JsonArray jsonArray=builder.build();
				sampleStatement.setString(5, jsonArray.toString());
				boolean resType=sampleStatement.execute();
			} catch (SQLException e) {
				//Because this is executing once per second, including the stack trace would
				//cause a very large log file.
				log.warn("Failed to execute sample statement - "+e.getMessage());
			}
        }
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

    public void shutdown() {
        // TODO there is nothing preventing inheritors from interacting with
        // these objects as (and after) they are destroyed.
        // TODO there isn't a coherent way to dispose of the instances an
        // inheritor may have registered... perhaps they should be responsible
        // in their override of shutdown?

		if (null != alarmLimitObjectiveCondition) {
		    eventLoop.removeHandler(alarmLimitObjectiveCondition);
		    alarmLimitObjectiveReader.delete_readcondition(alarmLimitObjectiveCondition);
		    alarmLimitObjectiveCondition = null;
		}

		subscriber.delete_datareader(alarmLimitObjectiveReader);
        domainParticipant.delete_topic(globalAlarmLimitObjectiveTopic);

        publisher.delete_datawriter(alarmLimitObjectiveWriter);
        domainParticipant.delete_topic(localAlarmLimitObjectiveTopic);
        // TODO Where a participant is shared it is not safe to unregister types
//        ice.LocalAlarmLimitObjectiveTypeSupport.unregister_type(domainParticipant, ice.LocalAlarmLimitObjectiveTypeSupport.get_type_name());

        publisher.delete_datawriter(alarmLimitDataWriter);
        domainParticipant.delete_topic(alarmLimitTopic);
        // TODO Where a participant is shared it is not safe to unregister types
//        ice.AlarmLimitTypeSupport.unregister_type(domainParticipant, ice.AlarmLimitTypeSupport.get_type_name());

        publisher.delete_datawriter(sampleArrayDataWriter);
        domainParticipant.delete_topic(sampleArrayTopic);
        // TODO Where a participant is shared it is not safe to unregister types
//        SampleArrayTypeSupport.unregister_type(domainParticipant, SampleArrayTypeSupport.get_type_name());

        publisher.delete_datawriter(numericDataWriter);
        domainParticipant.delete_topic(numericTopic);
        // TODO Where a participant is shared it is not safe to unregister types
//        NumericTypeSupport.unregister_type(domainParticipant, NumericTypeSupport.get_type_name());

        publisher.delete_datawriter(deviceIdentityWriter);
        domainParticipant.delete_topic(deviceIdentityTopic);
        // TODO Where a participant is shared it is not safe to unregister types
//        DeviceIdentityTypeSupport.unregister_type(domainParticipant, DeviceIdentityTypeSupport.get_type_name());
        
        publisher.delete_datawriter(deviceAlertConditionWriter);
        domainParticipant.delete_topic(deviceAlertConditionTopic);
        // TODO Where a participant is shared it is not safe to unregister types
//        ice.DeviceAlertConditionTypeSupport.unregister_type(domainParticipant, ice.DeviceAlertConditionTypeSupport.get_type_name());

        publisher.delete_datawriter(patientAlertWriter);
        domainParticipant.delete_topic(patientAlertTopic);
        
        publisher.delete_datawriter(technicalAlertWriter);
        domainParticipant.delete_topic(technicalAlertTopic);
        // TODO Where a participant is shared it is not safe to unregister types
//        ice.AlertTypeSupport.unregister_type(domainParticipant, ice.AlertTypeSupport.get_type_name());
        
        averagingThread.interrupt();

        log.info("AbstractDevice shutdown complete");
    }

    public AbstractDevice(final Subscriber subscriber, final Publisher publisher, final EventLoop eventLoop) {

        deviceIdentity  = (new DeviceIdentityBuilder()).osName().softwareRev().withIcon(this, iconResourceName()).build();
        
        this.domainParticipant = subscriber.get_participant();
        this.subscriber = subscriber;
        this.publisher = publisher;

        timestampFactory = new DomainClock(domainParticipant);

        DeviceIdentityTypeSupport.register_type(domainParticipant, DeviceIdentityTypeSupport.get_type_name());
        deviceIdentityTopic = TopicUtil.findOrCreateTopic(domainParticipant, ice.DeviceIdentityTopic.VALUE, ice.DeviceIdentityTypeSupport.class);
        deviceIdentityWriter = (DeviceIdentityDataWriter) publisher.create_datawriter_with_profile(deviceIdentityTopic, QosProfiles.ice_library,
                QosProfiles.device_identity, null, StatusKind.STATUS_MASK_NONE);
        if (null == deviceIdentityWriter) {
            throw new RuntimeException("deviceIdentityWriter not created");
        }

        NumericTypeSupport.register_type(domainParticipant, NumericTypeSupport.get_type_name());
        numericTopic = TopicUtil.findOrCreateTopic(domainParticipant, ice.NumericTopic.VALUE, NumericTypeSupport.class);
        numericDataWriter = (NumericDataWriter) publisher.create_datawriter_with_profile(numericTopic, QosProfiles.ice_library,
                QosProfiles.numeric_data, null, StatusKind.STATUS_MASK_NONE);
        if (null == numericDataWriter) {
            throw new RuntimeException("numericDataWriter not created");
        }

        SampleArrayTypeSupport.register_type(domainParticipant, SampleArrayTypeSupport.get_type_name());
        sampleArrayTopic = TopicUtil.findOrCreateTopic(domainParticipant, ice.SampleArrayTopic.VALUE, SampleArrayTypeSupport.class);
        sampleArrayDataWriter = (SampleArrayDataWriter) publisher.create_datawriter_with_profile(sampleArrayTopic, QosProfiles.ice_library,
                QosProfiles.waveform_data, null, StatusKind.STATUS_MASK_NONE);
        if (null == sampleArrayDataWriter) {
            throw new RuntimeException("sampleArrayDataWriter not created");
        }

        ice.AlarmLimitTypeSupport.register_type(domainParticipant, ice.AlarmLimitTypeSupport.get_type_name());
        alarmLimitTopic = TopicUtil.findOrCreateTopic(domainParticipant, ice.AlarmLimitTopic.VALUE, ice.AlarmLimitTypeSupport.class);
        alarmLimitDataWriter = (ice.AlarmLimitDataWriter) publisher.create_datawriter_with_profile(alarmLimitTopic, QosProfiles.ice_library,
                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        ice.LocalAlarmLimitObjectiveTypeSupport.register_type(domainParticipant, ice.LocalAlarmLimitObjectiveTypeSupport.get_type_name());
        localAlarmLimitObjectiveTopic = TopicUtil.findOrCreateTopic(domainParticipant, ice.LocalAlarmLimitObjectiveTopic.VALUE,
                ice.LocalAlarmLimitObjectiveTypeSupport.class);
        alarmLimitObjectiveWriter = (LocalAlarmLimitObjectiveDataWriter) publisher.create_datawriter_with_profile(localAlarmLimitObjectiveTopic,
                QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        globalAlarmLimitObjectiveTopic = TopicUtil.findOrCreateTopic(domainParticipant, ice.GlobalAlarmLimitObjectiveTopic.VALUE,
                ice.GlobalAlarmLimitObjectiveTypeSupport.class);
        alarmLimitObjectiveReader = (ice.GlobalAlarmLimitObjectiveDataReader) subscriber.create_datareader_with_profile(
                 globalAlarmLimitObjectiveTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
       
        ice.DeviceAlertConditionTypeSupport.register_type(domainParticipant, ice.DeviceAlertConditionTypeSupport.get_type_name());
        deviceAlertConditionTopic = TopicUtil.findOrCreateTopic(domainParticipant, ice.DeviceAlertConditionTopic.VALUE,
                ice.DeviceAlertConditionTypeSupport.class);
        deviceAlertConditionWriter = (ice.DeviceAlertConditionDataWriter) publisher.create_datawriter_with_profile(deviceAlertConditionTopic,
                QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        ice.AlertTypeSupport.register_type(domainParticipant, ice.AlertTypeSupport.get_type_name());
        patientAlertTopic = TopicUtil.findOrCreateTopic(domainParticipant, ice.PatientAlertTopic.VALUE, ice.AlertTypeSupport.class);
        patientAlertWriter = (ice.AlertDataWriter) publisher.create_datawriter_with_profile(patientAlertTopic, QosProfiles.ice_library,
                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        
        technicalAlertTopic = TopicUtil.findOrCreateTopic(domainParticipant, ice.TechnicalAlertTopic.VALUE, ice.AlertTypeSupport.class);
        technicalAlertWriter = (ice.AlertDataWriter) publisher.create_datawriter_with_profile(technicalAlertTopic, QosProfiles.ice_library,
                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        this.eventLoop = eventLoop;
        try {
        	dbconn = SQLLogging.getConnection();
			numericStatement=dbconn.prepareStatement("INSERT INTO allnumerics(t_sec, t_nanosec, udi, metric_id, val) VALUES (?,?,?,?,?)");
			sampleStatement=dbconn.prepareStatement("INSERT INTO allsamples(t_sec, t_nanosec, udi, metric_id, floats) VALUES (?,?,?,?,?)");
			numericExportStatement=dbconn.prepareStatement("INSERT INTO numerics_for_export(t_sec, t_nanosec, udi, metric_id, val) VALUES (?,?,?,?,?)");
		} catch (SQLException e) {
			log.warn("Could not connect to database - server probably not running",e);
		}
        
        /*
         * Later on, add a constructor with a param for this...
         */
        averagingTime=DEFAULT_AVERAGING_TIME;
        averagingThread=new AveragingThread(averagingTime);
        averagingThread.start();
        
    }

    /**
     * post-construction initialization method to allow implementations to
     * initialize/start whatever sub-components they manage. Ideally, for
     * more sophisticated devices everything complex should be moved out and
     * assembled via 'spring' ioc composition, but there are plenty of cases
     * in the middle where this is appropriate. This would be spring's
     * InitializingBean::afterPropertiesSet lifecycle pointcut.
     */
    public void init() {
    }

    /**
     * @return an instance of the device clock that should be used in stamping messages. Fall-back implementation
     * will supply dds time. If device maintains its own notion of the clock, it could use DeviceClock.ComboClock wrapper
     * to provide clock reading that would contain multiple values.
     **/

    protected final DeviceClock getClockProvider() {
        return timestampFactory;
    }

    public void setAlarmLimit(ice.GlobalAlarmLimitObjective obj) {
    }

    public void unsetAlarmLimit(String metricId, ice.LimitType limit_type) {
    }

    private static class MetricAndType {
        private final String metric_id;
        private final ice.LimitType limit_type;
        public MetricAndType(final String metric_id, final ice.LimitType limit_type) {
            this.metric_id = metric_id;
            this.limit_type = limit_type;
        }
        public ice.LimitType getLimit_type() {
            return limit_type;
        }
        public String getMetric_id() {
            return metric_id;
        }
    }
    private Map<InstanceHandle_t, MetricAndType> instanceToAlarmLimit = new HashMap<>();

    protected void writeDeviceIdentity() {
        if (null == deviceIdentity.unique_device_identifier || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("cannot write deviceIdentity without a UDI");
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
                                        instanceToAlarmLimit.put(new InstanceHandle_t(si.instance_handle), new MetricAndType(obj.metric_id, obj.limit_type));
                                    }

                                    if (0 != (si.instance_state & InstanceStateKind.ALIVE_INSTANCE_STATE)) {
                                        if (si.valid_data) {
                                        	String msg="Limit " + obj.metric_id + " "+obj.limit_type+" limit changed to [ " + obj.value + "  " + obj.unit_identifier + "]";
                                            log.warn(msg);
                                            SQLLogging.log("AbstractDevice", msg);
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
                                        MetricAndType mt = instanceToAlarmLimit.get(si.instance_handle);
                                        
                                        if (null != mt) {
                                            log.debug("Unsetting alarm limit " + mt.getMetric_id()+ " " + mt.getLimit_type());
                                            unsetAlarmLimit(mt.getMetric_id(), mt.getLimit_type());
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

    @ManagedAttribute(description="Device Manufacturer.")
    public String getManufacturer() {
        return null == deviceIdentity ? null : deviceIdentity.manufacturer;
    }

    @ManagedAttribute(description="Device Model.")
    public String getModel() {
        return null == deviceIdentity ? null : deviceIdentity.model;
    }

    @ManagedAttribute(description="Unique Device Identifier.")
    public String getUniqueDeviceIdentifier() {
        return null == deviceIdentity ? null : deviceIdentity.unique_device_identifier;
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
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
    
    /**
     * A class for keeping a rolling array to derive an average from.
     * It uses an ArrayList to keep the values, rather than a fixed size
     * array, to cater for variable frequency of data.  Calling the get()
     * method empties the array.  Adding via the add() method or calculating
     * the average are synchronized on the ArrayList, as it's expected that the
     * average will be requested from a different thread to one populating the
     * values.

     * @author simon
     *
     */
    class Averager {
    	
    	private ArrayList<Float> values;
    	
    	public Averager() {
    		values=new ArrayList<>();
    	}
    	
    	public void add(float f) {
    		synchronized (values) {
    			values.add(f);
			}
    	}
    	
    	/**
    	 * Derive the average
    	 * @return the average of all the floats in the values array
    	 */
    	public float get() {
    		synchronized (values) {
    			float avg=0;
    			int i=0;
				for(;i<values.size();i++) {
					avg+=values.get(i);
				}
				avg=avg/i;
				values.clear();
				return avg;
			}
    	}
    }
    
    class AveragingThread extends Thread {
    	
    	private int interval;
    	
    	public AveragingThread(int interval) {
    		this.interval=interval;
    	}

		@Override
		public void run() {
			while(true) {
				try {
					sleep(interval);
					for(Enumeration<String> e=averagesByNumeric.keys();e.hasMoreElements();) {
						String key=e.nextElement();
						float val=averagesByNumeric.get(key).get();
						int second=(int)(System.currentTimeMillis()/1000);
						//INSERT INTO numerics_for_export(t_sec, t_nanosec, udi, metric_id, val);
						numericExportStatement.setInt(1, second);
						numericExportStatement.setInt(2, 0);	//For now.
						numericExportStatement.setString(3, deviceIdentity.unique_device_identifier);
						numericExportStatement.setString(4, key);
						numericExportStatement.setFloat(5, val);
						numericExportStatement.execute();
					}
				} catch (InterruptedException e) {
					log.info("Averaging Thread was interrupted");
					return;
				} catch (SQLException sqle) {
					log.error("Could not add average value for numerics", sqle);
				}
			}
		}
    	
    }

}
