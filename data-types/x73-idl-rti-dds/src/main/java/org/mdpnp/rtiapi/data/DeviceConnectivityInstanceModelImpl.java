package org.mdpnp.rtiapi.data;


public class DeviceConnectivityInstanceModelImpl extends InstanceModelImpl<ice.DeviceConnectivity, ice.DeviceConnectivityDataReader, ice.DeviceConnectivityDataWriter> implements DeviceConnectivityInstanceModel {

    public DeviceConnectivityInstanceModelImpl(String topic) {
        super(topic, ice.DeviceConnectivity.class, ice.DeviceConnectivityDataReader.class, ice.DeviceConnectivityDataWriter.class, ice.DeviceConnectivityTypeSupport.class, ice.DeviceConnectivitySeq.class);
    }

}
