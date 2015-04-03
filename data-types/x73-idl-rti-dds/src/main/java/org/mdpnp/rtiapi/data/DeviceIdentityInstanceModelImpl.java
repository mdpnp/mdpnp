package org.mdpnp.rtiapi.data;

public class DeviceIdentityInstanceModelImpl extends InstanceModelImpl<ice.DeviceIdentity, ice.DeviceIdentityDataReader, ice.DeviceIdentityDataWriter> implements DeviceIdentityInstanceModel {

    public DeviceIdentityInstanceModelImpl(String topic) {
        super(topic, ice.DeviceIdentity.class, ice.DeviceIdentityDataReader.class, ice.DeviceIdentityDataWriter.class, ice.DeviceIdentityTypeSupport.class, ice.DeviceIdentitySeq.class);
    }
    

}
