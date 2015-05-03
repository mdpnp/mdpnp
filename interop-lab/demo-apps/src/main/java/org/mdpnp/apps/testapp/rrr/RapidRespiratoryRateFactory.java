package org.mdpnp.apps.testapp.rrr;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

/**
 *
 */
public class RapidRespiratoryRateFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType RRR =
            new IceApplicationProvider.AppType("Respiratory Rate Calc", "NORRR", RapidRespiratoryRate.class.getResource("rrr.png"), 0.75, false);

    @Override
    public IceApplicationProvider.AppType getAppType() { return RRR;}

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {
        final EventLoop  eventLoop = (EventLoop)parentContext.getBean("eventLoop");
        final Subscriber subscriber= (Subscriber)parentContext.getBean("subscriber");
        final int        domainId  = (Integer)parentContext.getBean("domainId");
        final DeviceListModel deviceListModel = (DeviceListModel) parentContext.getBean("deviceListModel");
        final SampleArrayFxList sampleArrayList = parentContext.getBean("sampleArrayList", SampleArrayFxList.class);
        
        FXMLLoader loader = new FXMLLoader(RapidRespiratoryRate.class.getResource("RapidRespiratoryRate.fxml"));
        
        final Parent ui = loader.load();
        
        final RapidRespiratoryRate controller = ((RapidRespiratoryRate)loader.getController());

        controller.set(parentContext, domainId, eventLoop, subscriber, deviceListModel);
        controller.start(sampleArrayList);

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return RRR;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
            }

            @Override
            public void stop() {
            }

            @Override
            public void destroy() {
                controller.stop();
            }
        };
    }

}
