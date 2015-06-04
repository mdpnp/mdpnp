package org.mdpnp.apps.testapp.pca;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

/**
 *
 */
public class PCAApplicationFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType PCA =
            new  IceApplicationProvider.AppType("Infusion Safety", "NOPCA", PCAConfig.class.getResource("infusion-safety.png"), 0.75, false);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return PCA;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final ScheduledExecutorService refreshScheduler = (ScheduledExecutorService) parentContext.getBean("refreshScheduler");
        final ice.InfusionObjectiveDataWriter objectiveWriter = (ice.InfusionObjectiveDataWriter) parentContext.getBean("objectiveWriter");
        final VitalModel vitalModel = parentContext.getBean("vitalModel", VitalModel.class);
        final DeviceListModel deviceListModel = (DeviceListModel) parentContext.getBean("deviceListModel");
        final InfusionStatusFxList infusionStatusList = parentContext.getBean("infusionStatusList", InfusionStatusFxList.class);
        
        FXMLLoader loader = new FXMLLoader(PCAPanel.class.getResource("PCAPanel.fxml"));
        Parent ui = loader.load();
        
        final PCAPanel pcaPanel = loader.getController();        
        
        pcaPanel.set(refreshScheduler, objectiveWriter, deviceListModel, infusionStatusList);
        

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return PCA;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                pcaPanel.setModel(vitalModel);
            }

            @Override
            public void stop() {
                pcaPanel.setModel(null);
            }

            @Override
            public void destroy() {
            }
        };
    }
}
