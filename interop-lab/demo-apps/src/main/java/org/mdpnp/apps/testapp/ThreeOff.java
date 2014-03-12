/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
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
