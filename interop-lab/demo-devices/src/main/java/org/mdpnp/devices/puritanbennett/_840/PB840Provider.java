package org.mdpnp.devices.puritanbennett._840;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.support.AbstractApplicationContext;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

/**
 *
 */
public class PB840Provider extends DeviceDriverProvider.SpringLoadedDriver {

    @Override
    public DeviceType getDeviceType(){
        return new DeviceType(ice.ConnectionType.Serial, "Puritan Bennett", "840", "PB840", 2);
    }

    @Override
    public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
        EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
        Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
        Publisher publisher = context.getBean("publisher", Publisher.class);
        return new DemoPB840(subscriber, publisher, eventLoop);
    }
}
