package org.mdpnp.devices.simulation.pump;

import ice.InfusionObjective;
import ice.InfusionStatusDataWriter;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.EventLoop.ConditionHandler;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.QueryCondition;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

public class SimInfusionPump extends AbstractSimulatedConnectedDevice {

    private ice.InfusionStatus infusionStatus = (ice.InfusionStatus) ice.InfusionStatus.create();
    private InstanceHandle_t infusionStatusHandle;

    private ice.InfusionStatusDataWriter infusionStatusWriter;
    private ice.InfusionObjectiveDataReader infusionObjectiveReader;
    private QueryCondition infusionObjectiveQueryCondition;
    private Topic infusionStatusTopic, infusionObjectiveTopic;

    private class MySimulatedInfusionPump extends SimulatedInfusionPump {
        @Override
        protected void receivePumpStatus(String drugName, boolean infusionActive, int drugMassMcg,
                int solutionVolumeMl, int volumeToBeInfusedMl, int infusionDurationSeconds,
                float infusionFractionComplete) {
            infusionStatus.drug_name = drugName;
            infusionStatus.infusionActive = infusionActive;
            infusionStatus.drug_mass_mcg = drugMassMcg;
            infusionStatus.solution_volume_ml = solutionVolumeMl;
            infusionStatus.volume_to_be_infused_ml = volumeToBeInfusedMl;
            infusionStatus.infusion_duration_seconds = infusionDurationSeconds;
            infusionStatus.infusion_fraction_complete = infusionFractionComplete;
            infusionStatusWriter.write(infusionStatus, infusionStatusHandle);
        }
    }

    @Override
    public void connect(String str) {
        pump.connect(executor);
        super.connect(str);
    }

    @Override
    public void disconnect() {
        pump.disconnect();
        super.disconnect();
    }

    private final MySimulatedInfusionPump pump = new MySimulatedInfusionPump();

    protected void writeIdentity() {
        deviceIdentity.model = "Infusion Pump (Simulated)";
        writeDeviceIdentity();
    }

    protected void stopThePump(boolean stopThePump) {
        pump.setInterlockStop(stopThePump);
    }

    public SimInfusionPump(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);

        writeIdentity();

        ice.InfusionStatusTypeSupport.register_type(getParticipant(), ice.InfusionStatusTypeSupport.get_type_name());
        infusionStatusTopic = getParticipant().create_topic(ice.InfusionStatusTopic.VALUE, ice.InfusionStatusTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        infusionStatusWriter = (InfusionStatusDataWriter) publisher.create_datawriter(infusionStatusTopic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        infusionStatus.unique_device_identifier = deviceIdentity.unique_device_identifier;
        infusionStatusHandle = infusionStatusWriter.register_instance(infusionStatus);

        infusionStatus.drug_name = "Morphine";
        infusionStatus.drug_mass_mcg = 20;
        infusionStatus.solution_volume_ml = 120;
        infusionStatus.infusion_duration_seconds = 3600;
        infusionStatus.infusion_fraction_complete = 0f;
        infusionStatus.infusionActive = true;
        infusionStatus.volume_to_be_infused_ml = 100;

        infusionStatusWriter.write(infusionStatus, infusionStatusHandle);

        ice.InfusionObjectiveTypeSupport.register_type(getParticipant(), ice.InfusionObjectiveTypeSupport.get_type_name());
        infusionObjectiveTopic = getParticipant().create_topic(ice.InfusionObjectiveTopic.VALUE,  ice.InfusionObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        infusionObjectiveReader = (ice.InfusionObjectiveDataReader) subscriber.create_datareader(infusionObjectiveTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        StringSeq params = new StringSeq();
        params.add("'"+deviceIdentity.unique_device_identifier+"'");
        infusionObjectiveQueryCondition = infusionObjectiveReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, "unique_device_identifier = %0", params);
        eventLoop.addHandler(infusionObjectiveQueryCondition, new ConditionHandler() {
            private ice.InfusionObjectiveSeq data_seq = new ice.InfusionObjectiveSeq();
            private SampleInfoSeq info_seq = new SampleInfoSeq();

            @Override
            public void conditionChanged(Condition condition) {

                for(;;) {
                    try {
                        infusionObjectiveReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition) condition);
                        for(int i = 0; i < info_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) info_seq.get(i);
                            ice.InfusionObjective data = (InfusionObjective) data_seq.get(i);
                            if(si.valid_data) {
                                stopThePump(data.stopInfusion);
                            }
                        }
                    } catch(RETCODE_NO_DATA noData) {
                        break;
                    } finally {
                        infusionObjectiveReader.return_loan(data_seq, info_seq);
                    }
                }
            }

        });


    }

    @Override
    public void shutdown() {
        eventLoop.removeHandler(infusionObjectiveQueryCondition);
        infusionObjectiveReader.delete_readcondition(infusionObjectiveQueryCondition);
        infusionObjectiveQueryCondition = null;

        subscriber.delete_datareader(infusionObjectiveReader);
        infusionObjectiveReader = null;

        getParticipant().delete_topic(infusionObjectiveTopic);
        infusionObjectiveTopic = null;

        infusionStatusWriter.unregister_instance(infusionStatus, infusionStatusHandle);
        infusionStatusHandle = null;

        publisher.delete_datawriter(infusionStatusWriter);
        infusionStatusWriter = null;

        getParticipant().delete_topic(infusionStatusTopic);
        infusionStatusTopic = null;

        super.shutdown();
    }

    @Override
    protected String iconResourceName() {
        return "pump.png";
    }

}
