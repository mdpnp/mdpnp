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

import javax.swing.AbstractListModel;

import org.mdpnp.devices.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataDataReader;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataSeq;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataTypeSupport;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.InstanceHandle_t;
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
public class DeviceListModel extends AbstractListModel<Device> {

    private final void update(DeviceConnectivity dc) {
        if(!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:"+Thread.currentThread());
        }
        Device device = contentsByUDI.get(dc.unique_device_identifier);
        if(null != device) {
            device.setDeviceConnectivity(dc);
            Integer idx = contentsByIdx.get(device);
            if(null != idx) {
                fireContentsChanged(DeviceListModel.this, idx, idx);
            } else {
                log.warn("No index for device UDI="+dc.unique_device_identifier);
            }
        } else {
            log.warn("Tried to update non-existent device for connectivity with UDI="+dc.unique_device_identifier);
        }
    }

    private final void update(DeviceIdentity di) {
        if(!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:"+Thread.currentThread());
        }
        Device device = contentsByUDI.get(di.unique_device_identifier);
        if(null != device) {
            device.setDeviceIdentity(di);
            Integer idx = contentsByIdx.get(device);
            if(null != idx) {
                fireContentsChanged(DeviceListModel.this, idx, idx);
            } else {
                log.warn("No index for device identity UDI="+di.unique_device_identifier);
            }
        } else {
            log.warn("Tried to update non-existent device for identity with UDI="+di.unique_device_identifier);
        }
    }

    private final void addOrUpdate(InstanceHandle_t handle, ParticipantBuiltinTopicData pbtd) {
        if(!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:"+Thread.currentThread());
        }
        Device device = contentsByHandle.get(handle);
        if(null == device) {
            device = new Device(pbtd);
            contentsByHandle.put(new InstanceHandle_t(handle), device);
            contents.add(0, device);
            contentsByIdx.clear();
            for(int i = 0; i < contents.size(); i++) {
                contentsByIdx.put(contents.get(i), i);
            }
            String udi = device.getUDI();
            // Check for Identity and Connectivity in the case that it arrived before the discovery message
            if(null != udi && !"".equals(udi)) {
                contentsByUDI.put(udi, device);
                DeviceConnectivity dc = getConnectivityForUdi(udi);
                DeviceIdentity di = getIdentityForUdi(udi);
                device.setDeviceConnectivity(dc);
                device.setDeviceIdentity(di);
            }
            fireIntervalAdded(DeviceListModel.this, 0, 0);
        } else {
            device.setParticipantData(pbtd);
            String udi = device.getUDI();
            if(null != udi && !"".equals(udi) && !contentsByUDI.containsKey(udi)) {
                contentsByUDI.put(udi, device);
                DeviceConnectivity dc = getConnectivityForUdi(udi);
                DeviceIdentity di = getIdentityForUdi(udi);
                device.setDeviceConnectivity(dc);
                device.setDeviceIdentity(di);
            }
            int idx = contentsByIdx.get(device);
            fireContentsChanged(DeviceListModel.this, idx, idx);
        }

    }

    private final void remove(final InstanceHandle_t handle) {
        if(!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:"+Thread.currentThread());
        }
        if(null == handle) {
            throw new IllegalArgumentException("Tried to remove a null handle");
        }
        Device device = contentsByHandle.remove(handle);
        if(null != device) {
            String udi = device.getUDI();
            if(null != udi && !"".equals(udi)) {
                if(null == contentsByUDI.remove(udi)) {
                    log.warn("No UDI="+device.getUDI());
                }
            }

            final int idx = contentsByIdx.get(device);
            contents.remove(idx);
            contentsByIdx.clear();
            for(int i = 0; i < contents.size(); i++) {
                contentsByIdx.put(contents.get(i), i);
            }
            lastRemoved = device;
            fireIntervalRemoved(DeviceListModel.this, idx, idx);

            log.warn("Removed index="+idx);
            lastRemoved = null;
        } else {
            log.warn("Tried to remove non-existent handle="+handle+ " w/hashCode="+handle.hashCode()+" "+contentsByHandle.get(handle));
            for(InstanceHandle_t h : contentsByHandle.keySet()) {
                log.warn(h + " " + h.equals(handle) + " " + h.hashCode() + " " + handle.hashCode());
            }
        }
    }

    private static final Logger log = LoggerFactory
            .getLogger(DeviceListModel.class);

