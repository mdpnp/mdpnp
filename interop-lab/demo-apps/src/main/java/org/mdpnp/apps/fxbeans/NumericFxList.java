package org.mdpnp.apps.fxbeans;

import javafx.beans.Observable;
import javafx.util.Callback;

public class NumericFxList extends AbstractFxList<ice.Numeric, ice.NumericDataReader, NumericFx> {

    public NumericFxList(final String topicName) {
        super(topicName, ice.Numeric.class, ice.NumericDataReader.class, ice.NumericTypeSupport.class, ice.NumericSeq.class, NumericFx.class);
    }

    @Override
    public Callback<NumericFx, Observable[]> buildExtractor() {
        return new Callback<NumericFx, Observable[]>() {
            @Override
            public Observable[] call(NumericFx param) {
                return new Observable[] { param.source_timestampProperty() };
            }
        };
    }
}
