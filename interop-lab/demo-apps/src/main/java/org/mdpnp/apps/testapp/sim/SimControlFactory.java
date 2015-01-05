package org.mdpnp.apps.testapp.sim;

import com.rti.dds.domain.DomainParticipant;
import org.mdpnp.apps.testapp.*;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class SimControlFactory implements IceApplicationProvider {

    private final IceAppsContainer.AppType SimControl =
            new IceAppsContainer.AppType("sim", "Simulation Control", "NOSIM", IceApplicationProvider.class.getResource("sim.png"), 0.75);

    @Override
    public IceAppsContainer.AppType getAppType() {
        return SimControl;

    }

    @Override
    public IceAppsContainer.IceApp create(ApplicationContext parentContext) {

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


        return new IceAppsContainer.IceApp() {

            @Override
            public String getId() {
                return SimControl.getId();
            }

            @Override
            public String getName() {
                return SimControl.getName();
            }

            @Override
            public Icon getIcon() {
                return SimControl.getIcon();
            }

            @Override
            public Component getUI() {
                return ui;
            }

            @Override
            public void start(ApplicationContext context) {
            }

            @Override
            public void stop() {
            }
        };
    }
}
