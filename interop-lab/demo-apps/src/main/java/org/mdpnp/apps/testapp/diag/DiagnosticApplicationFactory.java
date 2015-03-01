package org.mdpnp.apps.testapp.diag;

import java.awt.Component;
import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javax.swing.JPanel;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.alarm.AlarmApplication;
import org.mdpnp.apps.testapp.export.DataCollector;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

public class DiagnosticApplicationFactory implements IceApplicationProvider {
    private final IceApplicationProvider.AppType DiagnosticApplication =
            new IceApplicationProvider.AppType("System Explorer", "NODIAG", (URL)DiagnosticApplicationFactory.class.getResource("diag.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return DiagnosticApplication;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final Subscriber subscriber = (Subscriber)parentContext.getBean("subscriber");

        final EventLoop eventLoop = (EventLoop)parentContext.getBean("eventLoop");

        final Diagnostic diagnostic = new Diagnostic(subscriber, eventLoop);

        FXMLLoader loader = new FXMLLoader(DiagnosticApplication.class.getResource("DiagnosticApplication.fxml"));
        
        final Parent ui = loader.load();
        
        final DiagnosticApplication controller = ((DiagnosticApplication)loader.getController());
        
        controller.setModel(diagnostic);

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return DiagnosticApplication;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                diagnostic.start();
            }

            @Override
            public void stop() {
                diagnostic.stop();
//                try {
////                    emitter.stop();
//                } catch (Exception ex) {
//                    throw new IllegalStateException("Failed to stop data collector", ex);
//                }
            }

            @Override
            public void destroy() throws Exception {
                controller.stop();
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
