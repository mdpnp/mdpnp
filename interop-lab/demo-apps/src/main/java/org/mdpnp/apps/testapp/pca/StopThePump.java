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
package org.mdpnp.apps.testapp.pca;

import ice.InfusionObjectiveDataWriter;
import ice.InfusionStatus;
import ice.InfusionStatusDataReader;

import java.io.IOException;

import org.mdpnp.rtiapi.data.QosProfiles;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.GuardCondition;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

/**
 * @author Jeff Plourde
 *
 */
public class StopThePump {
    public static void main(String[] args) {
        int domainId = 0;

        if (args.length > 0) {
            domainId = Integer.parseInt(args[0]);
        }

        DomainParticipant part = DomainParticipantFactory.get_instance().create_participant(domainId,
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        Subscriber sub = part.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        Publisher pub = part.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.InfusionStatusTypeSupport.register_type(part, ice.InfusionStatusTypeSupport.get_type_name());

        Topic infoTopic = part.create_topic(ice.InfusionStatusTopic.VALUE, ice.InfusionStatusTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.InfusionStatusDataReader reader = (InfusionStatusDataReader) sub.create_datareader_with_profile(infoTopic, QosProfiles.ice_library,
                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        ReadCondition rc = reader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
                InstanceStateKind.ALIVE_INSTANCE_STATE);

        ice.InfusionObjectiveTypeSupport.register_type(part, ice.InfusionObjectiveTypeSupport.get_type_name());
        Topic objTopic = part.create_topic(ice.InfusionObjectiveTopic.VALUE, ice.InfusionObjectiveTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.InfusionObjectiveDataWriter writer = (InfusionObjectiveDataWriter) pub.create_datawriter_with_profile(objTopic, QosProfiles.ice_library,
                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        final GuardCondition exitCondition = new GuardCondition();

        Thread t = new Thread(new Runnable() {
            public void run() {
                System.out.println("Press enter to exit");
                try {
                    System.in.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Activate exit trigger");
                exitCondition.set_trigger_value(true);
            }
        });
        t.setDaemon(true);
        t.start();

        WaitSet ws = new WaitSet();
        ws.attach_condition(rc);
        ws.attach_condition(exitCondition);

        ConditionSeq active_conditions = new ConditionSeq();
        Duration_t forever = new Duration_t(Duration_t.DURATION_INFINITE_SEC, Duration_t.DURATION_INFINITE_NSEC);

        boolean exit = false;

        ice.InfusionStatusSeq data_seq = new ice.InfusionStatusSeq();
        SampleInfoSeq info_seq = new SampleInfoSeq();

        while (!exit) {

            ws.wait(active_conditions, forever);
            for (int i = 0; i < active_conditions.size(); i++) {
                Condition c = (Condition) active_conditions.get(i);
                if (c.equals(exitCondition)) {
                    exit = true;
                    break;
                } else if (c.equals(rc)) {
                    for (;;) {
                        try {
                            reader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, rc);
                            for (int j = 0; j < data_seq.size(); j++) {
                                SampleInfo si = (SampleInfo) info_seq.get(j);
                                ice.InfusionStatus data = (InfusionStatus) data_seq.get(j);
                                if (si.valid_data) {
                                    if (data.infusionActive) {
                                        System.err.println("Stopping active infusion:" + data);
                                        ice.InfusionObjective obj = new ice.InfusionObjective();
                                        obj.unique_device_identifier = data.unique_device_identifier;
                                        obj.requestor = "ME";
                                        obj.stopInfusion = true;
                                        writer.write(obj, InstanceHandle_t.HANDLE_NIL);
                                    }
                                }
                            }
                        } catch (RETCODE_NO_DATA noData) {
                            break;
                        } finally {
                            reader.return_loan(data_seq, info_seq);
                        }
                    }
                }
            }
            active_conditions.clear();
        }
        pub.delete_datawriter(writer);
        writer = null;
        part.delete_topic(objTopic);
        objTopic = null;
        ice.InfusionObjectiveTypeSupport.unregister_type(part, ice.InfusionObjectiveTypeSupport.get_type_name());
        reader.delete_readcondition(rc);
        rc = null;
        sub.delete_datareader(reader);
        reader = null;
        part.delete_topic(infoTopic);
        infoTopic = null;
        ice.InfusionStatusTypeSupport.unregister_type(part, ice.InfusionStatusTypeSupport.get_type_name());

        part.delete_publisher(pub);
        pub = null;
        part.delete_subscriber(sub);
        sub = null;
        DomainParticipantFactory.get_instance().delete_participant(part);
        part = null;

        DomainParticipantFactory.finalize_instance();

    }
}
