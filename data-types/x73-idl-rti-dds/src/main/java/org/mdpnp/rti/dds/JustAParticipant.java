package org.mdpnp.rti.dds;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataDataReader;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataSeq;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataTypeSupport;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.subscription.ViewStateKind;

public class JustAParticipant {
    public static void main(String[] args) throws InterruptedException {
        DDS.init();
        DomainParticipantFactoryQos fQos = new DomainParticipantFactoryQos();
        fQos.entity_factory.autoenable_created_entities = false;
        DomainParticipantFactory.get_instance().set_qos(fQos);
        DomainParticipantQos qos = new DomainParticipantQos();
        DomainParticipantFactory.get_instance().get_default_participant_qos(qos);
        qos.participant_name.name = "JustAParticipant";
        DomainParticipantFactory.get_instance().set_default_participant_qos(qos);
        DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(0, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final ParticipantBuiltinTopicDataDataReader reader = (ParticipantBuiltinTopicDataDataReader) participant.get_builtin_subscriber().lookup_datareader(ParticipantBuiltinTopicDataTypeSupport.PARTICIPANT_TOPIC_NAME);
//        participant.ignore_participant(participant.get_instance_handle());
        reader.set_listener(new DataReaderListener() {

            @Override
            public void on_data_available(DataReader arg0) {
                SampleInfoSeq info_seq = new SampleInfoSeq();
                ParticipantBuiltinTopicDataSeq data_seq = new ParticipantBuiltinTopicDataSeq();
                reader.read(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                try {
                    for(int i = 0; i < info_seq.size(); i++) {
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        ParticipantBuiltinTopicData data = (ParticipantBuiltinTopicData) data_seq.get(i);
                        System.out.println(si.instance_handle + " " + si.instance_state);
                        if(si.valid_data) {
                            System.out.println(data);
                        }
                    }
                } finally {
                    reader.return_loan(data_seq, info_seq);
                }
            }

            @Override
            public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {
                System.out.println("on_liveliness_change:"+arg1);

            }

            @Override
            public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {
                System.out.println("on_requested_deadline_missed:"+arg1);

            }

            @Override
            public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {
                System.out.println("on_requested_incompatible_qos:"+arg1);
            }

            @Override
            public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
                System.out.println("on_sample_lost:"+arg1);
            }

            @Override
            public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
                System.out.println("on_sample_rejected:"+arg1);
            }

            @Override
            public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {
                System.out.println("on_subscription_matched:"+arg1);

            }

        }, StatusKind.STATUS_MASK_ALL);
        participant.enable();
        Thread.sleep(60000L);
        DomainParticipantFactory.get_instance().delete_participant(participant);
        DomainParticipantFactory.finalize_instance();
    }
}