    protected final List<Device> contents = new ArrayList<Device>();
    protected final Map<String, Device> contentsByUDI = new HashMap<String, Device>();
    protected final Map<InstanceHandle_t, Device> contentsByHandle = new HashMap<InstanceHandle_t, Device>();
    protected final Map<Device, Integer> contentsByIdx = new HashMap<Device, Integer>();

    private final Subscriber subscriber;
    private final EventLoop eventLoop;
    protected ParticipantBuiltinTopicDataDataReader reader;
    protected ice.DeviceIdentityDataReader idReader;
    protected ice.DeviceConnectivityDataReader connReader;
    protected TopicDescription idTopic, connTopic;


    DeviceIdentity getIdentityForUdi(String udi) {
        DeviceIdentity keyHolder = new DeviceIdentity();
        keyHolder.unique_device_identifier = udi;

        DeviceIdentitySeq data_seq = new DeviceIdentitySeq();
        SampleInfoSeq info_seq = new SampleInfoSeq();
        InstanceHandle_t handle = idReader.lookup_instance(keyHolder);
        if(InstanceHandle_t.HANDLE_NIL.equals(handle)) {
            return null;
        }

        try    {
            idReader.read_instance(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, handle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
            log.trace("read_instance for getIdentityForUdi("+udi+")...");
            boolean found = false;
            for(int i = 0; i < info_seq.size(); i++) {
                SampleInfo si = (SampleInfo) info_seq.get(i);
                DeviceIdentity dc = (DeviceIdentity) data_seq.get(i);
                if(si.valid_data) {
                    keyHolder.copy_from(dc);
                    found = true;
                }
            }
            if(found) {
                return keyHolder;
            }
        } catch (RETCODE_NO_DATA noData) {

        } finally {
            idReader.return_loan(data_seq, info_seq);
        }
        log.trace("No DeviceConnectivity data for udi="+udi);
        return null;
    }

    DeviceConnectivity getConnectivityForUdi(String udi) {
        DeviceConnectivity keyHolder = new DeviceConnectivity();
        keyHolder.unique_device_identifier = udi;

        DeviceConnectivitySeq data_seq = new DeviceConnectivitySeq();
        SampleInfoSeq info_seq = new SampleInfoSeq();
        InstanceHandle_t handle = connReader.lookup_instance(keyHolder);
        if(InstanceHandle_t.HANDLE_NIL.equals(handle)) {
            return null;
        }

        try    {
            connReader.read_instance(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, handle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
            log.trace("read_instance for getConnectivityForUdi("+udi+")...");
            boolean found = false;
            for(int i = 0; i < info_seq.size(); i++) {
                SampleInfo si = (SampleInfo) info_seq.get(i);
                DeviceConnectivity dc = (DeviceConnectivity) data_seq.get(i);
                if(si.valid_data) {
                    keyHolder.copy_from(dc);
                    found = true;
                }
            }
            if(found) {
                return keyHolder;
            }
        } catch (RETCODE_NO_DATA noData) {

        } finally {
            connReader.return_loan(data_seq, info_seq);
        }
        log.trace("No DeviceConnectivity data for udi="+udi);
        return null;
    }

    private final void dataAvailable(ice.DeviceConnectivityDataReader reader) {
        try {
            while(true) {
                try {
                    reader.read(conn_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE);
//                    log.trace("read for dataAvailable(DeviceConnectivityDataReader");
                    for(int i = 0; i < conn_seq.size(); i++) {
                        DeviceConnectivity dc = (DeviceConnectivity) conn_seq.get(i);
                        SampleInfo si = (SampleInfo) info_seq.get(i);

                        if(si.valid_data && 0 != (si.instance_state & InstanceStateKind.ALIVE_INSTANCE_STATE)) {
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
            reader.read(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
            for(int i = 0; i < data_seq.size(); i++) {
                DeviceIdentity di = (DeviceIdentity) data_seq.get(i);
                SampleInfo si = (SampleInfo) info_seq.get(i);

                if(si.valid_data) {
                    update(di);
                }
            }
        } catch (RETCODE_NO_DATA noData) {

        } finally {
            reader.return_loan(data_seq, info_seq);
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

                reader = (ParticipantBuiltinTopicDataDataReader) participant.get_builtin_subscriber().lookup_datareader(ParticipantBuiltinTopicDataTypeSupport.PARTICIPANT_TOPIC_NAME);
                idReader = (DeviceIdentityDataReader) subscriber.create_datareader(idTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
                connReader = (DeviceConnectivityDataReader) subscriber.create_datareader(connTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

                reader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
                idReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
                connReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);

                eventLoop.addHandler(reader.get_statuscondition(), new EventLoop.ConditionHandler() {

                    @Override
                    public void conditionChanged(Condition condition) {
                        ParticipantBuiltinTopicDataDataReader reader = (ParticipantBuiltinTopicDataDataReader) ((StatusCondition)condition).get_entity();
                        int status_changes = reader.get_status_changes();
                        if(0 != (status_changes & StatusKind.DATA_AVAILABLE_STATUS)) {
                            dataAvailable(reader);
                        }
                    }
                });

                eventLoop.addHandler(idReader.get_statuscondition(), new EventLoop.ConditionHandler() {
                        @Override
                        public void conditionChanged(Condition condition) {
                            ice.DeviceIdentityDataReader reader = (DeviceIdentityDataReader) ((StatusCondition)condition).get_entity();
                            int status_changes = reader.get_status_changes();
                            if(0 != (status_changes & StatusKind.DATA_AVAILABLE_STATUS)) {
                                dataAvailable(reader);
                            }
                        }

                    });
                eventLoop.addHandler(connReader.get_statuscondition(), new EventLoop.ConditionHandler() {

                    @Override
                    public void conditionChanged(Condition condition) {
                        ice.DeviceConnectivityDataReader reader = (ice.DeviceConnectivityDataReader) ((StatusCondition)condition).get_entity();
                        int status_changes = reader.get_status_changes();
                        if(0 != (status_changes & StatusKind.DATA_AVAILABLE_STATUS)) {
                            dataAvailable(reader);
                        }
                    }
                });


                InstanceHandle_t previousInstance = InstanceHandle_t.HANDLE_NIL;
                // Iterate to ensure we didn't miss any previously discovered instances
                int i = 0;
                for(;;) {
                    try {
                        reader.read_next_instance(part_seq, info_seq, 1, previousInstance, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                        ParticipantBuiltinTopicData data = (ParticipantBuiltinTopicData) part_seq.get(0);
                        SampleInfo si = (SampleInfo) info_seq.get(0);
                        i++;
                        log.trace("This InstanceHandle was already in the reader! "+si.instance_handle);
                        seeAnInstance(data, si);

                        previousInstance = si.instance_handle;
                    } catch (RETCODE_NO_DATA noData) {
                        log.trace("Iterated over " + i + " instances");
                        return;
                    } finally {
                        reader.return_loan(part_seq, info_seq);
                    }
                }
            }
        });
    }

    protected void notADevice(SampleInfo si, ParticipantBuiltinTopicData participant_info) {

    }

    private final void seeAnInstance(ParticipantBuiltinTopicData pbtd, SampleInfo si) {
        if(0 != (si.instance_state & InstanceStateKind.ALIVE_INSTANCE_STATE)) {
            if(si.valid_data) {
                // TODO temporarily filtering to only Device participants
                if("Device".equals(pbtd.participant_name.name)) {
                    addOrUpdate(si.instance_handle, pbtd);
                } else {
                    notADevice(si, pbtd);
                    remove(si.instance_handle);
                }
            } else {
                remove(si.instance_handle);
            }
         } else {
            remove(si.instance_handle);
         }
    }

    private final void dataAvailable(ParticipantBuiltinTopicDataDataReader reader) {
        try {
            reader.read(part_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
            for(int i = 0; i < part_seq.size(); i++) {
                ParticipantBuiltinTopicData pbtd = (ParticipantBuiltinTopicData) part_seq.get(i);
                SampleInfo si = (SampleInfo) info_seq.get(i);

                seeAnInstance(pbtd, si);
            }
        } catch (RETCODE_NO_DATA noData) {

        } finally {
            reader.return_loan(part_seq, info_seq);
        }
    }

    /**
     * Not re-entrant.  Call from same EventLoop thread
     * @param udi
     * @return
     */
    public Device getByUniqueDeviceIdentifier(String udi) {
        if(null == udi) {
            return null;
        }
        return contentsByUDI.get(udi);
    }

    private Device lastRemoved;

    public Device getLastRemoved() {
        return lastRemoved;
    }

    public void tearDown() {
        // TODO Tear down the topics?  IF created?  How do we interact with others in this PArticipant
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
