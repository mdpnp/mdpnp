package org.mdpnp.rtiapi.data;

public class AlarmLimitInstanceModelImpl extends InstanceModelImpl<ice.AlarmLimit, ice.AlarmLimitDataReader, ice.AlarmLimitDataWriter> 
										 implements AlarmLimitInstanceModel{
	
    public AlarmLimitInstanceModelImpl(String topic) {
        super(topic, ice.AlarmLimit.class, ice.AlarmLimitDataReader.class, ice.AlarmLimitDataWriter.class, ice.AlarmLimitTypeSupport.class, ice.AlarmLimitSeq.class);
    }

}
