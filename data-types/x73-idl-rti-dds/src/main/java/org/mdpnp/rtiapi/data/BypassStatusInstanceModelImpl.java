package org.mdpnp.rtiapi.data;

public class BypassStatusInstanceModelImpl extends InstanceModelImpl<ice.BypassStatus, ice.BypassStatusDataReader, ice.BypassStatusDataWriter> implements BypassStatusInstanceModel {

    public BypassStatusInstanceModelImpl(String topic) {
        super(topic, ice.BypassStatus.class, ice.BypassStatusDataReader.class, ice.BypassStatusDataWriter.class, ice.BypassStatusTypeSupport.class, ice.BypassStatusSeq.class);
    }

}
