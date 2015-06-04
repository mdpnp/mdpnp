package org.mdpnp.rtiapi.data;

public class SampleArrayInstanceModelImpl extends InstanceModelImpl<ice.SampleArray, ice.SampleArrayDataReader, ice.SampleArrayDataWriter> implements SampleArrayInstanceModel {

    public SampleArrayInstanceModelImpl(String topic) {
        super(topic, ice.SampleArray.class, ice.SampleArrayDataReader.class, ice.SampleArrayDataWriter.class, ice.SampleArrayTypeSupport.class, ice.SampleArraySeq.class);
    }

}
