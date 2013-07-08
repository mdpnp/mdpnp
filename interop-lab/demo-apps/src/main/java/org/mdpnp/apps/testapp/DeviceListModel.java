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
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

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
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.TopicDescription;



public class DeviceListModel extends AbstractListModel<Device> {

	private static final Logger log = LoggerFactory
			.getLogger(DeviceListModel.class);
	
	private final List<Device> contents = new ArrayList<Device>();
	
	@SuppressWarnings("serial")
	public static class DeviceIdentityTableModel extends AbstractTableModel implements
			ListDataListener {
		@SuppressWarnings("rawtypes")
        private final ListModel model;

		public DeviceIdentityTableModel(@SuppressWarnings("rawtypes") ListModel model) {
			this.model = model;
			model.addListDataListener(this);
		}

		@Override
		public int getRowCount() {
			return model.getSize();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ice.DeviceIdentity d = (ice.DeviceIdentity) model.getElementAt(rowIndex);
			switch (columnIndex) {
			case 0:
			    return d.universal_device_identifier;
			case 1:
				return d.manufacturer;
			case 2:
				return d.model;
			default:
				return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "UDI";
			case 1:
				return "Manufacturer";
			case 2:
				return "Model";
			default:
				return null;
			}
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
		}
	}


	private final Subscriber subscriber;
	private final ice.DeviceIdentityDataReader reader;
	private TopicDescription topic;
	private TopicDescription connTopic;
	private final ice.DeviceConnectivityDataReader connReader;
	
	
	public DeviceListModel(Subscriber subscriber, EventLoop eventLoop)	{
		this.subscriber = subscriber;
		DomainParticipant participant = subscriber.get_participant();
		topic = lookupOrCreateTopic(participant, ice.DeviceIdentityTopic.VALUE, DeviceIdentityTypeSupport.class);
		connTopic = lookupOrCreateTopic(participant, ice.DeviceConnectivityTopic.VALUE, DeviceConnectivityTypeSupport.class);
		
		reader = (DeviceIdentityDataReader) subscriber.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		connReader = (DeviceConnectivityDataReader) subscriber.create_datareader(connTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

		final DeviceIdentitySeq data_seq = new DeviceIdentitySeq();
		final SampleInfoSeq info_seq = new SampleInfoSeq();
		
		StatusCondition status = reader.get_statuscondition();
		status.set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
		
		eventLoop.addHandler(status, new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                ice.DeviceIdentityDataReader reader = (DeviceIdentityDataReader) ((StatusCondition)condition).get_entity();
                try {
                    reader.read(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                    for(int i = 0; i < data_seq.size(); i++) {
                        DeviceIdentity di = (DeviceIdentity) data_seq.get(i);
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        
                        Device current = null;
                        int cur_idx = -1;
                        
                        for(int j = 0; j < contents.size(); j++) {
                            if(si.instance_handle.equals(reader.lookup_instance(contents.get(j).getDeviceIdentity()))) {
                                current = contents.get(j);
                                cur_idx = j;
                                break;
                            } 
                        }
                        
                        switch(si.instance_state) {
                        case InstanceStateKind.ALIVE_INSTANCE_STATE:
                            if(si.valid_data) {
                                if(null == current) {
                                    Device device = new Device(di);
                                    contents.add(0, device);
                                    fireIntervalAdded(this, 0, 0);
                                } else {
                                    current.getDeviceIdentity().copy_from(di);
                                    fireContentsChanged(DeviceListModel.this, cur_idx, cur_idx);
                                }
                            }
                            break;
                        case InstanceStateKind.NOT_ALIVE_DISPOSED_INSTANCE_STATE:
                        case InstanceStateKind.NOT_ALIVE_NO_WRITERS_INSTANCE_STATE:
                            log.trace("InstanceState:"+si.instance_state);
                            if(cur_idx >= 0) {
                                Device device = contents.remove(cur_idx);
                                fireIntervalRemoved(DeviceListModel.this, cur_idx, cur_idx);
                            }
                            break;
                        }
                    }
                } catch (RETCODE_NO_DATA noData) {
                    
                } finally {
                    reader.return_loan(data_seq, info_seq);
                }
                
            }
		    
		});
		
//		eventLoop.addHandler(reader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE), new EventLoop.ConditionHandler() {
//
//            @Override
//            public void conditionChanged(Condition condition) {
//                data_seq.clear();
//                info_seq.clear();
//                try {
//                    for(;;) {
//                        reader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition) condition);
//                        for(int i = 0; i < info_seq.size(); i++) {
//                            SampleInfo si = (SampleInfo) info_seq.get(i);
//                            if(si.valid_data) {
//                                log.trace("Alive:"+si.instance_handle);
//                                added(si.instance_handle, (DeviceIdentity) data_seq.get(i));
//                            }
//                        }
//                        reader.return_loan(data_seq, info_seq);
//                    }
//                } catch(RETCODE_NO_DATA noData) {
//                    log.debug("RETCODE_NO_DATA on ALIVE_INSTANCE_STATE");
//                } finally {
////                    reader.return_loan(data_seq, info_seq);
//                }
//            }
//		});
//		
//		EventLoop.ConditionHandler ch;
//		eventLoop.addHandler(reader.create_readcondition(SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.NOT_ALIVE_NO_WRITERS_INSTANCE_STATE), ch = new EventLoop.ConditionHandler() {
//
//            @Override
//            public void conditionChanged(Condition condition) {
//                data_seq.clear();
//                info_seq.clear();
//                try {
//                    for(;;) {
//                        reader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition) condition);
//                        for(int i = 0; i < info_seq.size(); i++) {
//                            log.trace("Not Alive:"+((SampleInfo)info_seq.get(i)).instance_handle);
//                            removed(((SampleInfo)info_seq.get(i)).instance_handle);
//                        }
//                        reader.return_loan(data_seq, info_seq);
//                    }
//                } catch(RETCODE_NO_DATA noData) {
//                    log.debug("RETCODE_NO_DATA on NOT_ALIVE_INSTANCE_STATE");
//                } finally {
////                    reader.return_loan(data_seq, info_seq);
//                }
//                
//            }
//		    
//		});
//        eventLoop.addHandler(reader.create_readcondition(SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.NOT_ALIVE_DISPOSED_INSTANCE_STATE), ch);

		
		final DeviceConnectivitySeq conn_seq = new DeviceConnectivitySeq();
		
		eventLoop.addHandler(connReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE), new EventLoop.ConditionHandler() {
            
            @Override
            public void conditionChanged(Condition condition) {
                conn_seq.clear();
                info_seq.clear();
                try {
                    connReader.read_w_condition(conn_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition)condition);
                    for(int i = 0; i < info_seq.size(); i++) {
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        if(si.valid_data) {
                            DeviceConnectivity sample = (DeviceConnectivity) conn_seq.get(i);
                            
                            int j = 0;
                            for(j = 0; j < contents.size(); j++) {
                                Device device = contents.get(j);
                                if(device.getDeviceIdentity().universal_device_identifier.equals(sample.universal_device_identifier)) {
                                    device.setDeviceConnectivity(sample);
                                    fireContentsChanged(DeviceListModel.this, j, j);
                                    break;
                                }
                            }
                            if(j == contents.size()) {
                                Device device = new Device();
                                device.getDeviceIdentity().universal_device_identifier = sample.universal_device_identifier;
                                device.setDeviceConnectivity(sample);
                                contents.add(0, device);
                                fireIntervalAdded(DeviceListModel.this, 0, 0);
                            }
                        }
                    }
                } catch (RETCODE_NO_DATA noData) {
                    
                } finally {
                    connReader.return_loan(conn_seq, info_seq);
                }
            }
        });
		
	      eventLoop.addHandler(connReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.NOT_ALIVE_INSTANCE_STATE), new EventLoop.ConditionHandler() {
	            
	            @Override
	            public void conditionChanged(Condition condition) {
	                conn_seq.clear();
	                info_seq.clear();
	                try {
	                    connReader.read_w_condition(conn_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition)condition);
	                    for(int i = 0; i < info_seq.size(); i++) {
	                        // TODO something here
	                    }
	                } catch (RETCODE_NO_DATA noData) {
	                    
	                } finally {
	                    connReader.return_loan(conn_seq, info_seq);
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
	    // TODO keep hashed
	    if(null == udi) {
	        return null;
	    }
	    for(Device d : contents) {
	        if(udi.equals(d.getDeviceIdentity().universal_device_identifier)) {
	            return d;
	        }
	    }
	    return null;
	}
	
	protected void added(InstanceHandle_t handle, DeviceIdentity di) {
	    int idx = indexOf(handle);
	    DeviceIdentity existent = null;
	    if(idx >= 0) {
	        log.debug("Updating idx="+idx);
	        existent = contents.get(idx).getDeviceIdentity();
	        existent.copy_from(di);
	        fireContentsChanged(this, idx, idx);
	    } else {
	        log.debug("Adding idx=0");
	        contents.add(0, new Device(di));
	        fireIntervalAdded(this, 0, 0);
	    }
	}
	
	private Device lastRemoved;
	// NOT REENTRANT AT ALL... BE CAREFUL ... SINGLE THREAD FOR NOW
	protected void removed(InstanceHandle_t handle) {
	    int idx = indexOf(handle);
	    
	    log.debug("Removing idx="+idx);
	    
        if(idx >= 0) {
            lastRemoved = contents.remove(idx);
            fireIntervalRemoved(this, idx, idx);
            lastRemoved = null;
        }
	}
	public Device getLastRemoved() {
        return lastRemoved;
    }
	
	public void tearDown() {
//	    subscriber.delete_datareader(reader);
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
