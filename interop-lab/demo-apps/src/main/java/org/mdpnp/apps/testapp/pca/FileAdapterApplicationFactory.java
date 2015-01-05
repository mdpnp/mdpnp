package org.mdpnp.apps.testapp.pca;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import org.mdpnp.apps.testapp.*;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 */
public class FileAdapterApplicationFactory implements IceApplicationProvider {

    private final IceAppsContainer.AppType FileAdapter =
            new IceAppsContainer.AppType("file", "CSV File Exporter", "NOCSV",  DataVisualization.class.getResource("csv-text.png"), 0.75);

    @Override
    public IceAppsContainer.AppType getAppType() {
        return FileAdapter;

    }

    @Override
    public IceAppsContainer.IceApp create(ApplicationContext parentContext) {

        final ScheduledExecutorService refreshScheduler = (ScheduledExecutorService) parentContext.getBean("refreshScheduler");
        final DeviceListModel nc = (DeviceListModel)parentContext.getBean("deviceListModel");
        final ice.InfusionObjectiveDataWriter objectiveWriter = (ice.InfusionObjectiveDataWriter) parentContext.getBean("objectiveWriter");

        final DeviceListCellRenderer deviceCellRenderer = new DeviceListCellRenderer(nc);

        final DataVisualization ui =
                new DataVisualization(refreshScheduler, objectiveWriter, deviceCellRenderer, new VitalSimpleTable(refreshScheduler));

        return new IceAppsContainer.IceApp() {

            @Override
            public String getId() {
                return FileAdapter.getId();
            }

            @Override
            public String getName() {
                return FileAdapter.getName();
            }

            @Override
            public Icon getIcon() {
                return FileAdapter.getIcon();
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
