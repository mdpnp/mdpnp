package org.mdpnp.apps.testapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ice.ConnectionType;
import javafx.beans.binding.Bindings;
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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
    Label applicationsLabel, domainIdLabel, deviceTypeLabel, serialPortsLabel, addressLabel, fhirServerLabel;
    @FXML
    GridPane gridPane;
    @FXML
    TextField fhirServer;

    @FXML
    VBox serialPortsContainer;

    @FXML
    TextField addressField;
    
    private Stage currentStage;
    
    private final BooleanProperty ready = new SimpleBooleanProperty(this, "ready", false);
    private final StringProperty start = new SimpleStringProperty(this, "start", "");
    private final ObjectProperty<Application> selectedApp = new SimpleObjectProperty<>(this, "selectedApp", null);
    private final ObjectProperty<DeviceDriverProvider> selectedDevice = new SimpleObjectProperty<>(this, "selectedDevice", null);
    private final StringProperty address = new SimpleStringProperty(this, "address", "");
    private final StringProperty domain = new SimpleStringProperty(this, "domain", "");
    private final StringProperty fhirServerName = new SimpleStringProperty(this, "fhirServerName", "");
    @FXML ComboBox<ice.ConnectionType> deviceCategory;
    
    
    
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
            gridPane.getChildren().remove(fhirServer);
            gridPane.getChildren().remove(fhirServerLabel);
            if(!gridPane.getChildren().contains(deviceType)) { gridPane.getChildren().add(deviceType); }
            if(!gridPane.getChildren().contains(deviceCategory)) { gridPane.getChildren().add(deviceCategory); }
            if(!gridPane.getChildren().contains(deviceTypeLabel)) { gridPane.getChildren().add(deviceTypeLabel); }
            if(!gridPane.getChildren().contains(deviceCategoryLabel)) { gridPane.getChildren().add(deviceCategoryLabel); }

            ice.ConnectionType selected = null;
            if (dt != null) {
            	//SK - For August 18 demo, drop the alias so button just says start
                start.set("Start");
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
                    gridPane.getChildren().remove(serialPortsContainer);
                } else {
                    gridPane.getChildren().remove(addressLabel);
                    gridPane.getChildren().remove(addressField);
                    if(!gridPane.getChildren().contains(serialPortsLabel)) { gridPane.getChildren().add(serialPortsLabel); }
                    if(!gridPane.getChildren().contains(serialPortsContainer)) { gridPane.getChildren().add(serialPortsContainer); }
                }

            } else if (ice.ConnectionType.Network.equals(selected)) {
                if(!gridPane.getChildren().contains(addressLabel)) { gridPane.getChildren().add(addressLabel); }
                if(!gridPane.getChildren().contains(addressField)) { gridPane.getChildren().add(addressField); }
                gridPane.getChildren().remove(serialPortsLabel);
                gridPane.getChildren().remove(serialPortsContainer);
            } else {
                gridPane.getChildren().remove(addressLabel);
                gridPane.getChildren().remove(addressField);
                gridPane.getChildren().remove(serialPortsLabel);
                gridPane.getChildren().remove(serialPortsContainer);
            }
            break;
        case ICE_Supervisor:
            gridPane.getChildren().remove(deviceCategory);
            gridPane.getChildren().remove(deviceCategoryLabel);
            gridPane.getChildren().remove(deviceType);
            gridPane.getChildren().remove(deviceTypeLabel);
            gridPane.getChildren().remove(addressLabel);
            gridPane.getChildren().remove(addressField);
            gridPane.getChildren().remove(serialPortsLabel);
            gridPane.getChildren().remove(serialPortsContainer);
            if(!gridPane.getChildren().contains(fhirServerLabel)) { gridPane.getChildren().add(fhirServerLabel); }
            if(!gridPane.getChildren().contains(fhirServer)) { gridPane.getChildren().add(fhirServer); }
            ready.set(true);
            start.set("Start " + app);
            break;
        }
        currentStage.sizeToScene();
    }
    
    private Map<ice.ConnectionType, ObservableList<DeviceDriverProvider>> deviceTypesByCategory = new HashMap<>();
    @FXML Label deviceCategoryLabel;
    
    @SuppressWarnings("unchecked")
    public void set(Configuration conf, boolean showAppsOption, Stage currentStage) {
        
        this.currentStage = currentStage;
        deviceType.setButtonCell(new DeviceDriverProviderCell());
        
        deviceType.setCellFactory(new Callback<ListView<DeviceDriverProvider>, ListCell<DeviceDriverProvider>>() {

            @Override
            public ListCell<DeviceDriverProvider> call(ListView<DeviceDriverProvider> param) {
                return new DeviceDriverProviderCell();
            }

        });
        
        ObservableList<ice.ConnectionType> categories = FXCollections.observableArrayList();
        for(int ordinal : ice.ConnectionType.getOrdinals()) {
            categories.add(ice.ConnectionType.from_int(ordinal));
        }

        deviceCategory.setItems(categories);
       
        
        for(DeviceDriverProvider ddp : DeviceFactory.getAvailableDevices()) {
            ObservableList<DeviceDriverProvider> list = deviceTypesByCategory.get(ddp.getDeviceType().getConnectionType());
            if(null==list) {
                list = FXCollections.observableArrayList();
                deviceTypesByCategory.put(ddp.getDeviceType().getConnectionType(), list);
            }
            list.add(ddp);
        }
        
        deviceCategory.valueProperty().addListener(new ChangeListener<ConnectionType>() {

            @Override
            public void changed(ObservableValue<? extends ConnectionType> observable, ConnectionType oldValue, ConnectionType newValue) {
                if(null != newValue) {
                    deviceType.setItems(deviceTypesByCategory.get(newValue));
                }
            }
        });
        
        deviceCategory.setValue(ice.ConnectionType.Simulated);
        applications.setItems(FXCollections.observableArrayList(Application.values()));

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
                            ObservableList<Node> children = serialPortsContainer.getChildren();
                            String[] addressElements = conf.getAddress().split(",");
                            for(int i = 0; i < addressElements.length; i++) {
                                if(i<children.size() && children.get(i) instanceof ComboBox) {
                                    ((ComboBox<String>)children.get(i)).getSelectionModel().select(addressElements[i]);
                                }
                            }
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
        fhirServerName.bind(fhirServer.textProperty());

        selectedDevice.addListener(new ChangeListener<DeviceDriverProvider>() {

            @Override
            public void changed(ObservableValue<? extends DeviceDriverProvider> observable, DeviceDriverProvider oldValue,
                    DeviceDriverProvider newValue) {
                if(null != newValue && ConnectionType.Serial.equals(newValue.getDeviceType().getConnectionType())) {
                    address.unbind();
                    serialPortsContainer.getChildren().clear();
                    ComboBox<?> serialPorts[] = new ComboBox[newValue.getDeviceType().getConnectionCount()];
                    List<Object> elements = new ArrayList<Object>(newValue.getDeviceType().getConnectionCount());
                    
                    ObservableList<String> portNames = FXCollections.observableList(SerialProviderFactory.getDefaultProvider().getPortNames());
                    for(int i = 0; i < serialPorts.length; i++) {
                        serialPorts[i] = new ComboBox<String>(portNames);
                        serialPortsContainer.getChildren().add(serialPorts[i]);
                        if(i > 0) {
                            elements.add(",");
                        }
                        elements.add(serialPorts[i].valueProperty());
                    }
                    address.bind(Bindings.concat(elements.toArray(new Object[0])));
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

    public StringProperty fhirServerNameProperty() {
        return this.fhirServerName;
    }

    public String getFhirServerName() {
        return this.fhirServerNameProperty().get();
    }

    public void setFhirServerName(final String fhirServerName) {
        this.fhirServerNameProperty().set(fhirServerName);
    }

    
}
