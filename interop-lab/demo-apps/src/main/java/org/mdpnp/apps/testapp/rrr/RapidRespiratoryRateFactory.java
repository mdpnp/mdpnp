package org.mdpnp.apps.testapp.rrr;

import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.Subscriber;
import org.mdpnp.apps.testapp.*;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.InstanceModel;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModel;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class RapidRespiratoryRateFactory implements IceApplicationProvider {

    private final IceAppsContainer.AppType RRR =
            new IceAppsContainer.AppType("rrr", "Respiratory Rate Calc", "NORRR", RapidRespiratoryRate.class.getResource("rrr.png"), 0.75);

    @Override
    public IceAppsContainer.AppType getAppType() { return RRR;}

    @Override
    public IceAppsContainer.IceApp create(ApplicationContext parentContext) {

        final DeviceListModel nc = (DeviceListModel)parentContext.getBean("deviceListModel");
        final DeviceListCellRenderer deviceCellRenderer = new DeviceListCellRenderer(nc);

        final EventLoop  eventLoop = (EventLoop)parentContext.getBean("eventLoop");
        final Subscriber subscriber= (Subscriber)parentContext.getBean("subscriber");
        final int        domainId  = (Integer)parentContext.getBean("domainId");

        InstanceModel capnoModel =  (InstanceModel)  parentContext.getBean("capnoModel");
        StringSeq params = new StringSeq();
        params.add("'"+rosetta.MDC_AWAY_CO2.VALUE+"'");
        params.add("'"+rosetta.MDC_IMPED_TTHOR.VALUE+"'");
        capnoModel.start(subscriber, eventLoop, "metric_id = %0 or metric_id = %1 ", params, QosProfiles.ice_library, QosProfiles.waveform_data);

        final RapidRespiratoryRate ui = new RapidRespiratoryRate(domainId, eventLoop, subscriber, deviceCellRenderer);

        return new IceAppsContainer.IceApp() {

            @Override
            public String getId() {
                return RRR.getId();
            }

            @Override
            public String getName() {
                return RRR.getName();
            }

            @Override
            public Icon getIcon() {
                return RRR.getIcon();
            }

            @Override
            public Component getUI() {
                return ui;
            }

            @Override
            public void start(ApplicationContext context) {
                SampleArrayInstanceModel capnoModel =  (SampleArrayInstanceModel)  context.getBean("capnoModel");
                ui.setModel(capnoModel);
            }

            @Override
            public void stop() {
                ui.setModel(null);
            }
        };
    }

}
