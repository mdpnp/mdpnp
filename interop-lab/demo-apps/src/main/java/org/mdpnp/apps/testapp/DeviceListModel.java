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

import static org.mdpnp.devices.TopicUtil.lookupOrCreateTopic;
import ice.DeviceConnectivity;
import ice.DeviceConnectivityDataReader;
import ice.DeviceConnectivitySeq;
import ice.DeviceConnectivityTypeSupport;
import ice.DeviceIdentity;
import ice.DeviceIdentityDataReader;
import ice.DeviceIdentitySeq;
import ice.DeviceIdentityTypeSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataDataReader;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataSeq;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataTypeSupport;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.RETCODE_PRECONDITION_NOT_MET;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusCondition;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.BuiltinTopicKey_t;
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
public class DeviceListModel extends AbstractListModel<Device> {
    
    private final static Pattern DEVICE_FILTER = Pattern.compile("Device"); 

    public Device getByUniqueDeviceIdentifier(String udi) {
        if(null == udi) {
            return null;
        }
        for(Device d : deviceByParticipantKey.values()) {
            if(udi.equals(d.getUDI())) {
                return d;
            }
        }
        return null;
    }
    
    private final Device getDevice(ParticipantBuiltinTopicData data, boolean create) {
        if(null == data) {
            return null;
        }
        Device device = contentsByParticipantKey.get(data.key);
        if(null == device && create) {
            device = deviceByParticipantKey.get(data.key);
            if(null == device) {
                device = new Device(data);
                deviceByParticipantKey.put(device.getParticipantData().key, device);
            }
            contentsByParticipantKey.put(device.getParticipantData().key, device);
            contents.add(0, device);
            contentsByIdx.clear();
            for (int i = 0; i < contents.size(); i++) {
                contentsByIdx.put(contents.get(i), i);
            }
            fireIntervalAdded(DeviceListModel.this, 0, 0);
        }
        return device;
    }
    
    private final void update(ParticipantBuiltinTopicData pbtd, boolean alive, boolean valid_data) {
        if (!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:" + Thread.currentThread());
        }
        
        if(alive) {
            if(pbtd.participant_name.name != null) {
                if(DEVICE_FILTER.matcher(pbtd.participant_name.name).matches()) {
                    if(valid_data) {
                        Device device = getDevice(pbtd, true);
                        device.setParticipantData(pbtd);
                        device.setDeviceIdentity(deviceIdentityByParticipantKey.get(pbtd.key));
                        device.setDeviceConnectivity(deviceConnectivityByParticipantKey.get(pbtd.key));
                        update(device);
                    } else {
                        log.debug("No valid data for " + pbtd.key);
                    }
                } else {
                    notADevice(pbtd, alive);
                }
            } else {
                log.debug("name of participant is null for " + pbtd.key);
            }
        } else {
            remove(getDevice(pbtd, false));
        }
    }
    
    private final void update(ParticipantBuiltinTopicData participantData, DeviceConnectivity dc) {
        if (!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:" + Thread.currentThread());
        }
        if(null != participantData) {
            if(deviceConnectivityByParticipantKey.containsKey(participantData.key)) {
                deviceConnectivityByParticipantKey.get(participantData.key).copy_from(dc);
            } else {
                deviceConnectivityByParticipantKey.put(new BuiltinTopicKey_t(participantData.key), new DeviceConnectivity(dc));
            }
        } else {
//            deviceConnectivityByParticipantKey.remove(participantData.key);
        }
        Device device = getDevice(participantData, false);
        if(null != device) {
            device.setDeviceConnectivity(dc);
            update(device);
        } else {
            device = getByUniqueDeviceIdentifier(dc.unique_device_identifier);
            if(null != device) {
                device.setDeviceConnectivity(dc);
                update(device);
            }
        }
    }

