package org.mdpnp.apps.testapp;

import ice.DeviceIdentity;
import ice.DeviceIdentityDataReader;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataTypeSupport;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicDescription;

public class DeviceIdentityListModel extends AbstractListModel<ice.DeviceIdentity> implements DataReaderListener {

	private static final Logger log = LoggerFactory
			.getLogger(DeviceIdentityListModel.class);
	
	private final List<ice.DeviceIdentity> contents = new ArrayList<ice.DeviceIdentity>();
	

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
	public DeviceIdentityListModel(Subscriber subscriber)	{
		this.subscriber = subscriber;
		topic = subscriber.get_participant().lookup_topicdescription(ice.DeviceIdentityTopic.VALUE);
		if(null == topic) {
		    ice.DeviceIdentityTypeSupport.register_type(subscriber.get_participant(), ice.DeviceIdentityTypeSupport.get_type_name());
		    topic = subscriber.get_participant().create_topic(ice.DeviceIdentityTopic.VALUE, ice.DeviceIdentityTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		}
		reader = (DeviceIdentityDataReader) subscriber.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT, this, StatusKind.DATA_AVAILABLE_STATUS);
	}

	public void tearDown() {

	}

    @Override
    public int getSize() {
        return contents.size();
    }

    @Override
    public DeviceIdentity getElementAt(int index) {
        return contents.get(index);
    }

    @Override
    public void on_data_available(DataReader arg0) {
        ice.DeviceIdentityDataReader reader = (DeviceIdentityDataReader) arg0;
        DeviceIdentity di = new DeviceIdentity();
        SampleInfo si = new SampleInfo();
        try {
            reader.read_next_sample(di, si);
            ice.DeviceIdentity current = null;
            int cur_idx = -1;
            
            for(int i = 0; i < contents.size(); i++) {
                if(si.instance_handle.equals(reader.lookup_instance(contents.get(i)))) {
                    current = contents.get(i);
                    cur_idx = i;
                    break;
                } 
            }
            
            switch(si.instance_state) {
            case InstanceStateKind.ALIVE_INSTANCE_STATE:
                if(si.valid_data) {
                    if(null == current) {
                        contents.add(0, di);
                        fireIntervalAdded(this, 0, 0);
                    } else {
                        current.copy_from(di);
                        fireContentsChanged(this, cur_idx, cur_idx);
                    }
                }
                break;
            case InstanceStateKind.NOT_ALIVE_DISPOSED_INSTANCE_STATE:
            case InstanceStateKind.NOT_ALIVE_NO_WRITERS_INSTANCE_STATE:
                if(cur_idx >= 0) {
                    contents.remove(cur_idx);
                    fireIntervalRemoved(this, cur_idx, cur_idx);
                }
                break;
            }
        } catch (RETCODE_NO_DATA noData) {
            
        }
        
    }

    @Override
    public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {
        // TODO Auto-generated method stub
        
    }

}
