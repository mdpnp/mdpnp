/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import org.mdpnp.devices.EventLoop.ConditionHandler;
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

public abstract class AbstractDevice implements ThreadFactory {
    protected final ThreadGroup threadGroup;
    protected final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(this);
    protected final EventLoop eventLoop;

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
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null
                || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException(
                    "Please populate deviceIdentity.unique_device_identifier before calling createNumericInstance");
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
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null
                || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException(
                    "Please populate deviceIdentity.unique_device_identifier before calling createAlarmInstance");
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
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null
                || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException(
                    "Please populate deviceIdentity.unique_device_identifier before calling createAlarmInstance");
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
        while(!registeredAlarmSettingsObjectiveInstances.isEmpty()) {
            unregisterAlarmSettingsObjectiveInstance(registeredAlarmSettingsObjectiveInstances.get(0));
        }
    }

    protected void unregisterAllAlarmSettingsInstances() {
        while(!registeredAlarmSettingsInstances.isEmpty()) {
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
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null
                || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException(
                    "Please populate deviceIdentity.unique_device_identifier before calling createSampleArrayInstance");
        }

        InstanceHolder<SampleArray> holder = new InstanceHolder<SampleArray>();
        holder.data = new SampleArray();
        holder.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
        holder.data.metric_id = metric_id;
        holder.data.instance_id = instance_id;
        holder.handle = sampleArrayDataWriter.register_instance(holder.data);
        registeredSampleArrayInstances.add(holder);
        return holder;
    }

    protected void numericSample(InstanceHolder<Numeric> holder, float newValue, Time_t time) {
        holder.data.value = newValue;
        if(null != time) {
            numericDataWriter.write_w_timestamp(holder.data, holder.handle, time);
        } else {
            numericDataWriter.write(holder.data, holder.handle);
        }
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

    protected InstanceHolder<ice.AlarmSettings> alarmSettingsSample(InstanceHolder<ice.AlarmSettings> holder, Float newLower, Float newUpper, String metric_id) {
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
            if(null != newLower) {
                log.warn("Not setting only a lower limit on " + metric_id + " for " + holder.data.unique_device_identifier);
            }
            if(null != newUpper) {
                log.warn("Not setting only an upper limit on " + metric_id + " for " + holder.data.unique_device_identifier);
            }
            if (null != holder) {
                unregisterAlarmSettingsInstance(holder);
                holder = null;
            }

        }

        return holder;
    }

    protected InstanceHolder<ice.LocalAlarmSettingsObjective> alarmSettingsObjectiveSample(InstanceHolder<ice.LocalAlarmSettingsObjective> holder, Float newLower, Float newUpper, String metric_id) {
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
        return numericSample(holder, null==newValue?((Float)null):((Float)(float)(int)newValue), metric_id, time);
    }

    // For convenience
    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Integer newValue, String metric_id, int instance_id, Time_t time) {
        return numericSample(holder, null==newValue?((Float)null):((Float)(float)(int)newValue), metric_id, instance_id, time);
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


    protected void sampleArraySample(InstanceHolder<SampleArray> holder, Collection<Number> newValues, int msPerSample, Time_t timestamp) {
        holder.data.values.clear();
        for (Number n : newValues) {
            holder.data.values.addFloat(n.floatValue());
        }
        holder.data.millisecondsPerSample = msPerSample;
//        log.info("Source:"+holder.data);
        if(null != timestamp) {
            sampleArrayDataWriter.write_w_timestamp(holder.data, holder.handle, timestamp);
        } else {
            sampleArrayDataWriter.write(holder.data, holder.handle);
        }
    }

    protected void sampleArraySample(InstanceHolder<SampleArray> holder, Number[] newValues, int msPerSample, Time_t timestamp) {
        sampleArraySample(holder, Arrays.asList(newValues), msPerSample, timestamp);
    }

    protected InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder, Number[] newValues,
            int msPerSample, String metric_id, Time_t timestamp) {
        return sampleArraySample(holder, newValues, msPerSample, metric_id, 0, timestamp);
    }

    protected InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder, Number[] newValues,
            int msPerSample, String metric_id, int instance_id, Time_t timestamp) {
        if (null != holder && (!holder.data.metric_id.equals(metric_id) || holder.data.instance_id != instance_id)) {
            unregisterSampleArrayInstance(holder);
            holder = null;
        }
        if (null != newValues) {
            if (null == holder) {
                holder = createSampleArrayInstance(metric_id, instance_id);
            }
            sampleArraySample(holder, newValues, msPerSample, timestamp);
        } else {
            if (holder != null) {
                unregisterSampleArrayInstance(holder);
                holder = null;
            }
        }
        return holder;
    }

    protected void sampleArraySample(InstanceHolder<SampleArray> holder, int[] newValues, int count, int msPerSample) {
        holder.data.values.clear();
        for (int i = 0; i < count; i++) {
            holder.data.values.addFloat(newValues[i]);
        }
        holder.data.millisecondsPerSample = msPerSample;

        sampleArrayDataWriter.write(holder.data, holder.handle);
    }

    protected InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder, int[] newValues,
            int count, int msPerSample, String metric_id) {
        return sampleArraySample(holder, newValues, count, msPerSample, metric_id, 0);
    }

    protected InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder, Collection<Number> newValues,
           int msPerSample, String metric_id, int instance_id, Time_t timestamp) {
        // if the specified holder doesn't match the specified name
        if (holder != null && (!holder.data.metric_id.equals(metric_id) || holder.data.instance_id != instance_id)) {
            unregisterSampleArrayInstance(holder);
            holder = null;
        }

        if (null != newValues) {
            if (null == holder) {
                holder = createSampleArrayInstance(metric_id, instance_id);
            }
            sampleArraySample(holder, newValues, msPerSample, timestamp);
        } else {
            if (holder != null) {
                unregisterSampleArrayInstance(holder);
                holder = null;
            }
        }
        return holder;
    }

    protected InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder, int[] newValues,
            int count, int msPerSample, String metric_id, int instance_id) {
        // if the specified holder doesn't match the specified name
        if (holder != null && (!holder.data.metric_id.equals(metric_id) || holder.data.instance_id != instance_id)) {
            unregisterSampleArrayInstance(holder);
            holder = null;
        }

        if (null != newValues) {
            if (null == holder) {
                holder = createSampleArrayInstance(metric_id, instance_id);
            }
            sampleArraySample(holder, newValues, count, msPerSample);
        } else {
            if (holder != null) {
                unregisterSampleArrayInstance(holder);
                holder = null;
            }
        }
        return holder;
    }

    protected String iconResourceName() {
        return null;
    }

    protected boolean iconFromResource(DeviceIdentity di, String iconResourceName) throws IOException {
        if (null != iconResourceName) {
            try {

                Method read = Class.forName("javax.imageio.ImageIO").getMethod("read", URL.class);
                Object bi = read.invoke(null, getClass().getResource(iconResourceName));
                // BufferedImage bi =
                // ImageIO.read(getClass().getResource(iconResourceName));
                Class<?> bufferedImage = Class.forName("java.awt.image.BufferedImage");
                di.icon.width = (Integer) bufferedImage.getMethod("getWidth").invoke(bi);
                // int width = bi.getWidth();
                di.icon.height = (Integer) bufferedImage.getMethod("getHeight").invoke(bi);

                // int height = bi.getHeight();
                Method getRGB = bufferedImage.getMethod("getRGB", int.class, int.class);

                byte[] raster = new byte[di.icon.width * di.icon.height * 4];
                log.trace("Image w=" + di.icon.width + " h=" + di.icon.height + " raster.length=" + raster.length);
                IntBuffer bb = ByteBuffer.wrap(raster).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
                for (int y = 0; y < di.icon.height; y++) {
                    for (int x = 0; x < di.icon.width; x++) {
                        bb.put((Integer) getRGB.invoke(bi, x, y));
                        // bb.put(bi.getRGB(x, y));
                    }
                }
                di.icon.raster.clear();
                di.icon.raster.addAllByte(raster);
                return true;
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException) e.getCause();
                }
            } catch (Exception e) {
                log.error("error in iconUpdateFromResource", e);
            }
            return false;
        } else {
            return false;
        }

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


        if(null != alarmSettingsObjectiveCondition) {
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

        domainParticipant = DomainParticipantFactory.get_instance().create_participant(domainId, pQos, null,
                StatusKind.STATUS_MASK_NONE);
        publisher = domainParticipant.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null,
                StatusKind.STATUS_MASK_NONE);
        subscriber = domainParticipant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null,
                StatusKind.STATUS_MASK_NONE);

        DeviceIdentityTypeSupport.register_type(domainParticipant, DeviceIdentityTypeSupport.get_type_name());
        deviceIdentityTopic = domainParticipant.create_topic(ice.DeviceIdentityTopic.VALUE,
                DeviceIdentityTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null,
                StatusKind.STATUS_MASK_NONE);
        deviceIdentityWriter = (DeviceIdentityDataWriter) publisher.create_datawriter(deviceIdentityTopic,
                Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        if (null == deviceIdentityWriter) {
            throw new RuntimeException("deviceIdentityWriter not created");
        }
        deviceIdentity = new DeviceIdentity();
        try {
            iconFromResource(deviceIdentity, iconResourceName());
        } catch (IOException e1) {
            log.warn("", e1);
        }

        NumericTypeSupport.register_type(domainParticipant, NumericTypeSupport.get_type_name());
        numericTopic = domainParticipant.create_topic(ice.NumericTopic.VALUE, NumericTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        numericDataWriter = (NumericDataWriter) publisher.create_datawriter(numericTopic,
                Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        if (null == numericDataWriter) {
            throw new RuntimeException("numericDataWriter not created");
        }

        SampleArrayTypeSupport.register_type(domainParticipant, SampleArrayTypeSupport.get_type_name());
        sampleArrayTopic = domainParticipant.create_topic(ice.SampleArrayTopic.VALUE,
                SampleArrayTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null,
                StatusKind.STATUS_MASK_NONE);
        sampleArrayDataWriter = (SampleArrayDataWriter) publisher.create_datawriter(sampleArrayTopic,
                Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        if (null == sampleArrayDataWriter) {
            throw new RuntimeException("sampleArrayDataWriter not created");
        }

        ice.AlarmSettingsTypeSupport.register_type(domainParticipant, ice.AlarmSettingsTypeSupport.get_type_name());
        alarmSettingsTopic = domainParticipant.create_topic(ice.AlarmSettingsTopic.VALUE, ice.AlarmSettingsTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        alarmSettingsDataWriter = (ice.AlarmSettingsDataWriter) publisher.create_datawriter(alarmSettingsTopic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        ice.LocalAlarmSettingsObjectiveTypeSupport.register_type(domainParticipant, ice.LocalAlarmSettingsObjectiveTypeSupport.get_type_name());
        alarmSettingsObjectiveTopic = domainParticipant.create_topic(ice.LocalAlarmSettingsObjectiveTopic.VALUE, ice.LocalAlarmSettingsObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        alarmSettingsObjectiveWriter = (LocalAlarmSettingsObjectiveDataWriter) publisher.create_datawriter(alarmSettingsObjectiveTopic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        TopicDescription alarmSettingsObjectiveTopic = TopicUtil.lookupOrCreateTopic(domainParticipant, ice.GlobalAlarmSettingsObjectiveTopic.VALUE, ice.GlobalAlarmSettingsObjectiveTypeSupport.class);
        alarmSettingsObjectiveReader = (ice.GlobalAlarmSettingsObjectiveDataReader) subscriber.create_datareader(alarmSettingsObjectiveTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);


        threadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup(), "AbstractDevice") {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error("Thrown by " + t.toString(), e);
                super.uncaughtException(t, e);
            }
        };


        threadGroup.setDaemon(true);
        this.eventLoop = eventLoop;
    }

    public void setAlarmSettings(ice.GlobalAlarmSettingsObjective obj) {

    }

    public void unsetAlarmSettings(String metricId) {

    }

    private Map<InstanceHandle_t, String> instanceMetrics = new HashMap<InstanceHandle_t, String>();


    protected void writeDeviceIdentity() {
        if(null==deviceIdentity.unique_device_identifier||"".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("cannot write deviceIdentity without a UDI");
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

        if(null == deviceIdentityHandle) {
            deviceIdentityHandle = deviceIdentityWriter.register_instance(deviceIdentity);
        }
        deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);

        if(null == alarmSettingsObjectiveCondition) {
            final SampleInfoSeq info_seq = new SampleInfoSeq();
            final ice.GlobalAlarmSettingsObjectiveSeq data_seq = new ice.GlobalAlarmSettingsObjectiveSeq();
            eventLoop.addHandler(alarmSettingsObjectiveCondition = alarmSettingsObjectiveReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE),
                new ConditionHandler() {
                    @Override
                    public void conditionChanged(Condition condition) {
                        try {
                            alarmSettingsObjectiveReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition) condition);
                            for(int i = 0; i < data_seq.size(); i++) {
                                SampleInfo si = (SampleInfo) info_seq.get(i);
                                ice.GlobalAlarmSettingsObjective obj = (ice.GlobalAlarmSettingsObjective) data_seq.get(i);

                                if(0 != (si.view_state & ViewStateKind.NEW_VIEW_STATE) && si.valid_data) {
                                    log.warn("Handle for metric_id="+obj.metric_id+" is " + si.instance_handle);
                                    instanceMetrics.put(new InstanceHandle_t(si.instance_handle), obj.metric_id);
                                }

                                if(0 != (si.instance_state & InstanceStateKind.ALIVE_INSTANCE_STATE)) {
                                    if(si.valid_data) {
                                        log.warn("Setting " + obj.metric_id + " to [ " + obj.lower + " , " + obj.upper + "]");
                                        setAlarmSettings(obj);
                                    }
                                } else {
                                    obj = new ice.GlobalAlarmSettingsObjective();
                                    log.warn("Unsetting handle " + si.instance_handle);
                                    // TODO 1-Oct-2013 JP This call to get_key_value fails consistently on ARM platforms
                                    // so I'm tracking instances externally for the time being
//                                    alarmSettingsObjectiveReader.get_key_value(obj, si.instance_handle);
                                    String metricId = instanceMetrics.get(si.instance_handle);
                                    log.warn("Unsetting " + metricId);
                                    if(null != metricId) {
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
}
