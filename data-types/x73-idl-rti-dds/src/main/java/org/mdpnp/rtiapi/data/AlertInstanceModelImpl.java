package org.mdpnp.rtiapi.data;

public class AlertInstanceModelImpl extends InstanceModelImpl<ice.Alert, ice.AlertDataReader, ice.AlertDataWriter> implements AlertInstanceModel {

    public AlertInstanceModelImpl(String topic) {
        super(topic, ice.Alert.class, ice.AlertDataReader.class, ice.AlertDataWriter.class, ice.AlertTypeSupport.class, ice.AlertSeq.class);
    }

}
