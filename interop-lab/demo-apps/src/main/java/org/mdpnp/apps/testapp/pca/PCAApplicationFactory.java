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
public class PCAApplicationFactory implements IceApplicationProvider {

    private final IceAppsContainer.AppType PCA =
            new  IceAppsContainer.AppType("pca", "Infusion Safety", "NOPCA", IceApplicationProvider.class.getResource("infusion-safety.png"), 0.75);

    @Override
    public IceAppsContainer.AppType getAppType() {
        return PCA;

    }

    @Override
    public IceAppsContainer.IceApp create(ApplicationContext parentContext) {

        final ScheduledExecutorService refreshScheduler = (ScheduledExecutorService) parentContext.getBean("refreshScheduler");
        final DeviceListModel nc = (DeviceListModel)parentContext.getBean("deviceListModel");
        final ice.InfusionObjectiveDataWriter objectiveWriter = (ice.InfusionObjectiveDataWriter) parentContext.getBean("objectiveWriter");

        final DeviceListCellRenderer deviceCellRenderer = new DeviceListCellRenderer(nc);

        final PCAPanel ui = new PCAPanel(refreshScheduler, objectiveWriter, deviceCellRenderer);
        ui.setOpaque(false);

        return new IceAppsContainer.IceApp() {

            @Override
            public String getId() {
                return PCA.getId();
            }

            @Override
            public String getName() {
                return PCA.getName();
            }

            @Override
            public Icon getIcon() {
                return getIcon();
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
