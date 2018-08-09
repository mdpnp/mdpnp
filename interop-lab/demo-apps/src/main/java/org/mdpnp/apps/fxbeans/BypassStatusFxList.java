package org.mdpnp.apps.fxbeans;

public class BypassStatusFxList extends AbstractFxList<ice.BypassStatus, ice.BypassStatusDataReader, BypassStatusFx>{

    public BypassStatusFxList(String topicName) {
        super(topicName, ice.BypassStatus.class, ice.BypassStatusDataReader.class, 
                ice.BypassStatusTypeSupport.class, ice.BypassStatusSeq.class, BypassStatusFx.class);
    }
}
