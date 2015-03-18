package org.mdpnp.rtiapi.data;

@SuppressWarnings("serial")
public class AlertInstanceModelImpl extends InstanceModelImpl<ice.Alert, ice.AlertDataReader> implements AlertInstanceModel {

    public AlertInstanceModelImpl(String topic) {
        super(topic, ice.Alert.class, ice.AlertDataReader.class, ice.AlertTypeSupport.class, ice.AlertSeq.class);
    }

}
