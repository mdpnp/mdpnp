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
package org.mdpnp.apps.testapp;

import static org.mdpnp.rtiapi.data.TopicUtil.lookupOrCreateTopic;
import ice.DeviceConnectivity;
import ice.DeviceConnectivityDataReader;
import ice.DeviceConnectivitySeq;
import ice.DeviceConnectivityTypeSupport;
import ice.DeviceIdentity;
import ice.DeviceIdentityDataReader;
import ice.DeviceIdentitySeq;
import ice.DeviceIdentityTypeSupport;
import ice.HeartBeat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

import org.mdpnp.devices.TimeManager;
import org.mdpnp.devices.TimeManagerListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusCondition;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.TopicDescription;

@SuppressWarnings("serial")
/**
 * A data model tracking all active participants; joining participant info, device
 * identity, and device connectivity information.
 * 
 * Joining between these topics turns out to be complicated.  Especially because previous
 * instances of DeviceIdentity and DeviceConnectivity do not generate a new ALIVE notification
 * when connection to a remote participant is re-established.  Devices continually re-publishing
 * this information would add even more bandwidth consumption over and above participant assertion
 * @author Jeff Plourde 
 *
 */
public class DeviceListModel extends AbstractListModel<Device> implements TimeManagerListener {
    
    public Device getByUniqueDeviceIdentifier(String udi) {
        if(null == udi) {
            return null;
        }
        return contentsByUDI.get(udi);
    }
    
    private final Device getDevice(String udi, boolean create) {
        if(null == udi) {
            log.warn("Cannot create device with null udi");
            return null;
        }
        Device device = contentsByUDI.get(udi);
        if(null == device) {
            if(create) {
                device = new Device(udi);
                IdentityAndParticipant iandp = deviceIdentityByUDI.get(udi);
                device.setDeviceIdentity(null==iandp?null:iandp.deviceIdentity, null==iandp?null:iandp.participantData);
                device.setDeviceConnectivity(deviceConnectivityByUDI.get(udi));
                contentsByUDI.put(udi, device);
                contents.add(0, device);
                contentsByIdx.clear();
                for (int i = 0; i < contents.size(); i++) {
                    contentsByIdx.put(contents.get(i), i);
                }
                fireIntervalAdded(this, 0, 0);
                log.debug("Added index=" + 0 + " " + device.getUDI() + " for a total size of " + getSize());
                // TODO This shouldn't strictly be necessary by the JList is not responding in all cases
                fireContentsChanged(this, 0, getSize()-1);
            } else {
                log.debug("Not creating unfounded device udi="+udi);
            }
        } else {
            int idx = contentsByIdx.get(device);
            log.debug("At idx="+idx+" find udi="+device.getUDI());
            fireContentsChanged(this, idx, idx);
        }
        return device;
    }
    
    protected void notADevice(ice.HeartBeat heartbeat, boolean alive) {
        
    }
    
    @Override
    public void aliveHeartbeat(SampleInfo sampleInfo, HeartBeat heartbeat) {
        if("Device".equals(heartbeat.type)) {
            log.trace(heartbeat.unique_device_identifier + " IS STILL ALIVE");
            getDevice(heartbeat.unique_device_identifier, true);
        } else {
            notADevice(heartbeat, true);
        }
    }
    
    @Override
    public void notAliveHeartbeat(SampleInfo sampleInfo, HeartBeat heartbeat) {
        if("Device".equals(heartbeat.type)) {
            log.debug(heartbeat.unique_device_identifier + " IS NO LONGER ALIVE");
            remove(getDevice(heartbeat.unique_device_identifier, false));
        } else {
            notADevice(heartbeat, false);
        }

    }
    
    @Override
    public void synchronization(String remote_udi, Duration_t latency, Duration_t clockDifference) {
        log.trace(remote_udi + " has latency="+latency+" and clockDifference="+clockDifference);
        Device device = getDevice(remote_udi, false);
        if(null != device) {
            device.setClockDifference(clockDifference);
            device.setRoundtripLatency(latency);
            update(device);
        }
    }
    
