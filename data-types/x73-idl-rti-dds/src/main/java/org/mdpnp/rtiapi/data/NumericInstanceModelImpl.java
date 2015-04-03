package org.mdpnp.rtiapi.data;

public class NumericInstanceModelImpl extends InstanceModelImpl<ice.Numeric, ice.NumericDataReader, ice.NumericDataWriter> implements NumericInstanceModel {

    public NumericInstanceModelImpl(String topic) {
        super(topic, ice.Numeric.class, ice.NumericDataReader.class, ice.NumericDataWriter.class, ice.NumericTypeSupport.class, ice.NumericSeq.class);
    }

}
