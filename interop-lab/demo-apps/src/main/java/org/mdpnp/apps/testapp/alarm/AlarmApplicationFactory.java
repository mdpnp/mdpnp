package org.mdpnp.apps.testapp.alarm;

import java.awt.Component;
import java.net.URL;

import javax.swing.JPanel;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.export.DataCollector;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

public class AlarmApplicationFactory implements IceApplicationProvider {
    private final IceApplicationProvider.AppType AlarmApplication =
            new IceApplicationProvider.AppType("Alarm History", "NOALARM", (URL)AlarmApplicationFactory.class.getResource("alarm.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return AlarmApplication;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) {

        final Subscriber subscriber = (Subscriber)parentContext.getBean("subscriber");

        final EventLoop eventLoop = (EventLoop)parentContext.getBean("eventLoop");
        
        final AlarmHistoryModel model = new AlarmHistoryModel(ice.PatientAlertTopic.VALUE, ice.TechnicalAlertTopic.VALUE);

        final AlarmApplication ui = new AlarmApplication();
        
        ui.setModel(model);

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return AlarmApplication;
            }

            @Override
            public Component getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                model.start(subscriber, eventLoop);
            }

            @Override
            public void stop() {
                model.stop();
            }

            @Override
            public void destroy() throws Exception {
                ui.stop();
            }
        };
    }


    @SuppressWarnings("serial")
    public static abstract class PersisterUI extends JPanel implements DataCollector.DataSampleEventListener  {

        public abstract String getName();

        public abstract void stop() throws Exception;

        public abstract boolean start() throws Exception;
    }

}
