package org.mdpnp.rtiapi.data;


public class DeviceConnectivityInstanceModelImpl extends InstanceModelImpl<ice.DeviceConnectivity, ice.DeviceConnectivityDataReader> implements DeviceConnectivityInstanceModel {

    public DeviceConnectivityInstanceModelImpl(String topic) {
        super(topic, ice.DeviceConnectivity.class, ice.DeviceConnectivityDataReader.class, ice.DeviceConnectivityTypeSupport.class, ice.DeviceConnectivitySeq.class);
    }

}
