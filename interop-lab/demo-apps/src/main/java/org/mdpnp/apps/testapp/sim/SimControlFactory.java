package org.mdpnp.apps.testapp.sim;

import java.io.IOException;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 */
public class SimControlFactory implements IceApplicationProvider {

    private static final Logger log = LoggerFactory.getLogger(SimControlFactory.class);

    private final IceApplicationProvider.AppType SimControl =
            new IceApplicationProvider.AppType("Simulation Control", "NOSIM", IceApplicationProvider.class.getResource("sim.png"), 0.75, false);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return SimControl;

    }

    @Override
    public IceApp create(ApplicationContext parentContext) throws IOException {
        return new SimControlApplication((AbstractApplicationContext)parentContext);
    }

    class SimControlApplication implements IceApp
    {
        final Stage dialog;
        final Parent ui;
        final ClassPathXmlApplicationContext ctx;
        final SimControl controller;

        SimControlApplication(final AbstractApplicationContext parentContext) throws IOException {

            String contextPath = "classpath*:/org/mdpnp/apps/testapp/sim/SimControlAppContext.xml";

            ctx = new ClassPathXmlApplicationContext(new String[] { contextPath }, parentContext);
            parentContext.addApplicationListener(new ApplicationListener<ContextClosedEvent>()
            {
                @Override
                public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
                    // only care to trap parent close events to kill the child context
                    if(parentContext == contextClosedEvent.getApplicationContext()) {
                        log.info("Handle parent context shutdown event");
                        ctx.close();
                    }
                }
            });

            ui = ctx.getBean(Parent.class);
            controller =  ctx.getBean(SimControl.class);

            dialog = new Stage(StageStyle.DECORATED);
            dialog.setTitle("Simulation Control");
//            dialog.setAlwaysOnTop(true);
            Scene scene = new Scene(ui);
            dialog.setScene(scene);
            dialog.sizeToScene();

        }

        @Override
        public AppType getDescriptor() {
            return SimControl;
        }

        /**
         * @return null to indicate that this is a self-managed dialog that does not want to me managed by the app container.
         */
        @Override
        public Parent getUI() {
            return null;
        }

        @Override
        public void activate(ApplicationContext context) {
            dialog.show();
        }

        @Override
        public void stop() {
            dialog.hide();

        }

        @Override
        public void destroy() throws Exception {
            ctx.destroy();
        }

    }
}
