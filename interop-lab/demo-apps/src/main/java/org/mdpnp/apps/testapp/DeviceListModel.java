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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

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
    
	private static final Logger log = LoggerFactory
			.getLogger(DeviceListModel.class);
	
	private final List<Device> contents = new ArrayList<Device>();
	private final Map<String, Device> contentsMap = new HashMap<String, Device>();
	
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
	            log.trace("SampleInfo="+fieldsToStringSilent(si)+" DeviceConnectivity="+dc);
    	        if(si.valid_data) {
    	            keyHolder.copy_from(data_seq.get(i));
    	            anySamples = true;
    	        }
	        }
	        if(anySamples) {
	            log.trace("Found DeviceConnectivity for udi="+udi+" " + keyHolder);
                return keyHolder; 
	        }
	    } catch (RETCODE_NO_DATA noData) {
	        
	    } finally {
	        connReader.return_loan(data_seq, info_seq);
	    }
        log.trace("No DeviceConnectivity data for udi="+udi);
        return null;
	}
	
	private final Device get(DeviceIdentity keyHolder, int[] cur_idx, boolean create) {
	       Device d = null;
	        for(int j = 0; j < contents.size(); j++) {
	            d = contents.get(j);
	            if(keyHolder.universal_device_identifier.equals(d.getDeviceIdentity().universal_device_identifier)) {
	                cur_idx[0] = j;
	                return d;
	            } 
	        }
	        if(create) {
	            
	            d = new Device(keyHolder);
	            contents.add(0, d);
	            contentsMap.put(d.getDeviceIdentity().universal_device_identifier, d);
	            fireIntervalAdded(this, 0, 0);
	            cur_idx[0] = 0;
	            log.trace("Created Device for udi="+d.getDeviceIdentity().universal_device_identifier);
	            DeviceConnectivity dc = getConnectivityForUdi(d.getDeviceIdentity().universal_device_identifier);
	            log.trace("Preexisting DeviceConnectivity="+dc);
	            d.setDeviceConnectivity(dc);
	            return d;
	        } else {
	            cur_idx[0] = -1;
	            return null;
	        }

	}
	
	
	private final Device get(InstanceHandle_t handle, int[] cur_idx, boolean create, DeviceIdentity[] keyHolder_) {
	    DeviceIdentity keyHolder = new DeviceIdentity();
        reader.get_key_value(keyHolder, handle);
	    
        if(null != keyHolder_) {
            keyHolder_[0] = keyHolder;
        }
        
        return get(keyHolder, cur_idx, create);
        
	}
	
	private final Device get(String udi, int[] cur_idx, boolean create) {
	    DeviceIdentity di = new DeviceIdentity();
	    di.universal_device_identifier = udi;
	    return get(di, cur_idx, create);
	}
	
   private final void dataAvailable(ice.DeviceConnectivityDataReader reader) {
        try {
            while(true) {
                try {
                    reader.read(conn_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE);
                    log.trace("read for dataAvailable(DeviceConnectivityDataReader");
                    for(int i = 0; i < conn_seq.size(); i++) {
                        DeviceConnectivity di = (DeviceConnectivity) conn_seq.get(i);
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        
                        log.trace("SampleInfo="+fieldsToStringSilent(si)+" DeviceConnectivity="+di);
                        
                        Device current = get(di.universal_device_identifier, cur_idx, false);
        
                        
                        if(si.valid_data) {
                            if(null != current) {
                                log.trace("Sample connectivity for existing device "+di.universal_device_identifier);
                                current.setDeviceConnectivity(di);
                                fireContentsChanged(DeviceListModel.this, cur_idx[0], cur_idx[0]);
                            } else {
                                // will pull from the reader
//                                orphanedConnectivity.put(di.universal_device_identifier, new DeviceConnectivity(di));
                            }
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
            while(true) {
                try {
                    reader.read(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE);
                    log.trace("read for dataAvailable(DeviceIdentityDataReader)...");
                    for(int i = 0; i < data_seq.size(); i++) {
                        DeviceIdentity di = (DeviceIdentity) data_seq.get(i);
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        log.trace("SampleInfo="+fieldsToStringSilent(si)+" DeviceIdentity="+di);
                        Device current = get(si.instance_handle, cur_idx, true, null);
        
                        if(si.valid_data) {
                            log.trace("Sample for existing device "+di.universal_device_identifier+" was " +current.getDeviceIdentity() + " will be " + di);
                            current.getDeviceIdentity().copy_from(di);
                            fireContentsChanged(DeviceListModel.this, cur_idx[0], cur_idx[0]);
                        }
                    }
                } finally {
                    reader.return_loan(data_seq, info_seq);
                }
            }
        } catch (RETCODE_NO_DATA noData) {
            
        } finally {
            
        }
	}
	
	private final void livelinessChanged(ice.DeviceIdentityDataReader reader) {
        LivelinessChangedStatus lcs = new LivelinessChangedStatus();
        reader.get_liveliness_changed_status(lcs);
        log.trace(lcs.toString());
        int count_instances = 0, alive_instances = 0, not_alive_instances = 0;
        InstanceHandle_t lastHandle = InstanceHandle_t.HANDLE_NIL;
        try {
            while(true) {
                try {
                    log.trace("read_next_instance");
                    reader.read_next_instance(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, lastHandle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                    log.trace("read_next_instance for livelinessChanged(DeviceIdentityDataReader)...");
    //                      reader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition) condition);
                    // How offensive is this to the intent of the API?
                    // I really only care about the most recent sample for the instance
                    int i = data_seq.size() - 1;
//                    for(int i = 0; i < data_seq.size(); i++) {
                        count_instances++;
                        
                        DeviceIdentity di = (DeviceIdentity) data_seq.get(i);
                        SampleInfo si = (SampleInfo) info_seq.get(i);
    
                        log.trace("SampleInfo="+fieldsToStringSilent(si)+" DeviceIdentity="+di);
                        lastHandle = si.instance_handle;
                        
                        Device current = get(lastHandle, cur_idx, false, keyHolder);
                        
                        if(0 != (InstanceStateKind.NOT_ALIVE_INSTANCE_STATE & si.instance_state)) {
                            not_alive_instances++;
                            
                            if(cur_idx[0] >= 0) {
                                log.trace("not alive known device InstanceState:"+si.instance_state+" "+keyHolder[0].universal_device_identifier);
                                  lastRemoved = contents.remove(cur_idx[0]);
                                  contentsMap.remove(lastRemoved);
                                  fireIntervalRemoved(DeviceListModel.this, cur_idx[0], cur_idx[0]);
                                  lastRemoved = null;
                              } else {
                                  log.trace("not alive InstanceState:"+si.instance_state+" "+keyHolder[0].universal_device_identifier);
                              }
                        } else if(InstanceStateKind.ALIVE_INSTANCE_STATE == si.instance_state) {
                            alive_instances++;
                            if(si.valid_data) {
                                if(null == current) {
                                    log.trace("alive unknown device "+keyHolder[0].universal_device_identifier);
                                    Device device = new Device(di);
                                    contents.add(0, device);
                                    contentsMap.put(device.getDeviceIdentity().universal_device_identifier, device);
                                    fireIntervalAdded(this, 0, 0);
                                } else {
                                    log.trace("alive known device " + keyHolder[0].universal_device_identifier);
                                    current.getDeviceIdentity().copy_from(di);
                                    fireContentsChanged(DeviceListModel.this, cur_idx[0], cur_idx[0]);
                                }
                            } else {
                                log.trace("alive with no samples device " + keyHolder[0].universal_device_identifier);
                                
                            }
                        } 
    
//                    }
                } finally {
                    reader.return_loan(data_seq, info_seq);
                }
            }
        } catch (RETCODE_NO_DATA noData) {
            
        } finally {
            
        }
        log.trace("Saw " + count_instances + " instances " + alive_instances + " alive and " + not_alive_instances + " not alive");

	}
	
    final DeviceIdentitySeq data_seq = new DeviceIdentitySeq();
    final DeviceConnectivitySeq conn_seq = new DeviceConnectivitySeq();
    final SampleInfoSeq info_seq = new SampleInfoSeq();
    private final int[] cur_idx = new int[1];
    private final DeviceIdentity[] keyHolder = new DeviceIdentity[1];
	
	public DeviceListModel(Subscriber subscriber, EventLoop eventLoop)	{
		this.subscriber = subscriber;
		this.eventLoop = eventLoop;
		DomainParticipant participant = subscriber.get_participant();
		topic = lookupOrCreateTopic(participant, ice.DeviceIdentityTopic.VALUE, DeviceIdentityTypeSupport.class);
		connTopic = lookupOrCreateTopic(participant, ice.DeviceConnectivityTopic.VALUE, DeviceConnectivityTypeSupport.class);
		
		reader = (DeviceIdentityDataReader) subscriber.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		connReader = (DeviceConnectivityDataReader) subscriber.create_datareader(connTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

		reader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS | StatusKind.LIVELINESS_LOST_STATUS | StatusKind.LIVELINESS_CHANGED_STATUS);
		connReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
		
		eventLoop.addHandler(reader.get_statuscondition(), new EventLoop.ConditionHandler() {
	            @Override
	            public void conditionChanged(Condition condition) {
	                ice.DeviceIdentityDataReader reader = (DeviceIdentityDataReader) ((StatusCondition)condition).get_entity();
	                int statusChanges = reader.get_status_changes();
	                if(0 != (StatusKind.DATA_AVAILABLE_STATUS & statusChanges)) {
	                    dataAvailable(reader);
	                }
	                
	                if(0 != (StatusKind.LIVELINESS_CHANGED_STATUS & statusChanges) ||
	                   0 != (StatusKind.LIVELINESS_LOST_STATUS & statusChanges)) {
	                    livelinessChanged(reader);
	                }
	                
	                
	            }
	            
	        });
		eventLoop.addHandler(connReader.get_statuscondition(), new EventLoop.ConditionHandler() {
            
            @Override
            public void conditionChanged(Condition condition) {
                ice.DeviceConnectivityDataReader reader = (ice.DeviceConnectivityDataReader) ((StatusCondition)condition).get_entity();
                if(0 != (StatusKind.DATA_AVAILABLE_STATUS & reader.get_status_changes())) {
                    dataAvailable(reader);
                }
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
	    return contentsMap.get(udi);
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
