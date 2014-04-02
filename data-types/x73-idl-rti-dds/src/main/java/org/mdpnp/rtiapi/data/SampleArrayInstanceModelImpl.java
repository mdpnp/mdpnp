package org.mdpnp.rtiapi.data;


@SuppressWarnings("serial")
public class SampleArrayInstanceModelImpl extends InstanceModelImpl<ice.SampleArray, ice.SampleArrayDataReader> implements SampleArrayInstanceModel {

    public SampleArrayInstanceModelImpl(String topic) {
        super(topic, ice.SampleArray.class, ice.SampleArrayDataReader.class, ice.SampleArrayTypeSupport.class, ice.SampleArraySeq.class);
    }

}
