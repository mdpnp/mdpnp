package org.mdpnp.apps.testapp;

import org.mdpnp.rti.dds.DDS;

import ice.Numeric;
import ice.NumericDataReader;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

public class OneOff {
    public static void main(String[] args) {
        DDS.init();
        DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(0, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT,  null,  StatusKind.STATUS_MASK_NONE);
        ice.NumericTypeSupport.register_type(participant, ice.NumericTypeSupport.get_type_name());
        Topic topic = participant.create_topic(ice.NumericTopic.VALUE, ice.NumericTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        Subscriber sub = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.NumericDataReader reader = (NumericDataReader) sub.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        WaitSet ws = new WaitSet();
        reader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
        ws.attach_condition(reader.get_statuscondition());
        ConditionSeq cond_seq = new ConditionSeq();
        Duration_t forever = new Duration_t(Duration_t.DURATION_INFINITE_SEC, Duration_t.DURATION_INFINITE_NSEC);
        ice.NumericSeq nu_seq = new ice.NumericSeq();
        SampleInfoSeq info_seq =new SampleInfoSeq();
        while(true) {
            ws.wait(cond_seq, forever);
            reader.take(nu_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
            for(int i = 0; i < nu_seq.size(); i++) {
                SampleInfo si = (SampleInfo) info_seq.get(i);
                ice.Numeric n = (Numeric) nu_seq.get(i);
                if(si.valid_data) {
                    System.out.println(n);
                }
            }
            reader.return_loan(nu_seq, info_seq);
            cond_seq.clear();
        }
        
    }
}
