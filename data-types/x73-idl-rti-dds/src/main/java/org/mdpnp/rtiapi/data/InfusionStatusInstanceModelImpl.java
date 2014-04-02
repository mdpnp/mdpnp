package org.mdpnp.rtiapi.data;


@SuppressWarnings("serial")
public class InfusionStatusInstanceModelImpl extends InstanceModelImpl<ice.InfusionStatus, ice.InfusionStatusDataReader> implements InfusionStatusInstanceModel {

    public InfusionStatusInstanceModelImpl(String topic) {
        super(topic, ice.InfusionStatus.class, ice.InfusionStatusDataReader.class, ice.InfusionStatusTypeSupport.class, ice.InfusionStatusSeq.class);
    }

}
