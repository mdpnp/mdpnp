package org.mdpnp.apps.testapp.export;

import java.awt.Component;

import javax.swing.JPanel;

import org.mdpnp.apps.testapp.DataVisualization;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

/**
 *
 */
public class FileAdapterApplicationFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType FileAdapter =
            new IceApplicationProvider.AppType("Data Recorder", "NOCSV",  DataVisualization.class.getResource("database-server.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return FileAdapter;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) {

        final Subscriber participant = (Subscriber)parentContext.getBean("subscriber");

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


    @SuppressWarnings("serial")
    public static abstract class PersisterUI extends JPanel implements DataCollector.DataSampleEventListener  {

        public abstract String getName();

        public abstract void stop() throws Exception;

        public abstract boolean start() throws Exception;
    }
}
