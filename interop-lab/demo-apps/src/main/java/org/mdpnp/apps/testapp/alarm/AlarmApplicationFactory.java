package org.mdpnp.apps.testapp.alarm;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

public class AlarmApplicationFactory implements IceApplicationProvider {
    private final IceApplicationProvider.AppType AlarmApplication = new IceApplicationProvider.AppType("Alarm History", "NOALARM",
            (URL) AlarmApplicationFactory.class.getResource("alarm.png"), 0.75, false);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return AlarmApplication;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final Subscriber subscriber = (Subscriber) parentContext.getBean("subscriber");

        final EventLoop eventLoop = (EventLoop) parentContext.getBean("eventLoop");

        FXMLLoader loader = new FXMLLoader(AlarmApplication.class.getResource("AlarmApplication.fxml"));

        final Parent ui = loader.load();

        final AlarmApplication controller = ((AlarmApplication) loader.getController());

        controller.start(eventLoop, subscriber);

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return AlarmApplication;
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
            }
        };
    }

}
