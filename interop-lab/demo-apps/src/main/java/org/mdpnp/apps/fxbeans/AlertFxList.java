package org.mdpnp.apps.fxbeans;

import javafx.beans.Observable;
import javafx.util.Callback;


public class AlertFxList extends AbstractFxList<ice.Alert, ice.AlertDataReader, AlertFx> {

    public AlertFxList(String topicName) {
        super(topicName, ice.Alert.class, ice.AlertDataReader.class, ice.AlertTypeSupport.class, 
                ice.AlertSeq.class, AlertFx.class);
    }

    @Override
    protected Callback<AlertFx, Observable[]> buildExtractor() {
        return new Callback<AlertFx, Observable[]>() {
            @Override
            public Observable[] call(AlertFx param) {
                return new Observable[] { param.source_timestampProperty() };
            }
        };
    }
}
