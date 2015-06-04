package org.mdpnp.apps.testapp.pca;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.springframework.context.ApplicationContext;

/**
 *
 */
public class PCAVizApplicationFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType PCAViz =
            new IceApplicationProvider.AppType("Data Visualization", "NOPCAVIZ", VitalMonitoring.class.getResource("data-viz.png"), 0.75, false);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return PCAViz;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {
        FXMLLoader loader = new FXMLLoader(VitalMonitoring.class.getResource("VitalMonitoring.fxml"));
        final Parent ui = loader.load();
        final VitalMonitoring vitalMonitoring = loader.getController(); 
        vitalMonitoring.setup();
        
        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return PCAViz;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                VitalModel vitalModel = (VitalModel)context.getBean("vitalModel");
                vitalMonitoring.setModel(vitalModel);
            }

            @Override
            public void stop() {
                vitalMonitoring.setModel(null);
            }

            @Override
            public void destroy() {
            }
        };
    }
}
