package org.mdpnp.apps.testapp.patient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.devices.MDSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.List;


/**
 *
 */
public class PatientApplicationFactory implements IceApplicationProvider {

    private static final Logger log = LoggerFactory.getLogger(PatientApplicationFactory.class);

    private final AppType appType =
            new AppType("Patient ID", "NOPATIENT",  PatientApplicationFactory.class.getResource("patient.png"), 0.75, true);

    @Override
    public AppType getAppType() {
        return appType;

    }
    @Override
    public IceApp create(ApplicationContext parentContext) throws IOException {
        return new PatientApplication((AbstractApplicationContext)parentContext);
    }

    class PatientApplication implements IceApp
    {
        final Parent ui;
        final ClassPathXmlApplicationContext ctx;
        final PatientInfoController controller;

        PatientApplication(final AbstractApplicationContext parentContext) throws IOException {

            String contextPath = "classpath*:/org/mdpnp/apps/testapp/patient/PatientAppContext.xml";

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
            controller =  ctx.getBean(PatientInfoController.class);
            ctx.getBean("wildcardMdsConnectivity", MDSHandler.class).start();
        }

        @Override
        public AppType getDescriptor() {
            return appType;
        }

        @Override
        public Parent getUI() {
            return ui;
        }

        void addDeviceAssociation(Device d , PatientInfo p)  {
            controller.addDeviceAssociation(d, p);
        }

        void handleDeviceLifecycleEvent(Device d, boolean p) {
            controller.handleDeviceLifecycleEvent(d, p);
        }

        void setConnectHandler(EventHandler<ActionEvent> a) {
            controller.setConnectHandler(a);
        }

        PatientInfo getSelectedPatient() {
            return controller.getSelectedPatient();
        }

        Device getSelectedDevice() {
            return controller.getSelectedDevice();
        }

        @Override
        public void activate(ApplicationContext context) {

        }

        @Override
        public void stop() {

        }

        @Override
        public void destroy() throws Exception {
            ctx.destroy();
        }

    }

    public interface EMRFacade {
        List<PatientInfo> getPatients();
        boolean createPatient(PatientInfo p);

        void deleteDevicePatientAssociation(DevicePatientAssociation assoc);
        DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc);
    }


}
