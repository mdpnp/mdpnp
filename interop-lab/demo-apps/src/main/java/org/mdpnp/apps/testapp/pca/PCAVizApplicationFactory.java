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

    private final IceAppsContainer.AppType PCAViz =
            new IceAppsContainer.AppType("pcaviz", "Data Visualization", "NOPCAVIZ", DataVisualization.class.getResource("data-viz.png"), 0.75);

    @Override
    public IceAppsContainer.AppType getAppType() {
        return PCAViz;

    }

    @Override
    public IceAppsContainer.IceApp create(ApplicationContext parentContext) {

        final ScheduledExecutorService refreshScheduler = (ScheduledExecutorService) parentContext.getBean("refreshScheduler");
        final DeviceListModel nc = (DeviceListModel)parentContext.getBean("deviceListModel");
        final ice.InfusionObjectiveDataWriter objectiveWriter = (ice.InfusionObjectiveDataWriter) parentContext.getBean("objectiveWriter");

        final DeviceListCellRenderer deviceCellRenderer = new DeviceListCellRenderer(nc);

        final DataVisualization ui =
                new DataVisualization(refreshScheduler, objectiveWriter, deviceCellRenderer);

        return new IceAppsContainer.IceApp() {

            @Override
            public String getId() {
                return PCAViz.getId();
            }

            @Override
            public String getName() {
                return PCAViz.getName();
            }

            @Override
            public Icon getIcon() {
                return PCAViz.getIcon();
            }

            @Override
            public Component getUI() {
                return ui;
            }

            @Override
            public void start(ApplicationContext context) {
                VitalModel vitalModel = (VitalModel)context.getBean("vitalModel");
                InfusionStatusInstanceModel pumpModel = (InfusionStatusInstanceModel)context.getBean("pumpModel");
                ui.setModel(vitalModel, pumpModel);
            }

            @Override
            public void stop() {
                ui.setModel(null, null);
            }
        };
    }
}