    private final void update(DeviceConnectivity dc) {
        if (!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:" + Thread.currentThread());
        }
        if(deviceConnectivityByUDI.containsKey(dc.unique_device_identifier)) {
            deviceConnectivityByUDI.get(dc.unique_device_identifier).copy_from(dc);
        } else {
            deviceConnectivityByUDI.put(dc.unique_device_identifier, new DeviceConnectivity(dc));
        }
        Device device = getDevice(dc.unique_device_identifier, false);
        if(null != device) {
            device.setDeviceConnectivity(dc);
            update(device);
        }
    }

    private final void update(DeviceIdentity di, ParticipantBuiltinTopicData data) {
        
        if (!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:" + Thread.currentThread());
        }
        if(deviceIdentityByUDI.containsKey(di.unique_device_identifier)) {
            IdentityAndParticipant iandp = deviceIdentityByUDI.get(di.unique_device_identifier); 
            iandp.deviceIdentity.copy_from(di);
            iandp.participantData.copy_from(data);
        } else {
            IdentityAndParticipant iandp = new IdentityAndParticipant(); 
            iandp.deviceIdentity.copy_from(di);
            iandp.participantData.copy_from(data);
            deviceIdentityByUDI.put(di.unique_device_identifier, iandp);
        }
        Device device = getDevice(di.unique_device_identifier, false);
        if(null != device) {
            device.setDeviceIdentity(di, data);
            update(device);
        }
    }

    
    
    private final void remove(Device device) {
        if(null == device) {
            log.debug("Tried to remove a null device");
            return;
        }
        if(null == contentsByUDI.remove(device.getUDI())) {
            log.warn("Attempting to remove a device not present in contentsByUDI " + device.getUDI());
        }
        final int idx = contentsByIdx.get(device);
        contents.remove(idx);
        contentsByIdx.clear();
        for (int i = 0; i < contents.size(); i++) {
            contentsByIdx.put(contents.get(i), i);
        }
        lastRemoved = device;
        fireIntervalRemoved(DeviceListModel.this, idx, idx);

        log.debug("Removed index=" + idx + " " + device.getUDI());
        lastRemoved = null;
    }
    
