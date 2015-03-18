package org.mdpnp.devices.puritanbennett._840;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

/**
 *
 */
public class PB840Provider implements DeviceDriverProvider {

    public DeviceType getDeviceType(){
        return new DeviceType(ice.ConnectionType.Serial, "Puritan Bennett", "840", "PB840");
    }

    public AbstractDevice create(ApplicationContext context) throws Exception {
        EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
        int domainId = (Integer)context.getBean("domainId");
        return new DemoPB840(domainId, eventLoop);

    }
}
