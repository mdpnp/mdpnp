package org.mdpnp.apps.fxbeans;



public class InfusionStatusFxList extends AbstractFxList<ice.InfusionStatus, ice.InfusionStatusDataReader, InfusionStatusFx>{

    public InfusionStatusFxList(String topicName) {
        super(topicName, ice.InfusionStatus.class, ice.InfusionStatusDataReader.class, 
                ice.InfusionStatusTypeSupport.class, ice.InfusionStatusSeq.class, InfusionStatusFx.class);
    }
}
