package org.mdpnp.apps.testapp.rbs;

import com.rti.dds.publication.Publisher;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.pca.PCAConfig;
import org.mdpnp.apps.testapp.pca.PCAPanel;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelImpl;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 */
public class RBSApplicationFactory implements IceApplicationProvider {

    private final AppType RBS =
            new  AppType("Rule-Based Safety", "NORBS", RBSConfig.class.getResource("rules-safety.png"), 0.75, false);

    @Override
    public AppType getAppType() {
        return RBS;

    }

    @Override
    public IceApp create(ApplicationContext parentContext) throws IOException {

        final ScheduledExecutorService refreshScheduler = (ScheduledExecutorService) parentContext.getBean("refreshScheduler");
        final ice.InfusionObjectiveDataWriter objectiveWriter = (ice.InfusionObjectiveDataWriter) parentContext.getBean("objectiveWriter");
        final DeviceListModel deviceListModel = (DeviceListModel) parentContext.getBean("deviceListModel");
        final InfusionStatusFxList infusionStatusList = parentContext.getBean("infusionStatusList", InfusionStatusFxList.class);
        final Publisher publisher = parentContext.getBean("publisher", Publisher.class);
        final EventLoop eventLoop = parentContext.getBean("eventLoop", EventLoop.class);
        final ObservableList<NumericFx> numericList = parentContext.getBean("numericList", ObservableList.class);

        FXMLLoader loader = new FXMLLoader(RBSPanel.class.getResource("RBSPanel.fxml"));
        Parent ui = loader.load();

        final RBSPanel rbsPanel = loader.getController();

        rbsPanel.set(refreshScheduler, objectiveWriter, deviceListModel, infusionStatusList);


        final VitalModel vitalModel = new VitalModelImpl(deviceListModel, numericList);
        vitalModel.start(publisher, eventLoop);

        return new IceApp() {

            @Override
            public AppType getDescriptor() {
                return RBS;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                rbsPanel.setModel(vitalModel);
            }

            @Override
            public void stop() {
                rbsPanel.setModel(null);
                vitalModel.clear();
            }

            @Override
            public void destroy() {
                vitalModel.stop();
            }
        };
    }
}
