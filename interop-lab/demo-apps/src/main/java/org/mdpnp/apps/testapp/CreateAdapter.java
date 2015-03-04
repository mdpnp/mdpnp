package org.mdpnp.apps.testapp;

import ice.ConnectionType;

import java.io.IOException;

import org.mdpnp.apps.testapp.Configuration.Application;
import org.mdpnp.apps.testapp.ConfigurationDialog.DeviceDriverProviderCell;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.serial.SerialProviderFactory;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class CreateAdapter {
    @FXML protected Button close, start;
    @FXML protected ComboBox<DeviceDriverProvider> deviceType;
    @FXML protected ComboBox<String> serialPorts;
    @FXML protected TextField address;
    
    boolean closePressed;
    
    Stage currentStage;
    
    public void setup() {
        deviceType.setButtonCell(new DeviceDriverProviderCell());
        deviceType.setCellFactory(new Callback<ListView<DeviceDriverProvider>, ListCell<DeviceDriverProvider>>() {

            @Override
            public ListCell<DeviceDriverProvider> call(ListView<DeviceDriverProvider> param) {
                return new DeviceDriverProviderCell();
            }

        });

        deviceType.setItems(FXCollections.observableArrayList(DeviceFactory.getAvailableDevices()));
        serialPorts.setItems(FXCollections.observableList(SerialProviderFactory.getDefaultProvider().getPortNames()));
        
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
                DeviceDriverProvider dt = (DeviceDriverProvider) deviceType.getSelectionModel().getSelectedItem();
                if (dt == null)
                    return;
                closePressed = false;
                currentStage.hide();
            }
        });        
    }
    
    public static Configuration showDialog(int domainId) throws IOException {
        FXMLLoader loader = new FXMLLoader(CreateAdapter.class.getResource("CreateAdapter.fxml"));
        Parent ui = loader.load();
        CreateAdapter d = loader.getController();
        d.currentStage = new Stage(StageStyle.UTILITY);
        d.setup();

        d.currentStage.initModality(Modality.APPLICATION_MODAL);
        d.currentStage.setTitle("Create a device adapter...");
        d.currentStage.setAlwaysOnTop(true);
        d.currentStage.setScene(new Scene(ui));
        d.currentStage.showAndWait();
        
        DeviceDriverProvider ddp = d.deviceType.getSelectionModel().getSelectedItem();
        String address;
        if(null == ddp) {
            return null;
        }
        if(ConnectionType.Serial.equals(ddp.getDeviceType().getConnectionType())) {
            address = d.serialPorts.getSelectionModel().getSelectedItem();
        } else {
            address = d.address.getText();
        }
        return d.closePressed ? null : new Configuration(false, Application.ICE_Device_Interface, 1, d.deviceType.getSelectionModel().getSelectedItem(), address);

    }
}


