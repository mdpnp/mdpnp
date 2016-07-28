package org.mdpnp.apps.testapp.chart;

import java.io.IOException;

import com.google.common.eventbus.EventBus;
import himss.PatientAssessmentDataWriter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.springframework.context.ApplicationContext;

public class ChartApplicationFactory implements IceApplicationProvider {
    private final IceApplicationProvider.AppType ChartApplication = new IceApplicationProvider.AppType("Chart", "NOCHART",
            ChartApplicationFactory.class.getResource("chart.png"), 0.75, false);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return ChartApplication;

    }


    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        String fxml = System.getProperty("ChartApplication.fxml", "Chart2Application.fxml");
        FXMLLoader loader = new FXMLLoader(ChartApplication.class.getResource(fxml));

        final Parent ui = loader.load();

        final String udi = parentContext.getBean("supervisorUdi", String.class);

        final VitalModel model = (VitalModel) parentContext.getBean("vitalModel");
        final WithVitalModel controller = loader.getController();
        controller.setModel(model);

        final EventBus eventBus = parentContext.getBean("eventBus", EventBus.class);
        eventBus.register(controller);

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return ChartApplication;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                if(controller instanceof WithPatientAssessmentDataWriter) {
                    final PatientAssessmentDataWriter writer = (PatientAssessmentDataWriter) parentContext.getBean("patientAssessmentWriter");
                    ((WithPatientAssessmentDataWriter)controller).configurePatientAssessmentWriter(udi, writer);
                }
            }

            @Override
            public void stop() {
                if(controller instanceof WithPatientAssessmentDataWriter) {
                    ((WithPatientAssessmentDataWriter)controller).configurePatientAssessmentWriter(null, null);
                }
            }

            @Override
            public void destroy() throws Exception {
                eventBus.unregister(controller);
            }
        };
    }

    public interface WithVitalModel {
        void setModel(VitalModel vitalModel);
    }

    public interface WithPatientAssessmentDataWriter {
        void configurePatientAssessmentWriter(String udi, PatientAssessmentDataWriter writer);
    }


}
