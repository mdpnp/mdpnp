package org.mdpnp.apps.testapp;

import org.mdpnp.rti.dds.DDS;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataDataReader;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataSeq;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataTypeSupport;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.ViewStateKind;

public class ThreeOff {
    public static void main(String[] args) {
        DDS.init();
        DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(0, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
//        PublicationBuiltinTopicDataTypeSupport.register_type(participant, PublicationBuiltinTopicDataTypeSupport.get_type_name());
//        TopicDescription topic = participant.lookup_topicdescription(PublicationBuiltinTopicDataTypeSupport.PUBLICATION_TOPIC_NAME);
//        PublicationBuiltinTopicDataDataReader reader = (PublicationBuiltinTopicDataDataReader) participant.get_builtin_subscriber().lookup_datareader(PublicationBuiltinTopicDataTypeSupport.PUBLICATION_TOPIC_NAME);
        ParticipantBuiltinTopicDataDataReader reader = (ParticipantBuiltinTopicDataDataReader) participant.get_builtin_subscriber().lookup_datareader(ParticipantBuiltinTopicDataTypeSupport.PARTICIPANT_TOPIC_NAME);
        reader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
        WaitSet ws = new WaitSet();
        ws.attach_condition(reader.get_statuscondition());
        Duration_t dur = new Duration_t(Duration_t.DURATION_INFINITE_SEC, Duration_t.DURATION_INFINITE_NSEC);
        ConditionSeq condSeq = new ConditionSeq();
        ParticipantBuiltinTopicDataSeq data_seq = new ParticipantBuiltinTopicDataSeq();
        SampleInfoSeq sample_seq = new SampleInfoSeq();
        for(;;) {
            ws.wait(condSeq, dur);
            try {
                reader.read(data_seq, sample_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                for(int i = 0; i < sample_seq.size(); i++) {
                    ParticipantBuiltinTopicData data = (ParticipantBuiltinTopicData) data_seq.get(i);
                    SampleInfo si = (SampleInfo) sample_seq.get(i);
                    System.out.println(si.instance_handle);
                    if(si.valid_data) {
                        System.out.println("\t"+data);
                    }
                }
            } finally {
                reader.return_loan(data_seq, sample_seq);
            }
        }
    }
}