    private final void update(ParticipantBuiltinTopicData participantData, DeviceIdentity di) {
        if (!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:" + Thread.currentThread());
        }
        if(null != participantData) {
            if(deviceIdentityByParticipantKey.containsKey(participantData.key)) {
                deviceIdentityByParticipantKey.get(participantData.key).copy_from(di);
            } else {
                deviceIdentityByParticipantKey.put(new BuiltinTopicKey_t(participantData.key), new DeviceIdentity(di));
            }
        } else {
            log.debug("No ParticipantData available to store DeviceIdentity for:"+di.unique_device_identifier);
//            deviceIdentityByParticipantKey.remove(participantData.key);
        }
        Device device = getDevice(participantData, false);
        if(null != device) {
            device.setDeviceIdentity(di);
            update(device);
        } else {
            device = getByUniqueDeviceIdentifier(di.unique_device_identifier);
            if(null != device) {
                device.setDeviceIdentity(di);
                update(device);
            } else {
                log.warn("Unable to find Device by participantData="+participantData + " or UDI="+di.unique_device_identifier);
            }
        }
    }

    
    
    private final void remove(Device device) {
        if (!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:" + Thread.currentThread());
        }
        if(null == device) {
            return;
        }
        contentsByParticipantKey.remove(device.getParticipantData().key);
        final int idx = contentsByIdx.get(device);
        contents.remove(idx);
        contentsByIdx.clear();
        for (int i = 0; i < contents.size(); i++) {
            contentsByIdx.put(contents.get(i), i);
        }
        lastRemoved = device;
        fireIntervalRemoved(DeviceListModel.this, idx, idx);

        log.warn("Removed index=" + idx);
        lastRemoved = null;
    }
    
