package org.mdpnp.apps.testapp.validate;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.springframework.context.ApplicationContext;

public class ValidateApplicationFactory implements IceApplicationProvider {
    private final IceApplicationProvider.AppType ValidateApplication = new IceApplicationProvider.AppType("Auto Validate", "NOCHART",
            ValidateApplicationFactory.class.getResource("checkbox.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return ValidateApplication;

    }


    
    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final VitalModel model = (VitalModel) parentContext.getBean("vitalModel");

        FXMLLoader loader = new FXMLLoader(ValidateApplication.class.getResource("ValidateApplication.fxml"));

        final Parent ui = loader.load();

        final ValidateApplication controller = ((ValidateApplication) loader.getController());

        controller.setModel(model);
        
        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return ValidateApplication;
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
            }
        };
    }

}
