package org.mdpnp.apps.testapp.pca;

import org.mdpnp.apps.testapp.*;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 */
public class PCAVizApplicationFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType PCAViz =
            new IceApplicationProvider.AppType("Data Visualization", "NOPCAVIZ", DataVisualization.class.getResource("data-viz.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return PCAViz;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) {

        final ScheduledExecutorService refreshScheduler = (ScheduledExecutorService) parentContext.getBean("refreshScheduler");
        final DeviceListModel nc = (DeviceListModel)parentContext.getBean("deviceListModel");
        final ice.InfusionObjectiveDataWriter objectiveWriter = (ice.InfusionObjectiveDataWriter) parentContext.getBean("objectiveWriter");

        final DeviceListCellRenderer deviceCellRenderer = new DeviceListCellRenderer(nc);

        final DataVisualization ui =
                new DataVisualization(refreshScheduler, objectiveWriter, deviceCellRenderer, new VitalMonitoring(refreshScheduler));

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return PCAViz;
            }

            @Override
            public Component getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                VitalModel vitalModel = (VitalModel)context.getBean("vitalModel");
                InfusionStatusInstanceModel pumpModel = (InfusionStatusInstanceModel)context.getBean("pumpModel");
                ui.setModel(vitalModel, pumpModel);
            }

            @Override
            public void stop() {
                ui.setModel(null, null);
            }

            @Override
            public void destroy() {
            }
        };
    }
}
