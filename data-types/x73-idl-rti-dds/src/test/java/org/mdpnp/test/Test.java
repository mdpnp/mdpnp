package org.mdpnp.test;

import org.mdpnp.rti.dds.DDS;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.dynamicdata.DynamicData;
import com.rti.dds.dynamicdata.DynamicDataReader;
import com.rti.dds.dynamicdata.DynamicDataSeq;
import com.rti.dds.dynamicdata.DynamicDataTypeProperty_t;
import com.rti.dds.dynamicdata.DynamicDataTypeSupport;
import com.rti.dds.dynamicdata.DynamicDataWriter;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
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
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicDescription;
import com.rti.dds.topic.builtin.TopicBuiltinTopicData;
import com.rti.dds.typecode.StructMember;
import com.rti.dds.typecode.TypeCode;
import com.rti.dds.typecode.TypeCodeFactory;

public class Test {
    public static void main(String[] args) {
        DDS.init();
        DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(0, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
//        TypeCode vendorId = TypeCodeFactory.get_instance().create_array_tc(3, TypeCode.TC_OCTET);
        TypeCode string = TypeCodeFactory.get_instance().create_string_tc(255);
        TypeCode sequenceOfString = TypeCodeFactory.get_instance().create_sequence_tc(255, string);
        TypeCode VendorId_t = TypeCodeFactory.get_instance().create_struct_tc("Wrapper", new StructMember[] { new StructMember("stringSequence", false, TypeCode.NOT_BITFIELD, false, sequenceOfString)});

        final DynamicDataTypeSupport ts = new DynamicDataTypeSupport(VendorId_t, DynamicDataTypeSupport.TYPE_PROPERTY_DEFAULT);
        ts.register_type(participant, "MYSPECIALTYPE");
        Topic topic = participant.create_topic("MYTOPIC", "MYSPECIALTYPE", DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final DynamicDataSeq data_seq = new DynamicDataSeq();
        final SampleInfoSeq info_seq = new SampleInfoSeq();
        final DynamicDataReader ddReader = (DynamicDataReader) participant.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT, new DataReaderListener() {

            @Override
            public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void on_data_available(DataReader arg0) {
                DynamicDataReader ddReader = (DynamicDataReader) arg0;
                ddReader.take(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                try {
                    for(int i = 0; i < info_seq.size(); i++) {
                        DynamicData dd = (DynamicData) data_seq.get(i);
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        if(si.valid_data) {
                            ts.print_data(dd);
                        }
                    }
                } finally {
                    ddReader.return_loan(data_seq, info_seq);
                }
            }
        }, StatusKind.DATA_AVAILABLE_STATUS);

        DynamicDataWriter writer = (DynamicDataWriter) participant.create_datawriter(topic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        DynamicData dd = (DynamicData) ts.create_data();
//        dd.set_byte_array("vendorId", DynamicData.MEMBER_ID_UNSPECIFIED, new byte[] {2,3,4});
        DynamicDataTypeSupport ts1 = new DynamicDataTypeSupport(dd.get_member_type("stringSequence", DynamicData.MEMBER_ID_UNSPECIFIED), DynamicDataTypeSupport.TYPE_PROPERTY_DEFAULT);
        DynamicDataSeq dd1 = new DynamicDataSeq();
        dd1.add("FOO");
        dd1.add("BAR");

        for(int i = 0; i < 60; i++) {
            writer.write(dd,  InstanceHandle_t.HANDLE_NIL);

            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        participant.delete_contained_entities();
        DomainParticipantFactory.get_instance().delete_participant(participant);
        DomainParticipantFactory.finalize_instance();


    }
}
