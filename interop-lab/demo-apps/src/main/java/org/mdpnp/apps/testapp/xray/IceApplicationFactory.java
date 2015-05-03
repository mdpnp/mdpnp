package org.mdpnp.apps.testapp.xray;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

/**
 *
 */
public class IceApplicationFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType XRay =
            new IceApplicationProvider.AppType("X-Ray Ventilator Sync", "NOXRAYVENT", XRayVentPanel.class.getResource("xray-vent.png"), 0.75, false);

    @Override
    public IceApplicationProvider.AppType getAppType() { return XRay;}

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {
        final DeviceListModel deviceListModel = parentContext.getBean("deviceListModel", DeviceListModel.class);
        final NumericFxList numericList = parentContext.getBean("numericList", NumericFxList.class);
        final SampleArrayFxList sampleArrayList = parentContext.getBean("sampleArrayList", SampleArrayFxList.class);
        FXMLLoader loader = new FXMLLoader(XRayVentPanel.class.getResource("XRayVentPanel.fxml"));
        
        final Parent ui = loader.load();
        
        final XRayVentPanel controller = ((XRayVentPanel)loader.getController());

        controller.set(deviceListModel, numericList, sampleArrayList);

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return XRay;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                final EventLoop  eventLoop = (EventLoop)context.getBean("eventLoop");
                final Subscriber subscriber= (Subscriber)context.getBean("subscriber");
                controller.start(subscriber, eventLoop);
            }

            @Override
            public void stop() {
                controller.stop();
            }

            @Override
            public void destroy() {
                controller.destroy();
            }
        };
    }

}
