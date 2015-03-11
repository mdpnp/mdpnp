package org.mdpnp.apps.testapp.export;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.testapp.DataVisualization;
import org.mdpnp.apps.testapp.DeviceListModelImpl;
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
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final Subscriber subscriber = (Subscriber)parentContext.getBean("subscriber");
        final DeviceListModelImpl deviceListModel = (DeviceListModelImpl) parentContext.getBean("deviceListModel");
        final DataCollector dataCollector = new DataCollector(subscriber);
        
        FXMLLoader loader = new FXMLLoader(DataCollectorApp.class.getResource("DataCollectorApp.fxml"));
        final Parent ui = loader.load();
        
        final DataCollectorApp controller = loader.getController();
        
        controller.set(dataCollector, deviceListModel);

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return FileAdapter;
            }

            @Override
            public Parent getUI() {
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
                controller.stop();
            }
        };
    }

    public static abstract class PersisterUIController implements DataCollector.DataSampleEventListener  {

        public abstract String getName();
        
        public abstract void setup();

        public abstract void stop() throws Exception;

        public abstract boolean start() throws Exception;
    }
}
