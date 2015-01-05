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

    @Override
    public AppType getAppType() { return AppType.XRay;}

    @Override
    public IceAppsContainer.IceApp create(ApplicationContext parentContext) {

        final DeviceListModel nc = (DeviceListModel)parentContext.getBean("deviceListModel");
        final DeviceListCellRenderer deviceCellRenderer = new DeviceListCellRenderer(nc);

        final EventLoop  eventLoop = (EventLoop)parentContext.getBean("eventLoop");
        final Subscriber subscriber= (Subscriber)parentContext.getBean("subscriber");

        final XRayVentPanel ui = new XRayVentPanel(subscriber, eventLoop, deviceCellRenderer);

        return new IceAppsContainer.IceApp() {

            @Override
            public String getId() {
                return AppType.XRay.getId();
            }

            @Override
            public String getName() {
                return AppType.XRay.getName();
            }

            @Override
            public Icon getIcon() {
                return AppType.XRay.getIcon();
            }

            @Override
            public Component getUI() {
                return ui;
            }

            @Override
            public void start(ApplicationContext context) {
                final EventLoop  eventLoop = (EventLoop)context.getBean("eventLoop");
                final Subscriber subscriber= (Subscriber)context.getBean("subscriber");
                ui.start(subscriber, eventLoop);
            }

            @Override
            public void stop() {
                ui.stop();
            }
        };
    }

}
