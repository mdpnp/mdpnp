package org.mdpnp.apps.testapp.export;

import java.io.IOException;

import com.rti.dds.subscription.Subscriber;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

/**
 *
 */
public class FileAdapterApplicationFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType FileAdapter =
            new IceApplicationProvider.AppType("Data Recorder", "NOCSV",  FileAdapterApplicationFactory.class.getResource("database-server.png"), 0.75, false);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return FileAdapter;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final SampleArrayFxList sampleArrayList = parentContext.getBean("sampleArrayList", SampleArrayFxList.class);
        final NumericFxList numericList = parentContext.getBean("numericList", NumericFxList.class);
        final DeviceListModel deviceListModel = parentContext.getBean("deviceListModel", DeviceListModel.class);

        final EventLoop eventLoop = parentContext.getBean("eventLoop", EventLoop.class);
        final Subscriber subscriber = parentContext.getBean("subscriber", Subscriber.class);

        final DataCollector dataCollector = new DataCollector(sampleArrayList, numericList);
        
        FXMLLoader loader = new FXMLLoader(DataCollectorApp.class.getResource("DataCollectorApp.fxml"));
        final Parent ui = loader.load();
        
        final DataCollectorApp controller = loader.getController();

        final MDSHandler mdsHandler = new MDSHandler(eventLoop, subscriber.get_participant());
        mdsHandler.addConnectivityListener(dataCollector);
        mdsHandler.start();

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
            }

            @Override
            public void stop() {
            }

            @Override
            public void destroy() throws Exception {
                controller.stop();
                dataCollector.destroy();
                mdsHandler.shutdown();
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
