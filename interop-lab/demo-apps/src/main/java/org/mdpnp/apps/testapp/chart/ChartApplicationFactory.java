package org.mdpnp.apps.testapp.chart;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.springframework.context.ApplicationContext;

public class ChartApplicationFactory implements IceApplicationProvider {
    private final IceApplicationProvider.AppType ChartApplication = new IceApplicationProvider.AppType("Chart", "NOCHART",
            (URL) null /*ChartApplicationFactory.class.getResource("chart.png")*/, 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return ChartApplication;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

//        final Subscriber subscriber = (Subscriber) parentContext.getBean("subscriber");

//        final EventLoop eventLoop = (EventLoop) parentContext.getBean("eventLoop");

        final VitalModel model = (VitalModel) parentContext.getBean("vitalModel");

        FXMLLoader loader = new FXMLLoader(ChartApplication.class.getResource("ChartApplication.fxml"));

        final Parent ui = loader.load();

        final ChartApplication controller = ((ChartApplication) loader.getController());

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
                model.stop();
            }
        };
    }

}
