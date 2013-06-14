package org.mdpnp.guis.swing;

import ice.DeviceIdentity;
import ice.DeviceIdentityDataReader;
import ice.DeviceIdentitySeq;
import ice.DeviceIdentityTypeSupport;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.TopicDescription;

public class DeviceMonitor {
    private final WaitSet waiter = new WaitSet();
    private final ConditionSeq condSeq = new ConditionSeq();
    private final StringSeq identity = new StringSeq();
    private final DeviceMonitorListener listener;
    
    
    interface ConditionHandler {
        void conditionChanged(ReadCondition condition);
    }
    
    abstract class AbstractConditionHandler<T extends DataReader> implements ConditionHandler {
        protected final T reader;
        public AbstractConditionHandler(T reader) {
            this.reader = reader;
        }
    }
    
    private final Map<Condition, ConditionHandler> conditionHandlers = new HashMap<Condition, ConditionHandler>();
    
    
    
    public DeviceMonitor(DomainParticipant participant, String udi, final DeviceMonitorListener listener) {
        this.listener = listener;
        identity.add("'"+udi+"'");
        Subscriber subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        TopicDescription deviceIdentityTopic = participant.lookup_topicdescription(ice.DeviceIdentityTopic.VALUE);
        if(null == deviceIdentityTopic) {
            DeviceIdentityTypeSupport.register_type(participant, ice.DeviceIdentityTypeSupport.get_type_name());
            deviceIdentityTopic = participant.create_topic(ice.DeviceIdentityTopic.VALUE, ice.DeviceIdentityTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        }
        DeviceIdentityDataReader reader = (DeviceIdentityDataReader) subscriber.create_datareader(deviceIdentityTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ConditionHandler ch = new AbstractConditionHandler<DeviceIdentityDataReader>(reader) {
            private final DeviceIdentitySeq data_seq = new DeviceIdentitySeq();
            private final SampleInfoSeq info_seq = new SampleInfoSeq();
            
            @Override
            public void conditionChanged(ReadCondition condition) {
                try {
                    reader.take_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, condition);
                    for(int i = 0; i < info_seq.size(); i++) {
                        if( ((SampleInfo)info_seq.get(i)).valid_data) {
                            DeviceIdentity di = (DeviceIdentity) data_seq.get(i);
                            listener.deviceIdentity(di);
                        }
                    }
                } catch (RETCODE_NO_DATA noData) {
                    
                } finally {
                    reader.return_loan(data_seq, info_seq);
                }
            }
        };
        Condition c = reader.create_querycondition(SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, "universal_device_identifier = %0", identity);
        conditionHandlers.put(c, ch);

        waiter.attach_condition(c);
        
        Thread t = new Thread(new Runnable() {
           public void run() {
               Duration_t dur = new Duration_t(Duration_t.DURATION_INFINITE_SEC, Duration_t.DURATION_INFINITE_NSEC);
               while(true) {
                   waitForIt(dur);
               }
           }
        }, "CompositeDevicePanel data handler");
        t.setDaemon(true);
        t.start();
    }

    public boolean waitForIt(Duration_t duration) {
        condSeq.clear();
        try {
            waiter.wait(condSeq, duration);
            for(int i = 0; i < condSeq.size(); i++) {
                ConditionHandler handler = conditionHandlers.get(condSeq.get(i));
                if(null != handler) {
                    handler.conditionChanged((ReadCondition) condSeq.get(i));
                }
            }
            return true;
        } catch(RETCODE_TIMEOUT timeout) {
            return false;
        }
    }
}
