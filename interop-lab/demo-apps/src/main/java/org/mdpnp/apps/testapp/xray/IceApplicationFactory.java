package org.mdpnp.apps.testapp.xray;

import com.rti.dds.subscription.Subscriber;
import org.mdpnp.apps.testapp.*;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class IceApplicationFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType XRay =
            new IceApplicationProvider.AppType("X-Ray Ventilator Sync", "NOXRAYVENT", IceApplicationProvider.class.getResource("xray-vent.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() { return XRay;}

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) {

        final DeviceListModel nc = (DeviceListModel)parentContext.getBean("deviceListModel");
        final DeviceListCellRenderer deviceCellRenderer = new DeviceListCellRenderer(nc);

        final EventLoop  eventLoop = (EventLoop)parentContext.getBean("eventLoop");
        final Subscriber subscriber= (Subscriber)parentContext.getBean("subscriber");

        final XRayVentPanel ui = new XRayVentPanel(subscriber, eventLoop, deviceCellRenderer);

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return XRay;
            }

            @Override
            public Component getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                final EventLoop  eventLoop = (EventLoop)context.getBean("eventLoop");
                final Subscriber subscriber= (Subscriber)context.getBean("subscriber");
                ui.start(subscriber, eventLoop);
            }

            @Override
            public void stop() {
                ui.stop();
            }

            @Override
            public void destroy() {
            }
        };
    }

}
