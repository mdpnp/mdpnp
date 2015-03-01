package org.mdpnp.apps.testapp.sim;

import com.rti.dds.domain.DomainParticipant;

import org.mdpnp.apps.testapp.*;
import org.springframework.context.ApplicationContext;

import javafx.scene.Parent;

import javax.swing.*;

import java.awt.*;

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
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) {

        final DomainParticipant participant = (DomainParticipant) parentContext.getBean("domainParticipant");

        SimControl simControl = new SimControl(participant);

        final JFrame ui = new JFrame("Sim Control");
        ui.getContentPane().add(new JScrollPane(simControl));
        ui.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        ui.setAlwaysOnTop(true);
        ui.pack();
        Dimension d = new Dimension();
        ui.getSize(d);
        d.width = 2 * d.width;
        ui.setSize(d);


        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return SimControl;
            }

            @Override
            public Parent getUI() {
                return null;
            }

            @Override
            public void activate(ApplicationContext context) {
            }

            @Override
            public void stop() {
            }

            @Override
            public void destroy() {
                ui.dispose();
            }
        };
    }
}
