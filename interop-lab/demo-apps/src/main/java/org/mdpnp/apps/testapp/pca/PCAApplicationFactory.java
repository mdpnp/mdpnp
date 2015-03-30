package org.mdpnp.apps.testapp.pca;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;
import org.springframework.context.ApplicationContext;

/**
 *
 */
public class PCAApplicationFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType PCA =
            new  IceApplicationProvider.AppType("Infusion Safety", "NOPCA", PCAConfig.class.getResource("infusion-safety.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return PCA;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final ScheduledExecutorService refreshScheduler = (ScheduledExecutorService) parentContext.getBean("refreshScheduler");
        final ice.InfusionObjectiveDataWriter objectiveWriter = (ice.InfusionObjectiveDataWriter) parentContext.getBean("objectiveWriter");
//        final VitalModel vitalModel = (VitalModel) parentContext.getBean("vitalModel");
        DeviceListModel deviceListModel = (DeviceListModel) parentContext.getBean("deviceListModel");
//        final InfusionStatusInstanceModel pumpModel = (InfusionStatusInstanceModel) parentContext.getBean("pumpModel");
        FXMLLoader loader = new FXMLLoader(PCAPanel.class.getResource("PCAPanel.fxml"));
        Parent ui = loader.load();
        
        final PCAPanel pcaPanel = loader.getController();        
        
        pcaPanel.set(refreshScheduler, objectiveWriter, deviceListModel);

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
                VitalModel vitalModel = (VitalModel)context.getBean("vitalModel");
                InfusionStatusInstanceModel pumpModel = (InfusionStatusInstanceModel)context.getBean("pumpModel");
                pcaPanel.setModel(vitalModel, pumpModel);
            }

            @Override
            public void stop() {
                pcaPanel.setModel(null, null);
            }

            @Override
            public void destroy() {
            }
        };
    }
}
