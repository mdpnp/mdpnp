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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

import org.mdpnp.devices.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusCondition;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.TopicDescription;

@SuppressWarnings("serial")
public class DeviceListModel extends AbstractListModel<Device> {

    private static String fieldsToString(Object o) throws IllegalArgumentException, IllegalAccessException {
        StringBuilder sb = new StringBuilder("[");
        Field[] fields = o.getClass().getFields();
        if(fields.length > 0) {
            sb.append(fields[0].getName()).append("=").append(fields[0].get(o));
            for(int i = 1; i < fields.length; i++) {
                sb.append(",").append(fields[i].getName()).append("=").append(fields[i].get(o));
            }
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    private static String fieldsToStringSilent(Object o) {
        try {
            return fieldsToString(o);
        } catch (IllegalArgumentException e) {
            log.error("", e);
        } catch (IllegalAccessException e) {
            log.error("", e);
        }
        return null;
    }

    private final void update(DeviceConnectivity dc) {
        Device device = contentsByUDI.get(dc.universal_device_identifier);
        if(null != device) {
            device.setDeviceConnectivity(dc);
            Integer idx = contentsByIdx.get(device);
            if(null != idx) {
                fireContentsChanged(DeviceListModel.this, idx, idx);
            } else {
                log.warn("No index for device UDI="+dc.universal_device_identifier);
            }
        } else {
            log.warn("Tried to update non-existent device for connectivity with UDI="+dc.universal_device_identifier);
        }
    }
    
    private final void update(DeviceIdentity di) {
        Device device = contentsByUDI.get(di.universal_device_identifier);
        if(null != device) {
            device.getDeviceIdentity().copy_from(di);
            Integer idx = contentsByIdx.get(device);
            if(null != idx) {
                fireContentsChanged(DeviceListModel.this, idx, idx);
            } else {
                log.warn("No index for device UDI="+di.universal_device_identifier);
            }
        } else {
            log.warn("Tried to update non-existent device with UDI="+di.universal_device_identifier);
        }
    }
    
    private final void add(InstanceHandle_t handle, DeviceIdentity di) {
        if(contentsByHandle.containsKey(handle)) {
            log.warn("Attempt to re-add device with handle="+handle);
            update(di);
        } else if(contentsByUDI.containsKey(di.universal_device_identifier)) {
            log.warn("Attempt to re-add device with udi="+di.universal_device_identifier);
        } else {
            Device d = new Device(di);
            DeviceConnectivity dc = getConnectivityForUdi(d.getDeviceIdentity().universal_device_identifier);
            d.setDeviceConnectivity(dc);
            add(handle, d);
        }        
    }
    
    private final void add(final InstanceHandle_t handle, final Device d) {
        if(d == null) {
            throw new IllegalArgumentException("Tried to add a null device");
        }
        if("".equals(d.getDeviceIdentity().universal_device_identifier)) {
            throw new IllegalArgumentException("Tried to add a device with no UDI");
        }
        if(contentsByUDI.containsKey(d.getDeviceIdentity().universal_device_identifier)) {
            log.warn("Ignored attempt to re-add UDI="+d.getDeviceIdentity().universal_device_identifier);
        } else if(contentsByHandle.containsKey(handle)) {
            log.warn("Ignored attempt to re-add instance_handle="+handle);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        contents.add(0, d);
                        contentsByUDI.put(d.getDeviceIdentity().universal_device_identifier, d);
                        // The incoming instance handle is borrowed and will be returned
                        // so we need to make a copy
                        contentsByHandle.put(new InstanceHandle_t(handle), d);
                        log.trace("Added handle="+handle+" and hashCode="+handle.hashCode());
                        contentsByIdx.clear();
                        for(int i = 0; i < contents.size(); i++) {
                            contentsByIdx.put(contents.get(i), i);
                        }
                        fireIntervalAdded(DeviceListModel.this, 0, 0);
                    }
                });
            } catch (InvocationTargetException e) {
                log.error("added", e);
            } catch (InterruptedException e) {
                log.error("added", e);
            }
        }
    }
    
    private final void remove(final InstanceHandle_t handle) {
        if(null == handle) {
            throw new IllegalArgumentException("Tried to remove a null handle");
        }
        if(contentsByHandle.containsKey(handle)) {
            final Device device = contentsByHandle.get(handle);
            if(contentsByUDI.containsKey(device.getDeviceIdentity().universal_device_identifier)) {
                contentsByUDI.remove(device.getDeviceIdentity().universal_device_identifier);
            } else {
                log.warn("No UDI="+device.getDeviceIdentity().universal_device_identifier);
            }
            if(contentsByIdx.containsKey(device)) {
                
                final int idx = contentsByIdx.get(device);
                try {
                    // Cheap way to synchronize
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            contents.remove(idx);
                            contentsByIdx.clear();
                            for(int i = 0; i < contents.size(); i++) {
                                contentsByIdx.put(contents.get(i), i);
                            }
                            lastRemoved = device;
                            fireIntervalRemoved(DeviceListModel.this, idx, idx);
//                        fireIntervalRemoved(this, contents.size(), contents.size());
//                        fireContentsChanged(this, 0, contents.size() - 1);
                            log.warn("Removed index="+idx);
                            lastRemoved = null;                        
                        }
                    });
                } catch (InvocationTargetException e) {
                    log.error("removing", e);
                } catch (InterruptedException e) {
                    log.error("removing", e);
                }

            } else {
                log.warn("No index for handle="+handle);
            }
            contentsByHandle.remove(handle);
            log.info("Removed handle="+handle);
        } else {
            log.warn("Tried to remove non-existent handle="+handle+ " w/hashCode="+handle.hashCode()+" "+contentsByHandle.get(handle));
            for(InstanceHandle_t h : contentsByHandle.keySet()) {
                log.warn(h + " " + h.equals(handle) + " " + h.hashCode() + " " + handle.hashCode());
            }
        }
    }
    public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        InstanceHandle_t handle = new InstanceHandle_t();
        Field isValid = InstanceHandle_t.class.getDeclaredField("is_valid");
        isValid.setAccessible(true);
        isValid.setBoolean(handle, true);
        Field length = InstanceHandle_t.class.getDeclaredField("_length");
        length.setAccessible(true);
        length.setInt(handle, 16);
        
        byte[] theBytes = new byte[16];
        for(int i = 0; i < theBytes.length; i++) {
            theBytes[i] = (byte) i; 
        }
        Field value = InstanceHandle_t.class.getDeclaredField("_value");
        value.setAccessible(true);
        value.set(handle, theBytes);
        System.out.println(handle);
        InstanceHandle_t handle2 = new InstanceHandle_t(handle);
        Map<InstanceHandle_t, Integer> map = new HashMap<InstanceHandle_t, Integer>();
        map.put(handle, 10);
        System.out.println(map.containsKey(handle2));
        
    }
    
	private static final Logger log = LoggerFactory
			.getLogger(DeviceListModel.class);
	
	protected final List<Device> contents = new ArrayList<Device>();
	protected final Map<String, Device> contentsByUDI = new HashMap<String, Device>();
	protected final Map<InstanceHandle_t, Device> contentsByHandle = new HashMap<InstanceHandle_t, Device>();
	protected final Map<Device, Integer> contentsByIdx = new HashMap<Device, Integer>();
	
	private final Subscriber subscriber;
	private final EventLoop eventLoop;
	private final ice.DeviceIdentityDataReader reader;
	private TopicDescription topic, connTopic;
	private final ice.DeviceConnectivityDataReader connReader;
	
	DeviceConnectivity getConnectivityForUdi(String udi) {
	    DeviceConnectivity keyHolder = new DeviceConnectivity();
	    keyHolder.universal_device_identifier = udi;
	    
	    DeviceConnectivitySeq data_seq = new DeviceConnectivitySeq();
	    SampleInfoSeq info_seq = new SampleInfoSeq();
	    InstanceHandle_t handle = connReader.lookup_instance(keyHolder);
	    if(InstanceHandle_t.HANDLE_NIL.equals(handle)) {
	        return null;
	    }
	    boolean anySamples = false;
        try    {
	        connReader.read_instance(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, handle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE);
	        log.trace("read_instance for getConnectivityForUdi("+udi+")...");
	        for(int i = 0; i < info_seq.size(); i++) {
	            SampleInfo si = (SampleInfo) info_seq.get(i);
	            DeviceConnectivity dc = (DeviceConnectivity) data_seq.get(i);
//	            log.trace("SampleInfo="+fieldsToStringSilent(si)+" DeviceConnectivity="+dc);
    	        if(si.valid_data) {
    	            keyHolder.copy_from(dc);
    	            anySamples = true;
    	        }
	        }
	        if(anySamples) {
//	            log.trace("Found DeviceConnectivity for udi="+udi+" " + keyHolder);
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
   
	
	private final void sampleLost(ice.DeviceIdentityDataReader reader) {
	    SampleLostStatus sls = new SampleLostStatus();
	    reader.get_sample_lost_status(sls);
	    log.debug("SampleLost:"+sls);
	}
	
	private final void sampleRejected(ice.DeviceIdentityDataReader reader) {
	    SampleRejectedStatus srs = new SampleRejectedStatus();
	    reader.get_sample_rejected_status(srs);
	    log.debug("SampleRejected:"+srs);
	}
	
    private final void livelinessChanged(ice.DeviceIdentityDataReader reader) {
        LivelinessChangedStatus change_status = new LivelinessChangedStatus();
        reader.get_liveliness_changed_status(change_status);
        log.trace("liveliness changed:"+ change_status);
        // A DataWriter has moved from not alive to alive
        // I cannot figure out how to detect the relevant instance state change in on data available
        if(change_status.alive_count_change > 0 && change_status.not_alive_count_change < 0) {
            log.trace("A DataWriter has regained liveliness, iterate instances");
            InstanceHandle_t lastHandle = InstanceHandle_t.HANDLE_NIL;

            for(;;) {
                try {
                    reader.read_next_instance(data_seq, info_seq, 1, lastHandle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                    for(int i = 0; i < data_seq.size(); i++) {
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        ice.DeviceIdentity di = (DeviceIdentity) data_seq.get(i);
                        if(!si.valid_data) {
                            // Populate at least the key fields for debug messages
                            di = new DeviceIdentity();
                            reader.get_key_value(di, si.instance_handle);
                        }
                        log.trace("DeviceIdentity:"+di);
                        if(change_status.last_publication_handle.equals(si.publication_handle)) {
                            log.info("Resurrected:"+di);
                            add(si.instance_handle, di);
                        } 
                        lastHandle = si.instance_handle;
                        
                    }
                } catch(RETCODE_NO_DATA noData) {
                    break;
                } finally {
                    reader.return_loan(data_seq, info_seq);
                }
            }
        }
    }
   
	private final void dataAvailable(ice.DeviceIdentityDataReader reader) {
        try {
            log.trace("dataAvailable");
            reader.read(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
            for(int i = 0; i < data_seq.size(); i++) {
                DeviceIdentity di = (DeviceIdentity) data_seq.get(i);
                SampleInfo si = (SampleInfo) info_seq.get(i);

                if(!si.valid_data) {
                    // Populate at least the key fields for debug messages
                    di = new DeviceIdentity();
                    reader.get_key_value(di, si.instance_handle);
                }
                
//                log.trace("SampleState is " + (SampleStateKind.NOT_READ_SAMPLE_STATE==si.sample_state?"NOT ":"") + " read");
                
                if(0 != (ViewStateKind.NEW_VIEW_STATE & si.view_state)) {
                    if(si.valid_data) {
                        log.trace("View state is new, adding device " + di.universal_device_identifier + " with handle " + si.instance_handle + " w/hashCode="+si.instance_handle.hashCode());
                        add(si.instance_handle, di);
                    } else {
                        log.trace("View state is new, but no valid_data, adding device " + di.universal_device_identifier);
                        add(si.instance_handle, di);
                    }
                } else {
                    if(0 != (InstanceStateKind.NOT_ALIVE_INSTANCE_STATE & si.instance_state)) {
                        log.trace("Instance state is not alive, removing device " + di.universal_device_identifier);
                        remove(si.instance_handle);
                        
                        // Take samples for the instance out of the reader ...
//                        DeviceIdentitySeq temp_data_seq = new DeviceIdentitySeq();
//                        SampleInfoSeq temp_info_seq = new SampleInfoSeq();
//                        reader.take_instance(temp_data_seq, temp_info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, si.instance_handle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
//                        log.trace("Took " + temp_info_seq.size() + " samples from the reader");
//                        reader.return_loan(temp_data_seq, temp_info_seq);
                    } else if(0 != (InstanceStateKind.ALIVE_INSTANCE_STATE & si.instance_state)) {
                        
                        if(si.valid_data) {
                            log.trace("Instance state is alive, updating device " + di.universal_device_identifier);
                            update(di);
                        } else {
                            log.trace("Instance state is alive, but no sample valid_data, updating anyway " + di.universal_device_identifier);
                            update(di);
                        }
                        
                    }
                }
            }
        } catch (RETCODE_NO_DATA noData) {
            
        } finally {
            reader.return_loan(data_seq, info_seq);
        }
	}
	
    final DeviceIdentitySeq data_seq = new DeviceIdentitySeq();
    final DeviceConnectivitySeq conn_seq = new DeviceConnectivitySeq();
    final SampleInfoSeq info_seq = new SampleInfoSeq();
	
	public DeviceListModel(Subscriber subscriber, EventLoop eventLoop)	{
		this.subscriber = subscriber;
		this.eventLoop = eventLoop;
		DomainParticipant participant = subscriber.get_participant();
		topic = lookupOrCreateTopic(participant, ice.DeviceIdentityTopic.VALUE, DeviceIdentityTypeSupport.class);
		connTopic = lookupOrCreateTopic(participant, ice.DeviceConnectivityTopic.VALUE, DeviceConnectivityTypeSupport.class);
		
		reader = (DeviceIdentityDataReader) subscriber.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		connReader = (DeviceConnectivityDataReader) subscriber.create_datareader(connTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

		reader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS | StatusKind.LIVELINESS_CHANGED_STATUS | StatusKind.SAMPLE_LOST_STATUS | StatusKind.SAMPLE_REJECTED_STATUS);
		connReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
		
		eventLoop.addHandler(reader.get_statuscondition(), new EventLoop.ConditionHandler() {
	            @Override
	            public void conditionChanged(Condition condition) {
	                ice.DeviceIdentityDataReader reader = (DeviceIdentityDataReader) ((StatusCondition)condition).get_entity();
	                int status_changes = reader.get_status_changes();
	                if(0 != (status_changes & StatusKind.DATA_AVAILABLE_STATUS)) {
	                    dataAvailable(reader);
	                }
	                if(0 != (status_changes & StatusKind.LIVELINESS_CHANGED_STATUS)) {
	                    livelinessChanged(reader);
	                }
	                if(0 != (status_changes & StatusKind.SAMPLE_LOST_STATUS)) {
	                    sampleLost(reader);
	                }
	                if(0 != (status_changes & StatusKind.SAMPLE_REJECTED_STATUS)) {
	                    sampleRejected(reader);
	                }
	            }
	            
	        });
		eventLoop.addHandler(connReader.get_statuscondition(), new EventLoop.ConditionHandler() {
            
            @Override
            public void conditionChanged(Condition condition) {
                ice.DeviceConnectivityDataReader reader = (ice.DeviceConnectivityDataReader) ((StatusCondition)condition).get_entity();
                dataAvailable(reader);
            }
        });
	}
	
	protected int indexOf(InstanceHandle_t handle) {
        for(int i = 0; i < contents.size(); i++) {
            if(handle.equals(reader.lookup_instance(contents.get(i).getDeviceIdentity()))) {
                return i;
            } 
        }
        return -1;
	}
	
	/**
	 * Not re-entrant.  Call from same EventLoop thread
	 * @param udi
	 * @return
	 */
	public Device getByUniversalDeviceIdentifier(String udi) {
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
	    eventLoop.removeHandler(reader.get_statuscondition());
	    eventLoop.removeHandler(connReader.get_statuscondition());
	    reader.delete_contained_entities();
	    subscriber.delete_datareader(reader);
	    connReader.delete_contained_entities();
	    subscriber.delete_datareader(connReader);
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
