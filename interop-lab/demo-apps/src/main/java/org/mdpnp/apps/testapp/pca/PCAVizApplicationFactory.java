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

    @Override
    public AppType getAppType() {
        return AppType.PCAViz;

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
                return AppType.PCAViz.getId();
            }

            @Override
            public String getName() {
                return AppType.PCAViz.getName();
            }

            @Override
            public Icon getIcon() {
                return AppType.PCAViz.getIcon();
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
