package org.mdpnp.devices.simulation;

import ice.GlobalSimulationObjective;

import java.io.IOException;

import org.mdpnp.devices.EventLoop;

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
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

public class GlobalSimulationObjectiveMonitor {
    protected  ice.GlobalSimulationObjective globalSimulationObjective;
    protected  ice.GlobalSimulationObjectiveDataReader globalSimulationObjectiveReader;
    protected  Topic globalSimulationObjectiveTopic;
    private  ReadCondition rc;
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
        globalSimulationObjectiveTopic = domainParticipant.create_topic(ice.GlobalSimulationObjectiveTopic.VALUE, ice.GlobalSimulationObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        globalSimulationObjectiveReader = (ice.GlobalSimulationObjectiveDataReader) domainParticipant.create_datareader(globalSimulationObjectiveTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        rc = globalSimulationObjectiveReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);

        final ice.GlobalSimulationObjectiveSeq data_seq = new ice.GlobalSimulationObjectiveSeq();
        final SampleInfoSeq info_seq = new SampleInfoSeq();

        eventLoop.addHandler(rc, new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    globalSimulationObjectiveReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, rc);
                    for(int i = 0; i < info_seq.size(); i++) {
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        if(si.valid_data) {
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
