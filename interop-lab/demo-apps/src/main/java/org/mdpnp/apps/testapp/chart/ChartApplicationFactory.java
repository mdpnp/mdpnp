package org.mdpnp.apps.testapp.chart;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.springframework.context.ApplicationContext;

public class ChartApplicationFactory implements IceApplicationProvider {
    private final IceApplicationProvider.AppType ChartApplication = new IceApplicationProvider.AppType("Chart", "NOCHART",
            ChartApplicationFactory.class.getResource("chart.png"), 0.75, false);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return ChartApplication;

    }


    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final VitalModel model = (VitalModel) parentContext.getBean("vitalModel");

        String fxml = System.getProperty("ChartApplication.fxml", "ChartApplication.fxml");
        FXMLLoader loader = new FXMLLoader(ChartApplication.class.getResource(fxml));

        final Parent ui = loader.load();

        final ChartApplicationFactory.WithVitalModel controller = loader.getController();
        controller.setModel(model);
        
        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return ChartApplication;
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

            }
        };
    }

    public interface WithVitalModel {
        void setModel(VitalModel vitalModel);
    }

}
