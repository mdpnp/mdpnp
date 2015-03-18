package org.mdpnp.rtiapi.data;

@SuppressWarnings("serial")
public class AlarmSettingsInstanceModelImpl extends InstanceModelImpl<ice.AlarmSettings, ice.AlarmSettingsDataReader> implements AlarmSettingsInstanceModel {

    public AlarmSettingsInstanceModelImpl(String topic) {
        super(topic, ice.AlarmSettings.class, ice.AlarmSettingsDataReader.class, ice.AlarmSettingsTypeSupport.class, ice.AlarmSettingsSeq.class);
    }

}
