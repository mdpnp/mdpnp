/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices;

import ice.DeviceIdentity;
import ice.GlobalAlarmSettingsObjective;
import ice.Numeric;
import ice.SampleArray;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.mdpnp.devices.EventLoop.ConditionHandler;
import org.omg.dds.core.Condition;
import org.omg.dds.core.InstanceHandle;
import org.omg.dds.core.ServiceEnvironment;
import org.omg.dds.core.Time;
import org.omg.dds.core.policy.Durability;
import org.omg.dds.core.policy.History;
import org.omg.dds.core.policy.Liveliness;
import org.omg.dds.core.policy.Ownership;
import org.omg.dds.core.policy.PolicyFactory;
import org.omg.dds.core.policy.Reliability;
import org.omg.dds.domain.DomainParticipant;
import org.omg.dds.domain.DomainParticipantFactory;
import org.omg.dds.domain.DomainParticipantQos;
import org.omg.dds.pub.DataWriter;
import org.omg.dds.pub.DataWriterQos;
import org.omg.dds.pub.Publisher;
import org.omg.dds.sub.DataReader;
import org.omg.dds.sub.DataReader.Selector;
import org.omg.dds.sub.DataReaderQos;
import org.omg.dds.sub.InstanceState;
import org.omg.dds.sub.ReadCondition;
import org.omg.dds.sub.Sample;
import org.omg.dds.sub.SampleState;
import org.omg.dds.sub.Subscriber;
import org.omg.dds.sub.ViewState;
import org.omg.dds.topic.Topic;
import org.omg.dds.topic.TopicDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDevice implements ThreadFactory {
    protected final ThreadGroup threadGroup;
    protected final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(this);
    protected final EventLoop eventLoop;

    private static final Logger log = LoggerFactory.getLogger(AbstractDevice.class);

    protected final DomainParticipant domainParticipant;
    protected final Publisher publisher;
    protected final Subscriber subscriber;
    protected final Topic<DeviceIdentity> deviceIdentityTopic;
    private final DataWriter<DeviceIdentity> deviceIdentityWriter;
    protected final DeviceIdentity deviceIdentity;
    private InstanceHandle deviceIdentityHandle;

    protected final Topic<Numeric> numericTopic;
    protected final DataWriter<Numeric> numericDataWriter;

    protected final Topic<SampleArray> sampleArrayTopic;
    protected final DataWriter<ice.SampleArray> sampleArrayDataWriter;

    protected final Topic<ice.AlarmSettings> alarmSettingsTopic;
    protected final DataWriter<ice.AlarmSettings> alarmSettingsDataWriter;

    protected final Topic<ice.LocalAlarmSettingsObjective> alarmSettingsObjectiveTopic;
    protected final DataWriter<ice.LocalAlarmSettingsObjective> alarmSettingsObjectiveWriter;

    protected DataReader<ice.GlobalAlarmSettingsObjective> alarmSettingsObjectiveReader;
    protected Selector<ice.GlobalAlarmSettingsObjective> alarmSettingsObjectiveSelector;

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public static class InstanceHolder<T> {
        public T data;
        public InstanceHandle handle;

        public InstanceHolder() {

        }

        public InstanceHolder(T t, InstanceHandle handle) {
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

    protected InstanceHolder<Numeric> createNumericInstance(String metric_id) throws TimeoutException {
        return createNumericInstance(metric_id, 0);
    }

    protected InstanceHolder<Numeric> createNumericInstance(String metric_id, int instance_id) throws TimeoutException {
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
        holder.handle = numericDataWriter.registerInstance(holder.data);
        registeredNumericInstances.add(holder);
        return holder;
    }

    protected InstanceHolder<ice.AlarmSettings> createAlarmSettingsInstance(String metric_id) throws TimeoutException {
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null
                || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException(
                    "Please populate deviceIdentity.unique_device_identifier before calling createAlarmInstance");
        }

        InstanceHolder<ice.AlarmSettings> holder = new InstanceHolder<ice.AlarmSettings>();
        holder.data = new ice.AlarmSettings();
        holder.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
        holder.data.metric_id = metric_id;
        holder.handle = alarmSettingsDataWriter.registerInstance(holder.data);
        registeredAlarmSettingsInstances.add(holder);
        return holder;
    }

    protected InstanceHolder<ice.LocalAlarmSettingsObjective> createAlarmSettingsObjectiveInstance(String metric_id) throws TimeoutException {
        if (deviceIdentity == null || deviceIdentity.unique_device_identifier == null
                || "".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException(
                    "Please populate deviceIdentity.unique_device_identifier before calling createAlarmInstance");
        }

        InstanceHolder<ice.LocalAlarmSettingsObjective> holder = new InstanceHolder<ice.LocalAlarmSettingsObjective>();
        holder.data = new ice.LocalAlarmSettingsObjective();
        holder.data.unique_device_identifier = deviceIdentity.unique_device_identifier;
        holder.data.metric_id = metric_id;
        holder.handle = alarmSettingsObjectiveWriter.registerInstance(holder.data);
        registeredAlarmSettingsObjectiveInstances.add(holder);
        return holder;
    }

    protected void unregisterAllInstances() throws TimeoutException {
        unregisterAllNumericInstances();
        unregisterAllSampleArrayInstances();
        unregisterAllAlarmSettingsInstances();
        unregisterAllAlarmSettingsObjectiveInstances();
    }

    protected void unregisterAllAlarmSettingsObjectiveInstances() throws TimeoutException {
        while(!registeredAlarmSettingsObjectiveInstances.isEmpty()) {
            unregisterAlarmSettingsObjectiveInstance(registeredAlarmSettingsObjectiveInstances.get(0));
        }
    }

    protected void unregisterAllAlarmSettingsInstances() throws TimeoutException {
        while(!registeredAlarmSettingsInstances.isEmpty()) {
            unregisterAlarmSettingsInstance(registeredAlarmSettingsInstances.get(0));
        }
    }

    protected void unregisterAllNumericInstances() throws TimeoutException {
        while (!registeredNumericInstances.isEmpty()) {
            unregisterNumericInstance(registeredNumericInstances.get(0));
        }
    }

    protected void unregisterAllSampleArrayInstances() throws TimeoutException {
        while (!registeredSampleArrayInstances.isEmpty()) {
            unregisterSampleArrayInstance(registeredSampleArrayInstances.get(0));
        }
    }

    protected void unregisterNumericInstance(InstanceHolder<Numeric> holder) throws TimeoutException {
        if (null != holder) {
            registeredNumericInstances.remove(holder);
            numericDataWriter.unregisterInstance(holder.handle, holder.data);
        }
    }

    protected void unregisterSampleArrayInstance(InstanceHolder<SampleArray> holder) throws TimeoutException {
        registeredSampleArrayInstances.remove(holder);
        sampleArrayDataWriter.unregisterInstance(holder.handle, holder.data);
    }

    protected void unregisterAlarmSettingsInstance(InstanceHolder<ice.AlarmSettings> holder) throws TimeoutException {
        registeredAlarmSettingsInstances.remove(holder);
        alarmSettingsDataWriter.unregisterInstance(holder.handle, holder.data);
    }

    protected void unregisterAlarmSettingsObjectiveInstance(InstanceHolder<ice.LocalAlarmSettingsObjective> holder) throws TimeoutException {
        registeredAlarmSettingsObjectiveInstances.remove(holder);
        alarmSettingsObjectiveWriter.unregisterInstance(holder.handle, holder.data);
    }

    private List<InstanceHolder<SampleArray>> registeredSampleArrayInstances = new ArrayList<InstanceHolder<SampleArray>>();
    private List<InstanceHolder<Numeric>> registeredNumericInstances = new ArrayList<InstanceHolder<Numeric>>();
    private List<InstanceHolder<ice.AlarmSettings>> registeredAlarmSettingsInstances = new ArrayList<InstanceHolder<ice.AlarmSettings>>();
    private List<InstanceHolder<ice.LocalAlarmSettingsObjective>> registeredAlarmSettingsObjectiveInstances = new ArrayList<InstanceHolder<ice.LocalAlarmSettingsObjective>>();

    protected InstanceHolder<SampleArray> createSampleArrayInstance(String metric_id) throws TimeoutException {
        return createSampleArrayInstance(metric_id, 0);
    }

    protected InstanceHolder<SampleArray> createSampleArrayInstance(String metric_id, int instance_id) throws TimeoutException {
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
        holder.handle = sampleArrayDataWriter.registerInstance(holder.data);
        registeredSampleArrayInstances.add(holder);
        return holder;
    }

    protected void numericSample(InstanceHolder<Numeric> holder, float newValue, Time time) throws TimeoutException {
        holder.data.value = newValue;
        if(null != time) {
            numericDataWriter.write(holder.data, holder.handle, time);
        } else {
            numericDataWriter.write(holder.data, holder.handle);
        }
    }

    protected void alarmSettingsSample(InstanceHolder<ice.AlarmSettings> holder, float newLower, float newUpper) throws TimeoutException {
        holder.data.lower = newLower;
        holder.data.upper = newUpper;
        alarmSettingsDataWriter.write(holder.data, holder.handle);
    }

    protected void alarmSettingsObjectiveSample(InstanceHolder<ice.LocalAlarmSettingsObjective> holder, float newLower, float newUpper) throws TimeoutException {
        holder.data.lower = newLower;
        holder.data.upper = newUpper;
        alarmSettingsObjectiveWriter.write(holder.data, holder.handle);
    }

    protected InstanceHolder<ice.AlarmSettings> alarmSettingsSample(InstanceHolder<ice.AlarmSettings> holder, Float newLower, Float newUpper, String metric_id) throws TimeoutException {
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

    protected InstanceHolder<ice.LocalAlarmSettingsObjective> alarmSettingsObjectiveSample(InstanceHolder<ice.LocalAlarmSettingsObjective> holder, Float newLower, Float newUpper, String metric_id) throws TimeoutException {
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
    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Integer newValue, String metric_id, Time time) throws TimeoutException {
        return numericSample(holder, null==newValue?((Float)null):((Float)(float)(int)newValue), metric_id, time);
    }

    // For convenience
    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Integer newValue, String metric_id, int instance_id, Time time) throws TimeoutException {
        return numericSample(holder, null==newValue?((Float)null):((Float)(float)(int)newValue), metric_id, instance_id, time);
    }

    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Float newValue, String metric_id, Time time) throws TimeoutException {
        return numericSample(holder, newValue, metric_id, 0, time);
    }

    protected InstanceHolder<Numeric> numericSample(InstanceHolder<Numeric> holder, Float newValue, String metric_id, int instance_id, Time time) throws TimeoutException {
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


    protected void sampleArraySample(InstanceHolder<SampleArray> holder, Collection<Number> newValues, int msPerSample, Time timestamp) throws TimeoutException {
        holder.data.values = new float[newValues.size()];
        Iterator<Number> itr = newValues.iterator();
        int i = 0;
        while(itr.hasNext()) {
            holder.data.values[i++] = itr.next().floatValue();
        }
        holder.data.millisecondsPerSample = msPerSample;
//        log.info("Source:"+holder.data);
        if(null != timestamp) {
            sampleArrayDataWriter.write(holder.data, holder.handle, timestamp);
        } else {
            sampleArrayDataWriter.write(holder.data, holder.handle);
        }
    }

    protected void sampleArraySample(InstanceHolder<SampleArray> holder, Number[] newValues, int msPerSample, Time timestamp) throws TimeoutException {
        sampleArraySample(holder, Arrays.asList(newValues), msPerSample, timestamp);
    }

    protected InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder, Number[] newValues,
            int msPerSample, String metric_id, Time timestamp) throws TimeoutException {
        return sampleArraySample(holder, newValues, msPerSample, metric_id, 0, timestamp);
    }

    protected InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder, Number[] newValues,
            int msPerSample, String metric_id, int instance_id, Time timestamp) throws TimeoutException {
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

    protected void sampleArraySample(InstanceHolder<SampleArray> holder, int[] newValues, int count, int msPerSample) throws TimeoutException {
        holder.data.values = new float[newValues.length];
        for (int i = 0; i < count; i++) {
            holder.data.values[i] = newValues[i];
        }
        holder.data.millisecondsPerSample = msPerSample;

        sampleArrayDataWriter.write(holder.data, holder.handle);
    }

    protected InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder, int[] newValues,
            int count, int msPerSample, String metric_id) throws TimeoutException {
        return sampleArraySample(holder, newValues, count, msPerSample, metric_id, 0);
    }

    protected InstanceHolder<SampleArray> sampleArraySample(InstanceHolder<SampleArray> holder, Collection<Number> newValues,
           int msPerSample, String metric_id, int instance_id, Time timestamp) throws TimeoutException {
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
            int count, int msPerSample, String metric_id, int instance_id) throws TimeoutException {
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
                di.icon.raster = raster;
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


        if(null != alarmSettingsObjectiveSelector) {
            eventLoop.removeHandler(alarmSettingsObjectiveSelector.getCondition());
            alarmSettingsObjectiveSelector.getCondition().close();
            alarmSettingsObjectiveSelector = null;
        }

        alarmSettingsObjectiveReader.close();

        alarmSettingsObjectiveWriter.close();

        alarmSettingsObjectiveTopic.close();

        alarmSettingsDataWriter.close();
        alarmSettingsTopic.close();

        sampleArrayDataWriter.close();
        sampleArrayTopic.close();

        numericDataWriter.close();
        numericTopic.close();

        deviceIdentityWriter.close();
        deviceIdentityTopic.close();

        publisher.close();
        subscriber.close();

        domainParticipant.closeContainedEntities();

        domainParticipant.close();

        executor.shutdown();
        log.info("AbstractDevice shutdown complete");
    }

    public AbstractDevice(int domainId, EventLoop eventLoop) {
        ServiceEnvironment env = eventLoop.getServiceEnvironment();
        DomainParticipantFactory factory = DomainParticipantFactory.getInstance(eventLoop.getServiceEnvironment());

        Reliability r = PolicyFactory.getPolicyFactory(env).Reliability().withReliable();
        Durability  d = PolicyFactory.getPolicyFactory(env).Durability().withTransientLocal();
        Ownership o = PolicyFactory.getPolicyFactory(env).Ownership().withExclusive();
        Liveliness l = PolicyFactory.getPolicyFactory(env).Liveliness().withAutomatic().withLeaseDuration(2, TimeUnit.SECONDS);
        History h = PolicyFactory.getPolicyFactory(env).History().withKeepLast(10);



        domainParticipant = factory.createParticipant(domainId);

        publisher = domainParticipant.createPublisher();

        DataWriterQos dwQos = publisher.getDefaultDataWriterQos();
        publisher.setDefaultDataWriterQos(dwQos.withPolicies(r, d, o, l, h));
        subscriber = domainParticipant.createSubscriber();
        DataReaderQos drQos = subscriber.getDefaultDataReaderQos();
        subscriber.setDefaultDataReaderQos(drQos.withPolicies(r, d, o, l, h));

        deviceIdentityTopic = domainParticipant.createTopic(ice.DeviceIdentityTopic.value, ice.DeviceIdentity.class);
        deviceIdentityWriter = publisher.createDataWriter(deviceIdentityTopic);

        if (null == deviceIdentityWriter) {
            throw new RuntimeException("deviceIdentityWriter not created");
        }
        deviceIdentity = new DeviceIdentity();
        try {
            iconFromResource(deviceIdentity, iconResourceName());
        } catch (IOException e1) {
            log.warn("", e1);
        }

        numericTopic = domainParticipant.createTopic(ice.NumericTopic.value, ice.Numeric.class);
        numericDataWriter = publisher.createDataWriter(numericTopic);
        if (null == numericDataWriter) {
            throw new RuntimeException("numericDataWriter not created");
        }

        sampleArrayTopic = domainParticipant.createTopic(ice.SampleArrayTopic.value, ice.SampleArray.class);
        sampleArrayDataWriter = publisher.createDataWriter(sampleArrayTopic);
        if (null == sampleArrayDataWriter) {
            throw new RuntimeException("sampleArrayDataWriter not created");
        }
        alarmSettingsTopic = domainParticipant.createTopic(ice.AlarmSettingsTopic.value,  ice.AlarmSettings.class);
        alarmSettingsDataWriter = publisher.createDataWriter(alarmSettingsTopic);

        alarmSettingsObjectiveTopic = domainParticipant.createTopic(ice.LocalAlarmSettingsObjectiveTopic.value, ice.LocalAlarmSettingsObjective.class);
        alarmSettingsObjectiveWriter = publisher.createDataWriter(alarmSettingsObjectiveTopic);

        Topic<ice.GlobalAlarmSettingsObjective> alarmSettingsObjectiveTopic = domainParticipant.createTopic(ice.GlobalAlarmSettingsObjectiveTopic.value, ice.GlobalAlarmSettingsObjective.class);
//        TopicDescription<ice.GlobalAlarmSettingsObjective> alarmSettingsObjectiveTopic = TopicUtil.lookupOrCreateTopic(domainParticipant, ice.GlobalAlarmSettingsObjectiveTopic.value, ice.GlobalAlarmSettingsObjective.class);
        subscriber.createDataReader(alarmSettingsObjectiveTopic);

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

    private Map<InstanceHandle, String> instanceMetrics = new HashMap<InstanceHandle, String>();


    protected void writeDeviceIdentity() throws TimeoutException {
        if(null==deviceIdentity.unique_device_identifier||"".equals(deviceIdentity.unique_device_identifier)) {
            throw new IllegalStateException("cannot write deviceIdentity without a UDI");
        }

        DomainParticipantQos qos = domainParticipant.getQos();

        try {
            byte[] udiAscii = deviceIdentity.unique_device_identifier.getBytes("ASCII");
            qos.withPolicy(qos.getPolicyFactory().UserData().withValue(udiAscii, 0, udiAscii.length));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        domainParticipant.setQos(qos);

        if(null == deviceIdentityHandle) {
            deviceIdentityHandle = deviceIdentityWriter.registerInstance(deviceIdentity);
        }
        deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);

        if(null == alarmSettingsObjectiveSelector) {
            eventLoop.addHandler((alarmSettingsObjectiveSelector = alarmSettingsObjectiveReader.select().dataState(subscriber.createDataState().with(SampleState.NOT_READ).withAnyViewState().withAnyInstanceState())).getCondition(),
                    new ConditionHandler() {
                    @Override
                    public void conditionChanged(Condition condition) {
                        Sample.Iterator<ice.GlobalAlarmSettingsObjective> itr = alarmSettingsObjectiveReader.read(alarmSettingsObjectiveSelector);
                        try {

                            while(itr.hasNext()) {
                                Sample<ice.GlobalAlarmSettingsObjective> sample = itr.next();

                                if(ViewState.NEW.equals(sample.getViewState()) && sample.getData() != null) {
                                    log.warn("Handle for metric_id="+sample.getData().metric_id+" is " + sample.getInstanceHandle());
                                    instanceMetrics.put(sample.getInstanceHandle(), sample.getData().metric_id);
                                }

                                if(InstanceState.ALIVE.equals(sample.getInstanceState()) && sample.getData() != null) {
                                    log.warn("Setting " + sample.getData().metric_id + " to [ " + sample.getData().lower + " , " + sample.getData().upper + "]");
                                    setAlarmSettings(sample.getData());
                                } else {
                                    ice.GlobalAlarmSettingsObjective obj = new ice.GlobalAlarmSettingsObjective();
                                    log.warn("Unsetting handle " + sample.getInstanceHandle());
                                    // TODO 1-Oct-2013 JP This call to get_key_value fails consistently on ARM platforms
                                    // so I'm tracking instances externally for the time being
//                                    alarmSettingsObjectiveReader.get_key_value(obj, si.instance_handle);
                                    String metricId = instanceMetrics.get(sample.getInstanceHandle());
                                    log.warn("Unsetting " + metricId);
                                    if(null != metricId) {
                                        unsetAlarmSettings(metricId);
                                    }

                                }
                            }
                        } finally {
                            try {
                                itr.close();
                            } catch (IOException e) {
                                log.error("error closing iterator", e);
                            }
                        }
                    }
                });
        }
    }
}
