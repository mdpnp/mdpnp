package org.mdpnp.rtiapi.data;

public class GlobalSimulationObjectiveInstanceModelImpl extends InstanceModelImpl<ice.GlobalSimulationObjective, ice.GlobalSimulationObjectiveDataReader, ice.GlobalSimulationObjectiveDataWriter> implements GlobalSimulationObjectiveInstanceModel {
    public GlobalSimulationObjectiveInstanceModelImpl(String topic) {
        super(topic, ice.GlobalSimulationObjective.class, ice.GlobalSimulationObjectiveDataReader.class,
                ice.GlobalSimulationObjectiveDataWriter.class, ice.GlobalSimulationObjectiveTypeSupport.class,
                ice.GlobalSimulationObjectiveSeq.class);
    }
}
