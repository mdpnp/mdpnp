package org.mdpnp.apps.testapp.sim;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.springframework.context.ApplicationContext;

import com.rti.dds.domain.DomainParticipant;

/**
 *
 */
public class SimControlFactory implements IceApplicationProvider {

    private final IceApplicationProvider.AppType SimControl =
            new IceApplicationProvider.AppType("Simulation Control", "NOSIM", IceApplicationProvider.class.getResource("sim.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return SimControl;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final DomainParticipant participant = (DomainParticipant) parentContext.getBean("domainParticipant");

        FXMLLoader loader = new FXMLLoader(SimControl.class.getResource("SimControl.fxml"));
        
        final Parent ui = loader.load();
        
        final SimControl controller = ((SimControl)loader.getController());
        controller.setup(participant);

        
        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return SimControl;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                
//                final Stage dialog = new Stage(StageStyle.UTILITY);
//                dialog.setAlwaysOnTop(true);
//                Scene scene = new Scene(ui);
//                dialog.setScene(scene);
//                dialog.sizeToScene();
//                dialog.show();

                controller.start();
            }

            @Override
            public void stop() {
                controller.stop();
            }

            @Override
            public void destroy() {
                controller.tearDown();
            }
        };
    }
}
