package org.mdpnp.apps.testapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

import org.mdpnp.apps.testapp.Configuration.Application;
import org.mdpnp.devices.DeviceDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateAdapter {
    @FXML protected Button close, start;
    @FXML protected SettingsController settingsController;
    
    boolean closePressed;
    protected Stage currentStage;
    
    static Logger log = LoggerFactory.getLogger(CreateAdapter.class);
    
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
        if(d.closePressed) {
        	return null;
        }
        
        /*
         * Here we try and save the device definition to a file so that the user
         * can preserve this file later as a way to create a scenario.  We use a
         * try catch block for saving this as we don't want to prevent the device
         * from getting created if we fail to write the file.  The "device creation
         * log" is written to a file with todays day of the year, so we have a reasonable
         * way of keeping it consistent within a session, but separate from another
         * session.
         */
        int dayOfYear=Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        File f=new File(System.getProperty("user.home"),"device_creation_"+dayOfYear+".log");
        try(FileOutputStream fos=new FileOutputStream(f,true);PrintStream ps=new PrintStream(fos)) {
            String alias=ddp.getDeviceType().getAlias();
            ps.println(alias+"!"+address);
        } catch (IOException ioe) {
        	log.error("Failed to record device creation information for later", ioe);
        }

        Configuration c=new Configuration(false, Application.ICE_Device_Interface, domainId, ddp, address, fhirServerName, emrServerName);
        return c;

    }

}
