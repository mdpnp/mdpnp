package org.mdpnp.apps.fxbeans;



public class AlertFxList extends AbstractFxList<ice.Alert, ice.AlertDataReader, AlertFx> {

    public AlertFxList(String topicName) {
        super(topicName, ice.Alert.class, ice.AlertDataReader.class, ice.AlertTypeSupport.class, 
                ice.AlertSeq.class, AlertFx.class);
    }
}
