package org.mdpnp.apps.fxbeans;

import javafx.beans.Observable;
import javafx.util.Callback;


public class InfusionStatusFxList extends AbstractFxList<ice.InfusionStatus, ice.InfusionStatusDataReader, InfusionStatusFx>{

    public InfusionStatusFxList(String topicName) {
        super(topicName, ice.InfusionStatus.class, ice.InfusionStatusDataReader.class, 
                ice.InfusionStatusTypeSupport.class, ice.InfusionStatusSeq.class, InfusionStatusFx.class);
    }
    
    @Override
    protected Callback<InfusionStatusFx, Observable[]> buildExtractor() {
        return new Callback<InfusionStatusFx, Observable[]>() {
            @Override
            public Observable[] call(InfusionStatusFx param) {
                return new Observable[] { param.source_timestampProperty() };
            }
        };
    }

}
