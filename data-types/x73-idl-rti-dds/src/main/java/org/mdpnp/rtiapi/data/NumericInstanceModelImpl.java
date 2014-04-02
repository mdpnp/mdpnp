package org.mdpnp.rtiapi.data;


@SuppressWarnings("serial")
public class NumericInstanceModelImpl extends InstanceModelImpl<ice.Numeric, ice.NumericDataReader> implements NumericInstanceModel {

    public NumericInstanceModelImpl(String topic) {
        super(topic, ice.Numeric.class, ice.NumericDataReader.class, ice.NumericTypeSupport.class, ice.NumericSeq.class);
    }

}
