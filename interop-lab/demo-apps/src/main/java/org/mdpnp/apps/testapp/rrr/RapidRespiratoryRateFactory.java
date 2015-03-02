package org.mdpnp.apps.testapp.rrr;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModel;
import org.springframework.context.ApplicationContext;

import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.Subscriber;

/**
 *
 */
public class RapidRespiratoryRateFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType RRR =
            new IceApplicationProvider.AppType("Respiratory Rate Calc", "NORRR", RapidRespiratoryRate.class.getResource("rrr.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() { return RRR;}

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {
        final EventLoop  eventLoop = (EventLoop)parentContext.getBean("eventLoop");
        final Subscriber subscriber= (Subscriber)parentContext.getBean("subscriber");
        final int        domainId  = (Integer)parentContext.getBean("domainId");

        SampleArrayInstanceModel capnoModel =  (SampleArrayInstanceModel)  parentContext.getBean("capnoModel");
        StringSeq params = new StringSeq();
        params.add("'"+rosetta.MDC_AWAY_CO2.VALUE+"'");
        params.add("'"+rosetta.MDC_IMPED_TTHOR.VALUE+"'");
        capnoModel.start(subscriber, eventLoop, "metric_id = %0 or metric_id = %1 ", params, QosProfiles.ice_library, QosProfiles.waveform_data);

        FXMLLoader loader = new FXMLLoader(RapidRespiratoryRate.class.getResource("RapidRespiratoryRate.fxml"));
        
        final Parent ui = loader.load();
        
        final RapidRespiratoryRate controller = ((RapidRespiratoryRate)loader.getController());

        controller.set(domainId, eventLoop, subscriber);

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
                SampleArrayInstanceModel capnoModel =  (SampleArrayInstanceModel)  context.getBean("capnoModel");
                controller.setModel(capnoModel);
            }

            @Override
            public void stop() {
                controller.setModel(null);
            }

            @Override
            public void destroy() {
            }
        };
    }

}
