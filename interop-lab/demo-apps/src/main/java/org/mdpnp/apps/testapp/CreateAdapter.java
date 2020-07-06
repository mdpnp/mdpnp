package org.mdpnp.apps.testapp;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.mdpnp.apps.testapp.Configuration.Application;
import org.mdpnp.devices.DeviceDriverProvider;

public class CreateAdapter {
    @FXML protected Button close, start;
    @FXML protected SettingsController settingsController;
    
    boolean closePressed;
    protected Stage currentStage;
    
    public void set() {
        settingsController.set(null, false, currentStage);
        
        start.textProperty().bind(settingsController.startProperty());
        start.visibleProperty().bind(settingsController.readyProperty());
        
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closePressed = true;
                currentStage.hide();
            }
        });
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // // basic validation of parameters.
                if(null == settingsController.selectedDeviceProperty().get()) {
                    return;
                }
                closePressed = false;
                currentStage.hide();
            }
        });        
    }
    
    public static Configuration showDialog(int domainId) throws IOException {
        FXMLLoader loader = new FXMLLoader(CreateAdapter.class.getResource("CreateAdapter.fxml"));
        Parent ui = loader.load();
        CreateAdapter d = loader.getController();
        d.currentStage = new Stage();
        d.set();

        d.currentStage.initModality(Modality.APPLICATION_MODAL);
        d.currentStage.setTitle("Create a device adapter...");
        d.currentStage.setAlwaysOnTop(true);
        d.currentStage.setScene(new Scene(ui));
        d.currentStage.sizeToScene();
        d.currentStage.showAndWait();
        
        DeviceDriverProvider ddp = d.settingsController.selectedDeviceProperty().get();
        String address;
        if(null == ddp) {
            return null;
        }
        address = d.settingsController.addressProperty().get();
        String fhirServerName = d.settingsController.getFhirServerName();
        String emrServerName = d.settingsController.getEMRServerName();
        
        return d.closePressed ? null : new Configuration(false, Application.ICE_Device_Interface, domainId, ddp, address, fhirServerName, emrServerName);

    }
}
