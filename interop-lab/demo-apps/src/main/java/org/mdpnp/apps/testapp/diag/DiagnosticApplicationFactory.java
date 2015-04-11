package org.mdpnp.apps.testapp.diag;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.fxbeans.AlertFxList;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.validate.ValidationOracle;
import org.springframework.context.ApplicationContext;

public class DiagnosticApplicationFactory implements IceApplicationProvider {
    private final IceApplicationProvider.AppType DiagnosticApplication =
            new IceApplicationProvider.AppType("System Explorer", "NODIAG", (URL)DiagnosticApplicationFactory.class.getResource("diag.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return DiagnosticApplication;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {
        
        final NumericFxList numericList = parentContext.getBean("numericList", NumericFxList.class);
        
        final SampleArrayFxList sampleArrayList = parentContext.getBean("sampleArrayList", SampleArrayFxList.class);
        
        final AlertFxList patientAlertList = parentContext.getBean("patientAlertList", AlertFxList.class);
        
        final AlertFxList technicalAlertList = parentContext.getBean("technicalAlertList", AlertFxList.class);
        
        final ValidationOracle validationOracle = parentContext.getBean("validationOracle", ValidationOracle.class);
        
        final Diagnostic diagnostic = new Diagnostic(patientAlertList, technicalAlertList, numericList, sampleArrayList, validationOracle);

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
            }

            @Override
            public void stop() {
            }

            @Override
            public void destroy() throws Exception {
                controller.stop();
            }
        };
    }

}
