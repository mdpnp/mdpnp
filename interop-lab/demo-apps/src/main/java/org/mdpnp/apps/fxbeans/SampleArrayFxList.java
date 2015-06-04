package org.mdpnp.apps.fxbeans;



public class SampleArrayFxList extends AbstractFxList<ice.SampleArray, ice.SampleArrayDataReader, SampleArrayFx> {

    public SampleArrayFxList(final String topicName) {
        super(topicName, ice.SampleArray.class, ice.SampleArrayDataReader.class, 
              ice.SampleArrayTypeSupport.class, ice.SampleArraySeq.class, SampleArrayFx.class);
    }

}
