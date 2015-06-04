package org.mdpnp.apps.fxbeans;



public class LocalAlarmLimitObjectiveFxList extends AbstractFxList<ice.LocalAlarmLimitObjective, ice.LocalAlarmLimitObjectiveDataReader, LocalAlarmLimitObjectiveFx> {

    public LocalAlarmLimitObjectiveFxList(String topicName) {
        super(topicName, ice.LocalAlarmLimitObjective.class, ice.LocalAlarmLimitObjectiveDataReader.class, ice.LocalAlarmLimitObjectiveTypeSupport.class, 
                ice.LocalAlarmLimitObjectiveSeq.class, LocalAlarmLimitObjectiveFx.class);
    }
}