    private final boolean update(Device device) {
        final int idx = contentsByIdx.get(device);
        if(idx >= 0) {
            fireContentsChanged(DeviceListModel.this, idx, idx);
            return true;
        } else {
            return false;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(DeviceListModel.class);

    protected final List<Device> contents = new ArrayList<Device>();
    protected final Map<Device, Integer> contentsByIdx = new HashMap<Device, Integer>();
    
    protected final Map<String, Device> contentsByUDI = new java.util.concurrent.ConcurrentHashMap<String, Device>();
    private static final class IdentityAndParticipant {
        public final ParticipantBuiltinTopicData participantData = new ParticipantBuiltinTopicData();
        public final DeviceIdentity deviceIdentity = new DeviceIdentity();
    }
    
    protected final Map<String, IdentityAndParticipant> deviceIdentityByUDI = new java.util.concurrent.ConcurrentHashMap<String, IdentityAndParticipant>();
    protected final Map<String, DeviceConnectivity> deviceConnectivityByUDI = new java.util.concurrent.ConcurrentHashMap<String, DeviceConnectivity>();

    private final Subscriber subscriber;
    private final EventLoop eventLoop;
    protected ice.DeviceIdentityDataReader idReader;
    protected ice.DeviceConnectivityDataReader connReader;
    protected TopicDescription idTopic, connTopic;
    protected final TimeManager timeManager;

    private final void dataAvailable(ice.DeviceConnectivityDataReader reader) {
        try {
            for (;;) {
                try {
                    reader.read(conn_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                    for (int i = 0; i < conn_seq.size(); i++) {
                        DeviceConnectivity dc = (DeviceConnectivity) conn_seq.get(i);
                        SampleInfo si = (SampleInfo) info_seq.get(i);

                        if(si.publication_handle.is_nil()) {
                            log.warn("publication_handle is nil");
                        }
                        if (si.valid_data) {
                            update(dc);
                        }
                        
                    }
                } finally {
                    reader.return_loan(conn_seq, info_seq);
                }
            }
        } catch (RETCODE_NO_DATA noData) {

        } finally {

        }
    }

    private final void dataAvailable(ice.DeviceIdentityDataReader reader) {
        try {
            for (;;) {
                try {
                    reader.read(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                    for (int i = 0; i < data_seq.size(); i++) {
                        DeviceIdentity di = (DeviceIdentity) data_seq.get(i);
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        if(si.publication_handle.is_nil()) {
                            log.warn("publication_handle is nil");
                        }

                        if (si.valid_data) {
                            ParticipantBuiltinTopicData data = null;
                            try {
                                data = new ParticipantBuiltinTopicData();
                                reader.get_matched_publication_participant_data(data, si.publication_handle);
                            } catch(Exception e) {
                                log.warn("Unable to get participant information for DeviceIdentity publication");
                            }
//                            log.debug("DeviceIdentity at " + si.source_timestamp + " " + di);
                            update(di, data);
                        }
                        
                    }
                } finally {
                    reader.return_loan(data_seq, info_seq);
                }
            }
        } catch (RETCODE_NO_DATA noData) {

        }
    }

    final DeviceIdentitySeq data_seq = new DeviceIdentitySeq();
    final DeviceConnectivitySeq conn_seq = new DeviceConnectivitySeq();
    final SampleInfoSeq info_seq = new SampleInfoSeq();

    public void start() {
        DomainParticipant participant = subscriber.get_participant();

        idTopic = lookupOrCreateTopic(participant, ice.DeviceIdentityTopic.VALUE, DeviceIdentityTypeSupport.class);
        connTopic = lookupOrCreateTopic(participant, ice.DeviceConnectivityTopic.VALUE, DeviceConnectivityTypeSupport.class);

        idReader = (DeviceIdentityDataReader) subscriber.create_datareader_with_profile(idTopic, QosProfiles.ice_library,
                QosProfiles.device_identity, null, StatusKind.STATUS_MASK_NONE);

        connReader = (DeviceConnectivityDataReader) subscriber.create_datareader_with_profile(connTopic, QosProfiles.ice_library,
                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        idReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
        connReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);

        eventLoop.addHandler(idReader.get_statuscondition(), new EventLoop.ConditionHandler() {
            @Override
            public void conditionChanged(Condition condition) {
                ice.DeviceIdentityDataReader reader = (DeviceIdentityDataReader) ((StatusCondition) condition).get_entity();
                int status_changes = reader.get_status_changes();
                if (0 != (status_changes & StatusKind.DATA_AVAILABLE_STATUS)) {
                    dataAvailable(reader);
                }
            }

        });
        eventLoop.addHandler(connReader.get_statuscondition(), new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                ice.DeviceConnectivityDataReader reader = (ice.DeviceConnectivityDataReader) ((StatusCondition) condition).get_entity();
                int status_changes = reader.get_status_changes();
                if (0 != (status_changes & StatusKind.DATA_AVAILABLE_STATUS)) {
                    dataAvailable(reader);
                }
            }
        });
    }
    
    public DeviceListModel(final Subscriber subscriber, final EventLoop eventLoop, final TimeManager timeManager) {
        this.eventLoop = eventLoop;
        this.subscriber = subscriber;
        this.timeManager = timeManager;
        timeManager.addListener(this);
    }

    private Device lastRemoved;

    public Device getLastRemoved() {
        return lastRemoved;
    }

    public void tearDown() {
        // TODO Tear down the topics? IF created? How do we interact with others
        // in this PArticipant
        eventLoop.doLater(new Runnable() {
            public void run() {
                eventLoop.removeHandler(idReader.get_statuscondition());
                eventLoop.removeHandler(connReader.get_statuscondition());
                idReader.delete_contained_entities();
                subscriber.delete_datareader(idReader);
                connReader.delete_contained_entities();
                subscriber.delete_datareader(connReader);
            }
        });

    }

    @Override
    public int getSize() {
        return contents.size();
    }

    @Override
    public Device getElementAt(int index) {
        return contents.get(index);
    }
}
