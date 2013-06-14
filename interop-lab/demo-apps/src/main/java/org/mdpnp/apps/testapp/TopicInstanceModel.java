package org.mdpnp.apps.testapp;

import ice.DeviceIdentity;
import ice.DeviceIdentityDataReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.QueryCondition;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.topic.TopicDescription;
import com.rti.dds.topic.TypeSupport;

public class TopicInstanceModel<T, R extends DataReader, S extends TypeSupport> {
    private final List<T> contents = new ArrayList<T>();
    private final Map<InstanceHandle_t, T> instances = new HashMap<InstanceHandle_t, T>();
    
    
    public TopicInstanceModel(final DomainParticipant participant, final String topicName, WaitSet waitSet, Class<S> typeSupport) {
        
        subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        
        topic = subscriber.get_participant().lookup_topicdescription(topicName);
        if(null == topic) {
            ice.DeviceIdentityTypeSupport.register_type(subscriber.get_participant(), typeSupport.get_type_name());
            topic = subscriber.get_participant().create_topic(ice.DeviceIdentityTopic.VALUE, ice.DeviceIdentityTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        }
        reader = (R) subscriber.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ReadCondition rc;
        QueryCondition qc;
        qc.
    }
    
    
    private static final Logger log = LoggerFactory.getLogger(TopicInstanceModel.class);

    private final Subscriber subscriber;
    private final R reader;
    private TopicDescription topic;

    public int size() {
        return contents.size();
    }

    public T get(int index) {
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
}
