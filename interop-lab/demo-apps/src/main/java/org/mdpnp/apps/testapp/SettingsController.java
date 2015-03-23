package org.mdpnp.apps.testapp;

import ice.ConnectionType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.mdpnp.apps.testapp.Configuration.Application;
import org.mdpnp.apps.testapp.ConfigurationDialog.DeviceDriverProviderCell;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.TCPSerialProvider;

public class SettingsController {
    @FXML
    TextField domainId;
    @FXML
    ComboBox<Configuration.Application> applications;
    @FXML
    ComboBox<DeviceDriverProvider> deviceType;
    @FXML
    Label applicationsLabel, domainIdLabel, deviceTypeLabel, serialPortsLabel, addressLabel;
    @FXML
    GridPane gridPane;

    @FXML
    ComboBox<String> serialPorts;

    @FXML
    TextField addressField;
    
    private Stage currentStage;
    
    private final BooleanProperty ready = new SimpleBooleanProperty(this, "ready", false);
    private final StringProperty start = new SimpleStringProperty(this, "start", "");
    private final ObjectProperty<Application> selectedApp = new SimpleObjectProperty<>(this, "selectedApp", null);
    private final ObjectProperty<DeviceDriverProvider> selectedDevice = new SimpleObjectProperty<>(this, "selectedDevice", null);
    private final StringProperty address = new SimpleStringProperty(this, "address", "");
    private final StringProperty domain = new SimpleStringProperty(this, "domain", "");
    
    public boolean isReady() {
        return ready.get();
    }
    
    public ReadOnlyBooleanProperty readyProperty() {
        return ready;
    }
    
    public String getStart() {
        return start.get();
    }
    
    public ReadOnlyStringProperty startProperty() {
        return start;
    }
    
    public ReadOnlyObjectProperty<Application> selectedAppProperty() {
        return selectedApp;
    }
    
    public ReadOnlyObjectProperty<DeviceDriverProvider> selectedDeviceProperty() {
        return selectedDevice;
    }

    public ReadOnlyStringProperty addressProperty() {
        return address;
    }
    
    public ReadOnlyStringProperty domainProperty() {
        return domain;
    }
    
    protected void set(Application app, DeviceDriverProvider dt) {
        switch (app) {
        case ICE_Device_Interface:
            if(!gridPane.getChildren().contains(deviceType)) { gridPane.getChildren().add(deviceType); }
            if(!gridPane.getChildren().contains(deviceTypeLabel)) { gridPane.getChildren().add(deviceTypeLabel); }

            ice.ConnectionType selected = null;
            if (dt != null) {
                start.set("Start " + dt.getDeviceType().getAlias());
                ready.set(true);
                selected = dt.getDeviceType().getConnectionType();
            } else {
                ready.set(false);
            }
            if (ice.ConnectionType.Serial.equals(selected)) {
                if (SerialProviderFactory.getDefaultProvider() instanceof TCPSerialProvider) {
                    if(!gridPane.getChildren().contains(addressLabel)) { gridPane.getChildren().add(addressLabel); }
                    if(!gridPane.getChildren().contains(addressField)) { gridPane.getChildren().add(addressField); }
                    gridPane.getChildren().remove(serialPortsLabel);
                    gridPane.getChildren().remove(serialPorts);
                } else {
                    gridPane.getChildren().remove(addressLabel);
                    gridPane.getChildren().remove(addressField);
                    if(!gridPane.getChildren().contains(serialPortsLabel)) { gridPane.getChildren().add(serialPortsLabel); }
                    if(!gridPane.getChildren().contains(serialPorts)) { gridPane.getChildren().add(serialPorts); }
                }

            } else if (ice.ConnectionType.Network.equals(selected)) {
                if(!gridPane.getChildren().contains(addressLabel)) { gridPane.getChildren().add(addressLabel); }
                if(!gridPane.getChildren().contains(addressField)) { gridPane.getChildren().add(addressField); }
                gridPane.getChildren().remove(serialPortsLabel);
                gridPane.getChildren().remove(serialPorts);
            } else {
                gridPane.getChildren().remove(addressLabel);
                gridPane.getChildren().remove(addressField);
                gridPane.getChildren().remove(serialPortsLabel);
                gridPane.getChildren().remove(serialPorts);
            }
            break;
        case ICE_Supervisor:
        case ICE_ParticipantOnly:
            gridPane.getChildren().remove(deviceType);
            gridPane.getChildren().remove(deviceTypeLabel);
            gridPane.getChildren().remove(addressLabel);
            gridPane.getChildren().remove(addressField);
            gridPane.getChildren().remove(serialPortsLabel);
            gridPane.getChildren().remove(serialPorts);
            ready.set(true);
            start.set("Start " + app);
            break;
        }
        currentStage.sizeToScene();
    }
    
