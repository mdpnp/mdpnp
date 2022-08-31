package org.mdpnp.rtiapi.data;

public class InfusionProgramInstanceModelImpl extends InstanceModelImpl<ice.InfusionProgram, ice.InfusionProgramDataReader, ice.InfusionProgramDataWriter> implements InfusionProgramInstanceModel {

    public InfusionProgramInstanceModelImpl(String topic) {
        super(topic, ice.InfusionProgram.class, ice.InfusionProgramDataReader.class, ice.InfusionProgramDataWriter.class, ice.InfusionProgramTypeSupport.class, ice.InfusionProgramSeq.class);
    }

}
