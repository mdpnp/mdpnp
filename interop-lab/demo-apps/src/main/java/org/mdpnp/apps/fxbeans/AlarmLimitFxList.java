package org.mdpnp.apps.fxbeans;



public class AlarmLimitFxList extends AbstractFxList<ice.AlarmLimit, ice.AlarmLimitDataReader, AlarmLimitFx> {

    public AlarmLimitFxList(String topicName) {
        super(topicName, ice.AlarmLimit.class, ice.AlarmLimitDataReader.class, ice.AlarmLimitTypeSupport.class, 
                ice.AlarmLimitSeq.class, AlarmLimitFx.class);
    }
}
