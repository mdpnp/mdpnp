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
package org.mdpnp.devices.simulation;

import ice.GlobalSimulationObjective;

import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

/**
 * @author Jeff Plourde
 *
 */
public class GlobalSimulationObjectiveMonitor {
    protected ice.GlobalSimulationObjective globalSimulationObjective;
    protected ice.GlobalSimulationObjectiveDataReader globalSimulationObjectiveReader;
    protected Topic globalSimulationObjectiveTopic;
    private ReadCondition rc;
    private final GlobalSimulationObjectiveListener listener;

    public GlobalSimulationObjectiveMonitor(final GlobalSimulationObjectiveListener listener) {
        this.listener = listener;
    }

    public void unregister() {
        eventLoop.removeHandler(rc);
        globalSimulationObjectiveReader.delete_readcondition(rc);
        rc = null;
        eventLoop = null;

        domainParticipant.delete_datareader(globalSimulationObjectiveReader);
        globalSimulationObjectiveReader = null;
        domainParticipant.delete_topic(globalSimulationObjectiveTopic);
        globalSimulationObjectiveTopic = null;
        ice.GlobalSimulationObjectiveTypeSupport.unregister_type(domainParticipant, ice.GlobalSimulationObjectiveTypeSupport.get_type_name());
        domainParticipant = null;

    }

    private DomainParticipant domainParticipant;
    private EventLoop eventLoop;

    public void register(DomainParticipant domainParticipant, EventLoop eventLoop) {
        this.domainParticipant = domainParticipant;
        this.eventLoop = eventLoop;
        globalSimulationObjective = (ice.GlobalSimulationObjective) ice.GlobalSimulationObjective.create();
        ice.GlobalSimulationObjectiveTypeSupport.register_type(domainParticipant, ice.GlobalSimulationObjectiveTypeSupport.get_type_name());
        globalSimulationObjectiveTopic = domainParticipant.create_topic(ice.GlobalSimulationObjectiveTopic.VALUE,
                ice.GlobalSimulationObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        globalSimulationObjectiveReader = (ice.GlobalSimulationObjectiveDataReader) domainParticipant.create_datareader_with_profile(
                globalSimulationObjectiveTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        rc = globalSimulationObjectiveReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
                InstanceStateKind.ANY_INSTANCE_STATE);

        final ice.GlobalSimulationObjectiveSeq data_seq = new ice.GlobalSimulationObjectiveSeq();
        final SampleInfoSeq info_seq = new SampleInfoSeq();

        eventLoop.addHandler(rc, new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    globalSimulationObjectiveReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, rc);
                    for (int i = 0; i < info_seq.size(); i++) {
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        if (si.valid_data) {
                            ice.GlobalSimulationObjective gso = (GlobalSimulationObjective) data_seq.get(i);
                            listener.simulatedNumeric(gso);
                        }
                    }

                } catch (RETCODE_NO_DATA noData) {

                } finally {
                    globalSimulationObjectiveReader.return_loan(data_seq, info_seq);
                }
            }

        });
    }
}
