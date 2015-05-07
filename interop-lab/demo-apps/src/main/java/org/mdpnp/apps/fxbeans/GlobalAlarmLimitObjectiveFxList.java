package org.mdpnp.apps.fxbeans;



public class GlobalAlarmLimitObjectiveFxList extends AbstractFxList<ice.GlobalAlarmLimitObjective, ice.GlobalAlarmLimitObjectiveDataReader, GlobalAlarmLimitObjectiveFx> {

    public GlobalAlarmLimitObjectiveFxList(String topicName) {
        super(topicName, ice.GlobalAlarmLimitObjective.class, ice.GlobalAlarmLimitObjectiveDataReader.class, ice.GlobalAlarmLimitObjectiveTypeSupport.class, 
                ice.GlobalAlarmLimitObjectiveSeq.class, GlobalAlarmLimitObjectiveFx.class);
    }
}
