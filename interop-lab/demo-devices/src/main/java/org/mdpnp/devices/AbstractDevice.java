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

import ice.DeviceIdentity;
import ice.DeviceIdentityDataWriter;
import ice.DeviceIdentityTypeSupport;
import ice.LocalAlarmSettingsObjectiveDataWriter;
import ice.Numeric;
import ice.NumericDataWriter;
import ice.NumericTypeSupport;
import ice.SampleArray;
import ice.SampleArrayDataWriter;
import ice.SampleArrayTypeSupport;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

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
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;
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

    protected final Topic numericTopic;
    protected final NumericDataWriter numericDataWriter;

    protected final Topic sampleArrayTopic;
    protected final SampleArrayDataWriter sampleArrayDataWriter;

    protected final Topic alarmSettingsTopic;
    protected final ice.AlarmSettingsDataWriter alarmSettingsDataWriter;

    protected final Topic alarmSettingsObjectiveTopic;
    protected final ice.LocalAlarmSettingsObjectiveDataWriter alarmSettingsObjectiveWriter;

    protected ice.GlobalAlarmSettingsObjectiveDataReader alarmSettingsObjectiveReader;
    protected ReadCondition alarmSettingsObjectiveCondition;

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

    protected InstanceHolder<Numeric> createNumericInstance(String metric_id) {
        return createNumericInstance(metric_id, 0);
    }

    protected InstanceHolder<Numeric> createNumericInstance(String metric_id, int instance_id) {
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("Please populate deviceIdentity.unique_device_identifier before calling createNumericInstance");
        }

        InstanceHolder<Numeric> holder = new InstanceHolder<Numeric>();
        holder.data = new Numeric();
        holder.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
        holder.data.metric_id = metric_id;
        holder.data.instance_id = instance_id;
        holder.handle = numericDataWriter.register_instance(holder.data);

        registeredNumericInstances.add(holder);
        return holder;
    }

    protected InstanceHolder<ice.AlarmSettings> createAlarmSettingsInstance(String metric_id) {
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("Please populate deviceIdentity.unique_device_identifier before calling createAlarmInstance");
        }

        InstanceHolder<ice.AlarmSettings> holder = new InstanceHolder<ice.AlarmSettings>();
        holder.data = new ice.AlarmSettings();
        holder.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
        holder.data.metric_id = metric_id;
        holder.handle = alarmSettingsDataWriter.register_instance(holder.data);
        registeredAlarmSettingsInstances.add(holder);
        return holder;
    }

    protected InstanceHolder<ice.LocalAlarmSettingsObjective> createAlarmSettingsObjectiveInstance(String metric_id) {
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("Please populate deviceIdentity.unique_device_identifier before calling createAlarmInstance");
        }

        InstanceHolder<ice.LocalAlarmSettingsObjective> holder = new InstanceHolder<ice.LocalAlarmSettingsObjective>();
        holder.data = new ice.LocalAlarmSettingsObjective();
        holder.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
        holder.data.metric_id = metric_id;
        holder.handle = alarmSettingsObjectiveWriter.register_instance(holder.data);
        registeredAlarmSettingsObjectiveInstances.add(holder);
        return holder;
    }

    protected void unregisterAllInstances() {
        unregisterAllNumericInstances();
        unregisterAllSampleArrayInstances();
        unregisterAllAlarmSettingsInstances();
        unregisterAllAlarmSettingsObjectiveInstances();
    }

    protected void unregisterAllAlarmSettingsObjectiveInstances() {
        while (!registeredAlarmSettingsObjectiveInstances.isEmpty()) {
            unregisterAlarmSettingsObjectiveInstance(registeredAlarmSettingsObjectiveInstances.get(0));
        }
    }

    protected void unregisterAllAlarmSettingsInstances() {
        while (!registeredAlarmSettingsInstances.isEmpty()) {
            unregisterAlarmSettingsInstance(registeredAlarmSettingsInstances.get(0));
        }
    }

    protected void unregisterAllNumericInstances() {
        while (!registeredNumericInstances.isEmpty()) {
            unregisterNumericInstance(registeredNumericInstances.get(0));
        }
    }

    protected void unregisterAllSampleArrayInstances() {
        while (!registeredSampleArrayInstances.isEmpty()) {
            unregisterSampleArrayInstance(registeredSampleArrayInstances.get(0), null);
        }
    }

    protected void unregisterNumericInstance(InstanceHolder<Numeric> holder) {
        if (null != holder) {
            registeredNumericInstances.remove(holder);
            numericDataWriter.unregister_instance(holder.data, holder.handle);
        }
    }

    protected void unregisterSampleArrayInstance(InstanceHolder<SampleArray> holder, Time_t timestamp) {
        registeredSampleArrayInstances.remove(holder);
        if(sampleArraySpecifySourceTimestamp()&&null!=timestamp) {
            sampleArrayDataWriter.unregister_instance_w_timestamp(holder.data, holder.handle, timestamp);
        } else {
            sampleArrayDataWriter.unregister_instance(holder.data, holder.handle);
        }
    }

    protected void unregisterAlarmSettingsInstance(InstanceHolder<ice.AlarmSettings> holder) {
        registeredAlarmSettingsInstances.remove(holder);
        alarmSettingsDataWriter.unregister_instance(holder.data, holder.handle);
    }

    protected void unregisterAlarmSettingsObjectiveInstance(InstanceHolder<ice.LocalAlarmSettingsObjective> holder) {
        registeredAlarmSettingsObjectiveInstances.remove(holder);
        alarmSettingsObjectiveWriter.unregister_instance(holder.data, holder.handle);
    }

    private List<InstanceHolder<SampleArray>> registeredSampleArrayInstances = new ArrayList<InstanceHolder<SampleArray>>();
    private List<InstanceHolder<Numeric>> registeredNumericInstances = new ArrayList<InstanceHolder<Numeric>>();
    private List<InstanceHolder<ice.AlarmSettings>> registeredAlarmSettingsInstances = new ArrayList<InstanceHolder<ice.AlarmSettings>>();
    private List<InstanceHolder<ice.LocalAlarmSettingsObjective>> registeredAlarmSettingsObjectiveInstances = new ArrayList<InstanceHolder<ice.LocalAlarmSettingsObjective>>();

    protected InstanceHolder<SampleArray> createSampleArrayInstance(String metric_id) {
        return createSampleArrayInstance(metric_id, 0);
    }

    protected InstanceHolder<SampleArray> createSampleArrayInstance(String metric_id, int instance_id) {
        return createSampleArrayInstance(metric_id, instance_id, null);
    }

    protected InstanceHolder<SampleArray> createSampleArrayInstance(String metric_id, int instance_id, Time_t timestamp) {
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("Please populate deviceIdentity.unique_device_identifier before calling createSampleArrayInstance");
        }

        InstanceHolder<SampleArray> holder = new InstanceHolder<SampleArray>();
        holder.data = new SampleArray();
        holder.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
        holder.data.metric_id = metric_id;
        holder.data.instance_id = instance_id;
        if(sampleArraySpecifySourceTimestamp()) {
            holder.handle = sampleArrayDataWriter.register_instance_w_timestamp(holder.data, timestamp);
        }
        registeredSampleArrayInstances.add(holder);
        return holder;
    }

    protected void numericSample(InstanceHolder<Numeric> holder, float newValue, Time_t time) {
        holder.data.value = newValue;
        if (null != time) {
            holder.data.device_time.sec = time.sec;
            holder.data.device_time.nanosec = time.nanosec;
        } else {
            holder.data.device_time.sec = 0;
            holder.data.device_time.nanosec = 0;
        }
        numericDataWriter.write(holder.data, holder.handle);
    }

    protected void alarmSettingsSample(InstanceHolder<ice.AlarmSettings> holder, float newLower, float newUpper) {
        holder.data.lower = newLower;
        holder.data.upper = newUpper;
        alarmSettingsDataWriter.write(holder.data, holder.handle);
    }

    protected void alarmSettingsObjectiveSample(InstanceHolder<ice.LocalAlarmSettingsObjective> holder, float newLower, float newUpper) {
        holder.data.lower = newLower;
        holder.data.upper = newUpper;
        alarmSettingsObjectiveWriter.write(holder.data, holder.handle);
    }

    protected InstanceHolder<ice.AlarmSettings> alarmSettingsSample(InstanceHolder<ice.AlarmSettings> holder, Float newLower, Float newUpper,
            String metric_id) {
        if (holder != null && !holder.data.metric_id.equals(metric_id)) {
            unregisterAlarmSettingsInstance(holder);
            holder = null;
        }
        if (null != newLower && null != newUpper) {
            if (null == holder) {
                holder = createAlarmSettingsInstance(metric_id);
            }
            alarmSettingsSample(holder, newLower, newUpper);
        } else {
            if (null != newLower) {
                log.warn("Not setting only a lower limit on " + metric_id + " for " + holder.data.unique_device_identifier);
            }
            if (null != newUpper) {
                log.warn("Not setting only an upper limit on " + metric_id + " for " + holder.data.unique_device_identifier);
            }
            if (null != holder) {
                unregisterAlarmSettingsInstance(holder);
                holder = null;
            }

        }

        return holder;
    }

    protected InstanceHolder<ice.LocalAlarmSettingsObjective> alarmSettingsObjectiveSample(InstanceHolder<ice.LocalAlarmSettingsObjective> holder,
            Float newLower, Float newUpper, String metric_id) {
        if (holder != null && !holder.data.metric_id.equals(metric_id)) {
            unregisterAlarmSettingsObjectiveInstance(holder);
            holder = null;
        }
        if (null != newLower && null != newUpper) {
            if (null == holder) {
                holder = createAlarmSettingsObjectiveInstance(metric_id);
            }
            alarmSettingsObjectiveSample(holder, newLower, newUpper);
        } else {
            if (null != holder) {
                unregisterAlarmSettingsObjectiveInstance(holder);
                holder = null;
            }

        }

        return holder;
    }

    // For convenience
    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Integer newValue, String metric_id, Time_t time) {
        return numericSample(holder, null == newValue ? ((Float) null) : ((Float) (float) (int) newValue), metric_id, time);
    }

    // For convenience
    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Integer newValue, String metric_id, int instance_id, Time_t time) {
        return numericSample(holder, null == newValue ? ((Float) null) : ((Float) (float) (int) newValue), metric_id, instance_id, time);
    }

    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Float newValue, String metric_id, Time_t time) {
        return numericSample(holder, newValue, metric_id, 0, time);
    }

    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Float newValue, String metric_id, int instance_id, Time_t time) {
        if (holder != null && (!holder.data.metric_id.equals(metric_id) || holder.data.instance_id != instance_id)) {
            unregisterNumericInstance(holder);
            holder = null;
        }
        if (null != newValue) {
            if (null == holder) {
                holder = createNumericInstance(metric_id, instance_id);
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
    
    protected boolean sampleArraySpecifySourceTimestamp() {
        return false;
    }

    protected void sampleArraySample(InstanceHolder<ice.SampleArray> holder, Collection<Number> newValues, int msPerSample, Time_t deviceTimestamp) {
        holder.data.values.userData.clear();
        for (Number n : newValues) {
            holder.data.values.userData.addFloat(n.floatValue());
        }
        holder.data.millisecondsPerSample = msPerSample;
        if (deviceTimestamp != null) {
            holder.data.device_time.sec = deviceTimestamp.sec;
            holder.data.device_time.nanosec = deviceTimestamp.nanosec;
        } else {
            holder.data.device_time.sec = 0;
            holder.data.device_time.nanosec = 0;
        }
        if(sampleArraySpecifySourceTimestamp() && null != deviceTimestamp) {
            sampleArrayDataWriter.write_w_timestamp(holder.data, holder.handle, deviceTimestamp);
        } else {
            sampleArrayDataWriter.write(holder.data, holder.handle);
        }
    }

    protected void sampleArraySample(InstanceHolder<ice.SampleArray> holder, int[] newValues, int len, int msPerSample, Time_t deviceTimestamp) {
        holder.data.values.userData.clear();
        for (int n : newValues) {
            holder.data.values.userData.addFloat(n);
        }
        holder.data.millisecondsPerSample = msPerSample;
        if (deviceTimestamp != null) {
            holder.data.device_time.sec = deviceTimestamp.sec;
            holder.data.device_time.nanosec = deviceTimestamp.nanosec;
        } else {
            holder.data.device_time.sec = 0;
            holder.data.device_time.nanosec = 0;
        }
        if(sampleArraySpecifySourceTimestamp() && null != deviceTimestamp) {
            sampleArrayDataWriter.write_w_timestamp(holder.data, holder.handle, deviceTimestamp);
        } else {
            sampleArrayDataWriter.write(holder.data, holder.handle);
        }
    }

    protected void sampleArraySample(InstanceHolder<SampleArray> holder, Number[] newValues, int msPerSample, Time_t timestamp) {
        sampleArraySample(holder, Arrays.asList(newValues), msPerSample, timestamp);
    }

    protected InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder, Number[] newValues, int msPerSample,
            String metric_id, Time_t timestamp) {
        return sampleArraySample(holder, newValues, msPerSample, metric_id, 0, timestamp);
    }

    protected InstanceHolder<ice.SampleArray> sampleArraySample(InstanceHolder<ice.SampleArray> holder, int[] newValues, int len, int msPerSample,
            String metric_id, int instance_id) {
        return sampleArraySample(holder, newValues, len, msPerSample, metric_id, instance_id, null);
    }

    protected InstanceHolder<ice.SampleArray> sampleArraySample(InstanceHolder<ice.SampleArray> holder, int[] newValues, int len, int msPerSample,
            String metric_id, int instance_id, Time_t timestamp) {
        if (null != holder && (!holder.data.metric_id.equals(metric_id) || holder.data.instance_id != instance_id)) {
            unregisterSampleArrayInstance(holder, timestamp);
            holder = null;
        }
        if (null != newValues) {
            if (null == holder) {
                holder = createSampleArrayInstance(metric_id, instance_id, timestamp);
            }
            sampleArraySample(holder, newValues, len, msPerSample, timestamp);
        } else {
            if (holder != null) {
                unregisterSampleArrayInstance(holder, timestamp);
                holder = null;
            }
        }
        return holder;
    }

    protected InstanceHolder<ice.SampleArray> sampleArraySample(InstanceHolder<ice.SampleArray> holder, Number[] newValues, int msPerSample,
            String metric_id, int instance_id, Time_t timestamp) {
        return sampleArraySample(holder, Arrays.asList(newValues), msPerSample, metric_id, instance_id, timestamp);
    }

    protected InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder, Collection<Number> newValues, int msPerSample,
            String metric_id, int instance_id, Time_t timestamp) {
        // if the specified holder doesn't match the specified name
        if (holder != null && (!holder.data.metric_id.equals(metric_id) || holder.data.instance_id != instance_id)) {
            unregisterSampleArrayInstance(holder, timestamp);
            holder = null;
        }

        if (null != newValues) {
            if (null == holder) {
                holder = createSampleArrayInstance(metric_id, instance_id, timestamp);
            }
            sampleArraySample(holder, newValues, msPerSample, timestamp);
        } else {
            if (holder != null) {
                unregisterSampleArrayInstance(holder, timestamp);
                holder = null;
            }
        }
        return holder;
    }

    protected String iconResourceName() {
        return null;
    }

    protected boolean iconFromResource(DeviceIdentity di, String iconResourceName) throws IOException {
        if (null == iconResourceName) {
            di.icon.height = 0;
            di.icon.width = 0;
            di.icon.raster.clear();
            return true;
        }

        InputStream is = getClass().getResourceAsStream(iconResourceName);
        if (null != is) {
            try {
                {
                    byte[] xfer = new byte[1024];
                    int len = is.read(xfer);

                    di.icon.raster.userData.clear();

                    while (len >= 0) {
                        di.icon.raster.userData.addAllByte(xfer, 0, len);
                        len = is.read(xfer);
                    }
                    is.close();
                }

                Method read = Class.forName("javax.imageio.ImageIO").getMethod("read", InputStream.class);
                Object bi = read
                        .invoke(null, new ByteArrayInputStream(di.icon.raster.userData.toArrayByte(new byte[di.icon.raster.userData.size()])));
                Class<?> bufferedImage = Class.forName("java.awt.image.BufferedImage");
                di.icon.width = (Integer) bufferedImage.getMethod("getWidth").invoke(bi);
                di.icon.height = (Integer) bufferedImage.getMethod("getHeight").invoke(bi);
                return true;
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException) e.getCause();
                }
            } catch (Exception e) {
                log.error("error in iconUpdateFromResource", e);
            }
        }
        return false;
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
        
        if (null != alarmSettingsObjectiveCondition) {
            eventLoop.removeHandler(alarmSettingsObjectiveCondition);
            alarmSettingsObjectiveReader.delete_readcondition(alarmSettingsObjectiveCondition);
            alarmSettingsObjectiveCondition = null;
        }

        subscriber.delete_datareader(alarmSettingsObjectiveReader);

        publisher.delete_datawriter(alarmSettingsObjectiveWriter);
        domainParticipant.delete_topic(alarmSettingsObjectiveTopic);
        ice.LocalAlarmSettingsObjectiveTypeSupport.unregister_type(domainParticipant, ice.LocalAlarmSettingsObjectiveTypeSupport.get_type_name());

        publisher.delete_datawriter(alarmSettingsDataWriter);
        domainParticipant.delete_topic(alarmSettingsTopic);
        ice.AlarmSettingsTypeSupport.unregister_type(domainParticipant, ice.AlarmSettingsTypeSupport.get_type_name());

        publisher.delete_datawriter(sampleArrayDataWriter);
        domainParticipant.delete_topic(sampleArrayTopic);
        SampleArrayTypeSupport.unregister_type(domainParticipant, SampleArrayTypeSupport.get_type_name());

        publisher.delete_datawriter(numericDataWriter);
        domainParticipant.delete_topic(numericTopic);
        NumericTypeSupport.unregister_type(domainParticipant, NumericTypeSupport.get_type_name());

        publisher.delete_datawriter(deviceIdentityWriter);
        domainParticipant.delete_topic(deviceIdentityTopic);
        DeviceIdentityTypeSupport.unregister_type(domainParticipant, DeviceIdentityTypeSupport.get_type_name());

        domainParticipant.delete_publisher(publisher);
        domainParticipant.delete_subscriber(subscriber);

        domainParticipant.delete_contained_entities();

        DomainParticipantFactory.get_instance().delete_participant(domainParticipant);

        executor.shutdown();
        log.info("AbstractDevice shutdown complete");
    }

    public AbstractDevice(int domainId, EventLoop eventLoop) {
        DomainParticipantQos pQos = new DomainParticipantQos();
        DomainParticipantFactory.get_instance().get_default_participant_qos(pQos);
        pQos.participant_name.name = "Device";

        domainParticipant = DomainParticipantFactory.get_instance().create_participant(domainId, pQos, null, StatusKind.STATUS_MASK_NONE);
        publisher = domainParticipant.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        subscriber = domainParticipant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        DeviceIdentityTypeSupport.register_type(domainParticipant, DeviceIdentityTypeSupport.get_type_name());
        deviceIdentityTopic = domainParticipant.create_topic(ice.DeviceIdentityTopic.VALUE, DeviceIdentityTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        deviceIdentityWriter = (DeviceIdentityDataWriter) publisher.create_datawriter_with_profile(deviceIdentityTopic, QosProfiles.ice_library,
                QosProfiles.device_identity, null, StatusKind.STATUS_MASK_NONE);
        if (null == deviceIdentityWriter) {
            throw new RuntimeException("deviceIdentityWriter not created");
        }
        deviceIdentity = new DeviceIdentity();
        deviceIdentity.build = BuildInfo.getDescriptor();
        try {
            iconFromResource(deviceIdentity, iconResourceName());
        } catch (IOException e1) {
            log.warn("", e1);
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

        ice.AlarmSettingsTypeSupport.register_type(domainParticipant, ice.AlarmSettingsTypeSupport.get_type_name());
        alarmSettingsTopic = domainParticipant.create_topic(ice.AlarmSettingsTopic.VALUE, ice.AlarmSettingsTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        alarmSettingsDataWriter = (ice.AlarmSettingsDataWriter) publisher.create_datawriter_with_profile(alarmSettingsTopic, QosProfiles.ice_library,
                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        ice.LocalAlarmSettingsObjectiveTypeSupport.register_type(domainParticipant, ice.LocalAlarmSettingsObjectiveTypeSupport.get_type_name());
        alarmSettingsObjectiveTopic = domainParticipant.create_topic(ice.LocalAlarmSettingsObjectiveTopic.VALUE,
                ice.LocalAlarmSettingsObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        alarmSettingsObjectiveWriter = (LocalAlarmSettingsObjectiveDataWriter) publisher.create_datawriter_with_profile(alarmSettingsObjectiveTopic,
                QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        TopicDescription alarmSettingsObjectiveTopic = TopicUtil.lookupOrCreateTopic(domainParticipant, ice.GlobalAlarmSettingsObjectiveTopic.VALUE,
                ice.GlobalAlarmSettingsObjectiveTypeSupport.class);
        alarmSettingsObjectiveReader = (ice.GlobalAlarmSettingsObjectiveDataReader) subscriber.create_datareader_with_profile(
                alarmSettingsObjectiveTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

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
                checkForPartitionFile();
            }
        }, 1000L, 5000L, TimeUnit.MILLISECONDS);
    }

    public void setAlarmSettings(ice.GlobalAlarmSettingsObjective obj) {

    }

    public void unsetAlarmSettings(String metricId) {

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
        
        DomainParticipantQos qos = new DomainParticipantQos();
        domainParticipant.get_qos(qos);
        try {
            qos.user_data.value.clear();
            qos.user_data.value.addAllByte(deviceIdentity.unique_device_identifier.getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        domainParticipant.set_qos(qos);

        if (null == deviceIdentityHandle) {
            deviceIdentityHandle = deviceIdentityWriter.register_instance(deviceIdentity);
        }
        deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);

        if (null == alarmSettingsObjectiveCondition) {
            final SampleInfoSeq info_seq = new SampleInfoSeq();
            final ice.GlobalAlarmSettingsObjectiveSeq data_seq = new ice.GlobalAlarmSettingsObjectiveSeq();
            eventLoop.addHandler(
                    alarmSettingsObjectiveCondition = alarmSettingsObjectiveReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
                            ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE), new ConditionHandler() {
                        @Override
                        public void conditionChanged(Condition condition) {
                            try {
                                alarmSettingsObjectiveReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                        (ReadCondition) condition);
                                for (int i = 0; i < data_seq.size(); i++) {
                                    SampleInfo si = (SampleInfo) info_seq.get(i);
                                    ice.GlobalAlarmSettingsObjective obj = (ice.GlobalAlarmSettingsObjective) data_seq.get(i);

                                    if (0 != (si.view_state & ViewStateKind.NEW_VIEW_STATE) && si.valid_data) {
                                        log.warn("Handle for metric_id=" + obj.metric_id + " is " + si.instance_handle);
                                        instanceMetrics.put(new InstanceHandle_t(si.instance_handle), obj.metric_id);
                                    }

                                    if (0 != (si.instance_state & InstanceStateKind.ALIVE_INSTANCE_STATE)) {
                                        if (si.valid_data) {
                                            log.warn("Setting " + obj.metric_id + " to [ " + obj.lower + " , " + obj.upper + "]");
                                            setAlarmSettings(obj);
                                        }
                                    } else {
                                        obj = new ice.GlobalAlarmSettingsObjective();
                                        log.warn("Unsetting handle " + si.instance_handle);
                                        // TODO 1-Oct-2013 JP This call to
                                        // get_key_value fails consistently on
                                        // ARM platforms
                                        // so I'm tracking instances externally
                                        // for the time being
                                        // alarmSettingsObjectiveReader.get_key_value(obj,
                                        // si.instance_handle);
                                        String metricId = instanceMetrics.get(si.instance_handle);
                                        log.warn("Unsetting " + metricId);
                                        if (null != metricId) {
                                            unsetAlarmSettings(metricId);
                                        }

                                    }
                                }
                            } catch (RETCODE_NO_DATA noData) {

                            } finally {
                                alarmSettingsObjectiveReader.return_loan(data_seq, info_seq);
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
    public String[] getPartition() {
        PublisherQos pQos = new PublisherQos();
        publisher.get_qos(pQos);
        String[] partition = new String[pQos.partition.name.size()];
        for (int i = 0; i < partition.length; i++) {
            partition[i] = (String) pQos.partition.name.get(i);
        }
        return partition;
    }

    @Override
    public void addPartition(String partition) {
        List<String> currentPartition = new ArrayList<String>(Arrays.asList(getPartition()));
        currentPartition.add(partition);
        setPartition(currentPartition.toArray(new String[0]));
    }

    @Override
    public void removePartition(String partition) {
        List<String> currentPartition = new ArrayList<String>(Arrays.asList(getPartition()));
        currentPartition.remove(partition);
        setPartition(currentPartition.toArray(new String[0]));
    }

    @Override
    public void setPartition(String[] partition) {
        PublisherQos pQos = new PublisherQos();
        SubscriberQos sQos = new SubscriberQos();
        publisher.get_qos(pQos);
        subscriber.get_qos(sQos);
        
        if(null == partition) {
            partition = new String[0];
        }
        
        boolean same = partition.length == pQos.partition.name.size();
        
        if(same) {
            for(int i = 0; i < partition.length; i++) {
                if(!partition[i].equals(pQos.partition.name.get(i))) {
                    same = false;
                    break;
                }
            }
        }
        
        if(!same) {
            log.info("Changing partition to " + Arrays.toString(partition));
            pQos.partition.name.clear();
            sQos.partition.name.clear();
            pQos.partition.name.addAll(Arrays.asList(partition));
            sQos.partition.name.addAll(Arrays.asList(partition));
            publisher.set_qos(pQos);
            subscriber.set_qos(sQos);
        } else {
            log.info("Not changing to same partition " + Arrays.toString(partition));
        }
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
    
    private long lastPartitionFileTime = 0L;
    
    public void checkForPartitionFile() {
        File f = new File("device.partition");
        
        if(!f.exists()) {
            // File once existed
            if(lastPartitionFileTime!=0L) {
                setPartition(new String[0]);
                lastPartitionFileTime = 0L;
            } else {
                // No file and it never existed
            }
        } else if(f.canRead() && f.lastModified()>lastPartitionFileTime) {
            try {
                List<String> partition = new ArrayList<String>();
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String line = null;
                while(null != (line = br.readLine())) {
                    partition.add(line);
                }
                br.close();
                setPartition(partition.toArray(new String[0]));
            } catch (FileNotFoundException e) {
                log.error("Reading partition info", e);
            } catch (IOException e) {
                log.error("Reading partition info", e);
            }
            
            lastPartitionFileTime = f.lastModified();
        }
    }
    
    
    
}
