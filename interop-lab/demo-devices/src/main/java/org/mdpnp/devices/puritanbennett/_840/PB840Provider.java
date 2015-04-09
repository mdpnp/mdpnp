package org.mdpnp.devices.puritanbennett._840;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.support.AbstractApplicationContext;

/**
 *
 */
public class PB840Provider extends DeviceDriverProvider.SpringLoadedDriver {

    @Override
    public DeviceType getDeviceType(){
        return new DeviceType(ice.ConnectionType.Serial, "Puritan Bennett", "840", "PB840");
    }

    @Override
    public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
        EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
        int domainId = (Integer)context.getBean("domainId");
        return new DemoPB840(domainId, eventLoop);
    }
}
