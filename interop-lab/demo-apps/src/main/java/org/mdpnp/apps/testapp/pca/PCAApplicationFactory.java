package org.mdpnp.apps.testapp.pca;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;
import org.springframework.context.ApplicationContext;

/**
 *
 */
public class PCAApplicationFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType PCA =
            new  IceApplicationProvider.AppType("Infusion Safety", "NOPCA", IceApplicationProvider.class.getResource("infusion-safety.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return PCA;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final ScheduledExecutorService refreshScheduler = (ScheduledExecutorService) parentContext.getBean("refreshScheduler");
        final ice.InfusionObjectiveDataWriter objectiveWriter = (ice.InfusionObjectiveDataWriter) parentContext.getBean("objectiveWriter");

        
        FXMLLoader loader = new FXMLLoader(PCAPanel.class.getResource("PCAPanel.fxml"));
        Parent ui = loader.load();
        
        final PCAPanel controller = loader.getController();
        controller.set(refreshScheduler, objectiveWriter);

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
                controller.setModel(vitalModel, pumpModel);
            }

            @Override
            public void stop() {
                controller.setModel(null, null);
            }

            @Override
            public void destroy() {
            }
        };
    }
}