    public void set(Configuration conf, boolean showAppsOption, Stage currentStage) {
        
        this.currentStage = currentStage;
        deviceType.setButtonCell(new DeviceDriverProviderCell());
        
        deviceType.setCellFactory(new Callback<ListView<DeviceDriverProvider>, ListCell<DeviceDriverProvider>>() {

            @Override
            public ListCell<DeviceDriverProvider> call(ListView<DeviceDriverProvider> param) {
                return new DeviceDriverProviderCell();
            }

        });

        deviceType.setItems(FXCollections.observableArrayList(DeviceFactory.getAvailableDevices()));
        applications.setItems(FXCollections.observableArrayList(Application.values()));
        serialPorts.setItems(FXCollections.observableList(SerialProviderFactory.getDefaultProvider().getPortNames()));

        if (null != conf) {
            if (null != conf.getApplication()) {
                applications.getSelectionModel().select(conf.getApplication());
            }
            if (null != conf.getDeviceFactory()) {
                for(DeviceDriverProvider ddp : deviceType.getItems()) {
                    if(conf.getDeviceFactory().equals(ddp)) {
                        deviceType.getSelectionModel().select(ddp);
                        break;
                    }
                }
            }
            domainId.setText(Integer.toString(conf.getDomainId()));

            if (null != conf.getApplication() && null != conf.getAddress()) {
                switch (conf.getApplication()) {
                case ICE_Device_Interface:
                    if (null != conf.getDeviceFactory()) {
                        ice.ConnectionType connType = conf.getDeviceFactory().getDeviceType().getConnectionType();
                        if (ice.ConnectionType.Network.equals(connType)) {
                            this.addressField.setText(conf.getAddress());
                        } else if (ice.ConnectionType.Serial.equals(connType)) {
                            this.serialPorts.getSelectionModel().select(conf.getAddress());
                            this.addressField.setText(conf.getAddress());
                        }
                    }
                case ICE_Supervisor:
                    break;
                default:
                    break;
                }
            }
        }

        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                set(applications.getSelectionModel().getSelectedItem(), deviceType.getSelectionModel().getSelectedItem());
            }

        };
        
        applications.setOnAction(handler);
        deviceType.setOnAction(handler);
        
        selectedApp.bind(applications.getSelectionModel().selectedItemProperty());
        selectedDevice.bind(deviceType.getSelectionModel().selectedItemProperty());
        domain.bind(domainId.textProperty());
        address.bind(addressField.textProperty());

        selectedDevice.addListener(new ChangeListener<DeviceDriverProvider>() {

            @Override
            public void changed(ObservableValue<? extends DeviceDriverProvider> observable, DeviceDriverProvider oldValue,
                    DeviceDriverProvider newValue) {
                if(null != newValue && ConnectionType.Serial.equals(newValue.getDeviceType().getConnectionType())) {
                    address.unbind();
                    address.bind(serialPorts.getSelectionModel().selectedItemProperty());
                } else {
                    address.unbind();
                    address.bind(addressField.textProperty());
                }
            }
            
        });

        if(!showAppsOption) {
            applications.getSelectionModel().select(Application.ICE_Device_Interface);
            gridPane.getChildren().remove(applicationsLabel);
            gridPane.getChildren().remove(applications);
            gridPane.getChildren().remove(domainIdLabel);
            gridPane.getChildren().remove(domainId);
        } else {
            applications.getSelectionModel().select(Application.ICE_Supervisor);
        }
        
        set((Application) applications.getSelectionModel().getSelectedItem(), deviceType.getSelectionModel().getSelectedItem());
        
    }

    
}
