package org.mdpnp.apps.testapp;

import java.io.IOException;

import org.mdpnp.rti.dds.DDS;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.DurabilityQosPolicyKind;
import com.rti.dds.infrastructure.HistoryQosPolicyKind;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.OwnershipQosPolicyKind;
import com.rti.dds.infrastructure.ReliabilityQosPolicyKind;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;
import com.rti.dds.type.builtin.KeyedString;
import com.rti.dds.type.builtin.KeyedStringDataWriter;
import com.rti.dds.type.builtin.KeyedStringTypeSupport;

public class TwoOff {
    public static void main(String[] args) throws IOException {
        DDS.init();
        int domainId = 0;
        DomainParticipant part = DomainParticipantFactory.get_instance().create_participant(domainId, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
//        Subscriber sub = part.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        Publisher  pub = part.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        KeyedStringTypeSupport.register_type(part, KeyedStringTypeSupport.get_type_name());
        Topic topic = part.create_topic("device_driver_command_topic", KeyedStringTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        DataWriterQos qos = new DataWriterQos();
        part.get_default_datawriter_qos(qos);
        qos.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
        qos.ownership.kind = OwnershipQosPolicyKind.SHARED_OWNERSHIP_QOS;

        qos.durability.kind = DurabilityQosPolicyKind.TRANSIENT_LOCAL_DURABILITY_QOS;
//        System.out.println(qos.durability.direct_communication);
//        qos.durability.direct_communication = true;
        qos.history.kind = HistoryQosPolicyKind.KEEP_ALL_HISTORY_QOS;
//        qos.history.depth = 1;
//        qos.history.refilter = RefilterQosPolicyKind.NONE_REFILTER_QOS;
//        qos.publish_mode.kind = PublishModeQosPolicyKind.ASYNCHRONOUS_PUBLISH_MODE_QOS;
        
        KeyedStringDataWriter writer = (KeyedStringDataWriter) pub.create_datawriter(topic, qos, null, StatusKind.STATUS_MASK_NONE);
        KeyedString cmd = (KeyedString) KeyedString.create();
        cmd.key = "kds/legato 110/command";
        cmd.value = "SEQ#=0\rADDR=00\rSTOP\r\r";
        writer.write(cmd, InstanceHandle_t.HANDLE_NIL);
        System.in.read();
//        InstanceHandle_t instance_handle = arg0.register_instance(cmd);
        
//        StatusCondition statusCondition = writer.get_statuscondition();
//        statusCondition.set_enabled_statuses(StatusKind.PUBLICATION_MATCHED_STATUS);
//        
//        WaitSet ws = new WaitSet();
//        ws.attach_condition(statusCondition);
//        
//        ConditionSeq seq = new ConditionSeq();
//        Duration_t dur = new Duration_t(Duration_t.DURATION_INFINITE_SEC, Duration_t.DURATION_INFINITE_NSEC);
//        PublicationMatchedStatus status = new PublicationMatchedStatus();
//        
//        while(true) {
//            ws.wait(seq, dur);
//            if(!seq.isEmpty() && 0 != (StatusKind.PUBLICATION_MATCHED_STATUS & writer.get_status_changes())) {
//                writer.get_publication_matched_status(status);
//                writer.write(cmd, InstanceHandle_t.HANDLE_NIL);
//                System.err.println(" I WROTE TO THE THING " + cmd);
//            }
//                
//            seq.clear();
//        }
        part.delete_contained_entities();

        System.exit(0);
//        
    }
}
