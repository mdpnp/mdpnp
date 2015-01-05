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

    @Override
    public AppType getAppType() {
        return AppType.SimControl;

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
                return AppType.SimControl.getId();
            }

            @Override
            public String getName() {
                return AppType.SimControl.getName();
            }

            @Override
            public Icon getIcon() {
                return AppType.SimControl.getIcon();
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
