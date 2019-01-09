package org.mdpnp.apps.testapp.export;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.rti.dds.subscription.Subscriber;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.PatientAssessmentFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

/**
 *
 */
public class DataCollectorAppFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType FileAdapter =
            new IceApplicationProvider.AppType("Data Recorder", "NOCSV",  DataCollectorAppFactory.class.getResource("database-server.png"), 0.75, false);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return FileAdapter;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final SampleArrayFxList sampleArrayList = parentContext.getBean("sampleArrayList", SampleArrayFxList.class);
        final NumericFxList numericList = parentContext.getBean("numericList", NumericFxList.class);
        final DeviceListModel deviceListModel = parentContext.getBean("deviceListModel", DeviceListModel.class);
        final PatientAssessmentFxList paList = parentContext.getBean("assessmentList", PatientAssessmentFxList.class);

        final EventLoop eventLoop = parentContext.getBean("eventLoop", EventLoop.class);
        final Subscriber subscriber = parentContext.getBean("subscriber", Subscriber.class);

        final DataCollector[] dataCollectors = {
                new SampleArrayDataCollector(sampleArrayList),
                new NumericsDataCollector(numericList),
                new PatientAssessmentDataCollector(paList)
        };

        final URL[] persisters = {
                CSVPersister.class.getResource("CSVPersister.fxml"),
                JdbcPersister.class.getResource("JdbcPersister.fxml"),
                VerilogVCDPersister.class.getResource("VerilogVCDPersister.fxml"),
                MongoPersister.class.getResource("MongoPersister.fxml")
        };

        FXMLLoader loader = new FXMLLoader(DataCollectorApp.class.getResource("DataCollectorApp.fxml"));
        final Parent ui = loader.load();
        
        final DataCollectorApp controller = loader.getController();

        final MDSHandler mdsHandler = new MDSHandler(eventLoop, subscriber.get_participant());
        for(DataCollector dc : dataCollectors) {
            mdsHandler.addConnectivityListener(dc);
        }
        mdsHandler.start();

        controller.set(deviceListModel, dataCollectors, persisters);

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
                for(DataCollector dc : dataCollectors) {
                    mdsHandler.removeConnectivityListener(dc);
                    dc.destroy();
                }
                mdsHandler.shutdown();
            }
        };
    }

    public static abstract class PersisterUIController {

        public abstract String getName();
        
        public abstract void setup();

        public abstract void stop() throws Exception;

        public abstract boolean start() throws Exception;
        
        public void setRawDateFormat(boolean raw) {};
    }
}
