package org.mdpnp.apps.testapp.hl7;

import java.awt.Component;
import java.net.URL;

import javax.swing.JPanel;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.export.DataCollector;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

public class HL7ApplicationFactory implements IceApplicationProvider {
    private final IceApplicationProvider.AppType HL7Application =
            new IceApplicationProvider.AppType("HL7 Exporter", "NOHL7", (URL)HL7ApplicationFactory.class.getResource("hl7.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return HL7Application;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) {

        final Subscriber subscriber = (Subscriber)parentContext.getBean("subscriber");

        final EventLoop eventLoop = (EventLoop)parentContext.getBean("eventLoop");

        final HL7Emitter emitter = new HL7Emitter(subscriber, eventLoop);

        final HL7Application ui = new HL7Application();
        
        ui.setModel(emitter);

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return HL7Application;
            }

            @Override
            public Component getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
//                emitter.start();
            }

            @Override
            public void stop() {
//                try {
////                    emitter.stop();
//                } catch (Exception ex) {
//                    throw new IllegalStateException("Failed to stop data collector", ex);
//                }
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
