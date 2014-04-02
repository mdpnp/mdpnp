package org.mdpnp.rtiapi.data;


@SuppressWarnings("serial")
public class DeviceIdentityInstanceModelImpl extends InstanceModelImpl<ice.DeviceIdentity, ice.DeviceIdentityDataReader> implements DeviceIdentityInstanceModel {

    public DeviceIdentityInstanceModelImpl(String topic) {
        super(topic, ice.DeviceIdentity.class, ice.DeviceIdentityDataReader.class, ice.DeviceIdentityTypeSupport.class, ice.DeviceIdentitySeq.class);
    }
    

}
