package org.mdpnp.apps.fxbeans;


public class NumericFxList extends AbstractFxList<ice.Numeric, ice.NumericDataReader, NumericFx> {

    public NumericFxList(final String topicName) {
        super(topicName, ice.Numeric.class, ice.NumericDataReader.class, ice.NumericTypeSupport.class, ice.NumericSeq.class, NumericFx.class);
    }

}
