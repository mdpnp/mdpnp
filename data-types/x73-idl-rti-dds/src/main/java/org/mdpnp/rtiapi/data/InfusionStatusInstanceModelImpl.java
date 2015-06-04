package org.mdpnp.rtiapi.data;

public class InfusionStatusInstanceModelImpl extends InstanceModelImpl<ice.InfusionStatus, ice.InfusionStatusDataReader, ice.InfusionStatusDataWriter> implements InfusionStatusInstanceModel {

    public InfusionStatusInstanceModelImpl(String topic) {
        super(topic, ice.InfusionStatus.class, ice.InfusionStatusDataReader.class, ice.InfusionStatusDataWriter.class, ice.InfusionStatusTypeSupport.class, ice.InfusionStatusSeq.class);
    }

}
