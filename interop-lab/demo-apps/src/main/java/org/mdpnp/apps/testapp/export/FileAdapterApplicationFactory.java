package org.mdpnp.apps.testapp.export;

import com.rti.dds.domain.DomainParticipant;
import org.mdpnp.apps.testapp.*;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 */
public class FileAdapterApplicationFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType FileAdapter =
            new IceApplicationProvider.AppType("File Exporter", "NOCSV",  DataVisualization.class.getResource("csv-text.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return FileAdapter;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) {

        final DomainParticipant participant = (DomainParticipant)parentContext.getBean("domainParticipant");

        final DeviceListModel deviceModel = (DeviceListModel)parentContext.getBean("deviceListModel");

        final DataCollector dataCollector = new DataCollector(participant);

        final DataCollectorApp ui = new DataCollectorApp(deviceModel, dataCollector);

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return FileAdapter;
            }

            @Override
            public Component getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                dataCollector.start();
            }

            @Override
            public void stop() {
                try {
                    dataCollector.stop();
                } catch (Exception ex) {
                    throw new IllegalStateException("Failed to stop data collector", ex);
                }
            }

            @Override
            public void destroy() throws Exception {
                ui.stop();
            }
        };
    }


    public static abstract class PersisterUI extends JPanel implements DataCollector.DataSampleEventListener  {

        public abstract String getName();

        public abstract void stop() throws Exception;

        public abstract boolean start() throws Exception;
    }
}