    private final boolean update(Device device) {
        if (!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:" + Thread.currentThread());
        }

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
    protected final Map<BuiltinTopicKey_t, Device> contentsByParticipantKey = new HashMap<BuiltinTopicKey_t, Device>();
    protected final Map<Device, Integer> contentsByIdx = new HashMap<Device, Integer>();
    
    protected final Map<BuiltinTopicKey_t, Device> deviceByParticipantKey = new HashMap<BuiltinTopicKey_t, Device>();
    protected final Map<BuiltinTopicKey_t, DeviceIdentity> deviceIdentityByParticipantKey = new HashMap<BuiltinTopicKey_t, DeviceIdentity>();
    protected final Map<BuiltinTopicKey_t, DeviceConnectivity> deviceConnectivityByParticipantKey = new HashMap<BuiltinTopicKey_t, DeviceConnectivity>();

    private final Subscriber subscriber;
    private final EventLoop eventLoop;
    protected ParticipantBuiltinTopicDataDataReader reader;
    protected ice.DeviceIdentityDataReader idReader;
    protected ice.DeviceConnectivityDataReader connReader;
    protected TopicDescription idTopic, connTopic;

    private final ThreadLocal<ParticipantBuiltinTopicData> participantData = new ThreadLocal<ParticipantBuiltinTopicData>() {
        protected ParticipantBuiltinTopicData initialValue() {
            return new ParticipantBuiltinTopicData();
        };  
    };
    
    private final void dataAvailable(ice.DeviceConnectivityDataReader reader) {
        ParticipantBuiltinTopicData participantData = this.participantData.get();
        try {
            for (;;) {
                try {
                    reader.take(conn_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.ANY_SAMPLE_STATE,
                            ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                    for (int i = 0; i < conn_seq.size(); i++) {
                        DeviceConnectivity dc = (DeviceConnectivity) conn_seq.get(i);
                        SampleInfo si = (SampleInfo) info_seq.get(i);

                        if(si.publication_handle.is_nil()) {
                            log.warn("publication_handle is nil");
                        }
                        try {
                            reader.get_matched_publication_participant_data(participantData, si.publication_handle);
                        } catch (RETCODE_PRECONDITION_NOT_MET preCondition) {
                            log.debug("Unable to get participant for publication", preCondition);
                            participantData = null;
                        }
                        
                        if (si.valid_data) {
                            update(participantData, dc);
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
        ParticipantBuiltinTopicData participantData = this.participantData.get();
        try {
            for (;;) {
                try {
                    reader.take(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.ANY_SAMPLE_STATE,
                            ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                    for (int i = 0; i < data_seq.size(); i++) {
                        DeviceIdentity di = (DeviceIdentity) data_seq.get(i);
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        if(si.publication_handle.is_nil()) {
                            log.warn("publication_handle is nil");
                        }
                        try {
                            reader.get_matched_publication_participant_data(participantData, si.publication_handle);
                        } catch (RETCODE_PRECONDITION_NOT_MET preCondition) {
                            log.debug("Unable to get participant for publication", preCondition);
                            participantData = null;
                        }
                        
                        if (si.valid_data) {
//                            log.debug("DeviceIdentity at " + si.source_timestamp + " " + di);
                            update(participantData, di);
                        }
                        
                    }
                } finally {
                    reader.return_loan(data_seq, info_seq);
                }
            }
        } catch (RETCODE_NO_DATA noData) {

        }
    }
    private final void dataAvailable(ParticipantBuiltinTopicDataDataReader reader) {
        try {
            for(;;) {
                try {
                    reader.take(part_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.ANY_SAMPLE_STATE,
                            ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                    for (int i = 0; i < part_seq.size(); i++) {
                        ParticipantBuiltinTopicData pbtd = (ParticipantBuiltinTopicData) part_seq.get(i);
                        SampleInfo si = (SampleInfo) info_seq.get(i);

                        boolean alive = 0 != (si.instance_state & InstanceStateKind.ALIVE_INSTANCE_STATE);
                        
                        if(!si.valid_data) {
                            pbtd = new ParticipantBuiltinTopicData();
                            reader.get_key_value(pbtd, si.instance_handle);
                        }
                        log.debug("Participant " + pbtd.key + " is " + (alive?"":"NOT ") + " alive");
                        update(pbtd, alive, si.valid_data);
                    }
                } finally {
                    reader.return_loan(part_seq, info_seq);
                }
            }
        } catch (RETCODE_NO_DATA noData) {
            
        }
    }

    final DeviceIdentitySeq data_seq = new DeviceIdentitySeq();
    final DeviceConnectivitySeq conn_seq = new DeviceConnectivitySeq();
    final ParticipantBuiltinTopicDataSeq part_seq = new ParticipantBuiltinTopicDataSeq();
    final SampleInfoSeq info_seq = new SampleInfoSeq();

    public DeviceListModel(final Subscriber subscriber, final EventLoop eventLoop) {
        this.eventLoop = eventLoop;
        this.subscriber = subscriber;
        eventLoop.doLater(new Runnable() {
            public void run() {
                DomainParticipant participant = subscriber.get_participant();

                idTopic = lookupOrCreateTopic(participant, ice.DeviceIdentityTopic.VALUE, DeviceIdentityTypeSupport.class);
                connTopic = lookupOrCreateTopic(participant, ice.DeviceConnectivityTopic.VALUE, DeviceConnectivityTypeSupport.class);

                reader = (ParticipantBuiltinTopicDataDataReader) participant.get_builtin_subscriber().lookup_datareader(
                        ParticipantBuiltinTopicDataTypeSupport.PARTICIPANT_TOPIC_NAME);
                idReader = (DeviceIdentityDataReader) subscriber.create_datareader_with_profile(idTopic, QosProfiles.ice_library,
                        QosProfiles.invariant_state, null, StatusKind.STATUS_MASK_NONE);

                connReader = (DeviceConnectivityDataReader) subscriber.create_datareader_with_profile(connTopic, QosProfiles.ice_library,
                        QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

                reader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
                idReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
                connReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);

                eventLoop.addHandler(reader.get_statuscondition(), new EventLoop.ConditionHandler() {

                    @Override
                    public void conditionChanged(Condition condition) {
                        ParticipantBuiltinTopicDataDataReader reader = (ParticipantBuiltinTopicDataDataReader) ((StatusCondition) condition)
                                .get_entity();
                        int status_changes = reader.get_status_changes();
                        if (0 != (status_changes & StatusKind.DATA_AVAILABLE_STATUS)) {
                            dataAvailable(reader);
                        }
                    }
                });

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
        });
    }

    protected void notADevice(ParticipantBuiltinTopicData participant_info, boolean alive) {
        log.debug("Remote participant " + participant_info.key + " is not a device and is " + (alive?"":"NOT ")+ "alive");
    }

    public Device getByParticipantKey(BuiltinTopicKey_t key) {
        if(null == key) {
            return null;
        }
        return contentsByParticipantKey.get(key);
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
                eventLoop.removeHandler(reader.get_statuscondition());
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
