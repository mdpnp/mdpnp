package org.mdpnp.apps.fxbeans;

import javafx.beans.Observable;
import javafx.util.Callback;


public class SampleArrayFxList extends AbstractFxList<ice.SampleArray, ice.SampleArrayDataReader, SampleArrayFx> {

    public SampleArrayFxList(final String topicName) {
        super(topicName, ice.SampleArray.class, ice.SampleArrayDataReader.class, 
              ice.SampleArrayTypeSupport.class, ice.SampleArraySeq.class, SampleArrayFx.class);
    }

    @Override
    public Callback<SampleArrayFx, Observable[]> buildExtractor() {
        return new Callback<SampleArrayFx, Observable[]>() {
            @Override
            public Observable[] call(SampleArrayFx param) {
                return new Observable[] { param.source_timestampProperty() };
            }
        };
    }
}
