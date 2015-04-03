package org.mdpnp.rtiapi.data;

public class AlarmSettingsInstanceModelImpl extends InstanceModelImpl<ice.AlarmSettings, ice.AlarmSettingsDataReader, ice.AlarmSettingsDataWriter> implements AlarmSettingsInstanceModel {

    public AlarmSettingsInstanceModelImpl(String topic) {
        super(topic, ice.AlarmSettings.class, ice.AlarmSettingsDataReader.class, ice.AlarmSettingsDataWriter.class, ice.AlarmSettingsTypeSupport.class, ice.AlarmSettingsSeq.class);
    }

}
