package org.mdpnp.apps.testapp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import software.amazon.awssdk.core.internal.sync.FileContentStreamProvider;

import org.mdpnp.apps.testapp.Configuration.Application;
import org.mdpnp.devices.DeviceDriverProvider;

public class CreateMultiAdapters {
    @FXML protected Button close/*, start*/;
    //@FXML protected SettingsController settingsController;
    @FXML TextArea fileContent;
    
    boolean closePressed;
    protected Stage currentStage;
    
    private static List<String> configFileLines;
    
    public void set() {
        //settingsController.set(null, false, currentStage);
        
        //start.textProperty().bind(settingsController.startProperty());
        //start.visibleProperty().bind(settingsController.readyProperty());
        
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closePressed = true;
                currentStage.hide();
            }
        });
        /*
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
        */        
    }
    
    /*
    public static Configuration showDialog(int domainId) throws IOException {
        FXMLLoader loader = new FXMLLoader(CreateMultiAdapters.class.getResource("CreateAdapter.fxml"));
        Parent ui = loader.load();
        CreateMultiAdapters d = loader.getController();
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
    */
    
    public ArrayList<Configuration> showMultiDialog(int domainId) throws IOException {
        FXMLLoader loader = new FXMLLoader(CreateMultiAdapters.class.getResource("CreateMultiAdapters.fxml"));
        Parent ui = loader.load();
        CreateMultiAdapters d = loader.getController();
        d.currentStage = new Stage();
        d.set();

        d.currentStage.initModality(Modality.APPLICATION_MODAL);
        d.currentStage.setTitle("Create a device adapter...");
        d.currentStage.setAlwaysOnTop(true);
        d.currentStage.setScene(new Scene(ui));
        d.currentStage.sizeToScene();
        d.currentStage.showAndWait();
        
//        DeviceDriverProvider ddp = d.settingsController.selectedDeviceProperty().get();
//        String address;
//        if(null == ddp) {
//            return null;
//        }
//        address = d.settingsController.addressProperty().get();
//        String fhirServerName = d.settingsController.getFhirServerName();
//        String emrServerName = d.settingsController.getEMRServerName();
        ArrayList<Configuration> allConfigs=new ArrayList<>();
        
        if(configFileLines!=null) {
        	for(String oneLine : configFileLines) {
        		System.err.println("configFileLine is "+oneLine);
        		String[] fields=oneLine.split("!");
        		DeviceDriverProvider ddp=DeviceFactory.getDeviceDriverProvider(fields[0]);
        		if(ddp==null) {
        			System.err.println("No device found for alias "+fields[0]);
        			continue;
        		}
	        	Configuration c=new Configuration(false, Application.ICE_Device_Interface, domainId, ddp, fields.length==2 && fields[1].length()>0 ? fields[1] : null, "", "");
	        	allConfigs.add(c);
        	}
        }
        System.err.println("showMultiDialog allConfigs has size "+allConfigs.size());
        return allConfigs;
        //return d.closePressed ? null : new Configuration(false, Application.ICE_Device_Interface, domainId, ddp, address, fhirServerName, emrServerName);

    }
    
    public void chooseFile() throws IOException {
    	javafx.stage.FileChooser chooser=new javafx.stage.FileChooser();
    	chooser.setTitle("Select device definition file");
    	File selectedFile=chooser.showOpenDialog(currentStage);
    	configFileLines=Files.readAllLines(selectedFile.toPath());
    	for(String oneLine : configFileLines) {
    		fileContent.appendText(oneLine+"\n");
    	}
    }
}
