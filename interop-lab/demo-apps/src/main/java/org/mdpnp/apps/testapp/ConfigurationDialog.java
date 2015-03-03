/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import org.mdpnp.apps.testapp.Configuration.Application;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.TCPSerialProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class ConfigurationDialog {
    @FXML
    BorderPane main;
    @FXML
    Button start, quit;
    @FXML
    TextField domainId;
    @FXML
    ComboBox<Configuration.Application> applications;
    @FXML
    ComboBox<DeviceDriverProvider> deviceType;
    @FXML
    Label deviceTypeLabel, serialPortsLabel, addressLabel;

    @FXML
    ComboBox<String> serialPorts;

    @FXML
    TextField address;

    public boolean quitPressed;

    public ComboBox<Configuration.Application> getApplications() {
        return applications;
    }

    public TextField getDomainId() {
        return domainId;
    }

    public Button getQuit() {
        return quit;
    }

    // private static ComboBox<DeviceDriverProvider> makeDeviceTypesUIModel()
    // {
    // DeviceDriverProvider[] arr = DeviceFactory.getAvailableDevices();
    // DeviceDriverProvider[] l = new DeviceDriverProvider[arr.length+1];
    // System.arraycopy(arr, 0, l, 1, arr.length);
    // l[0] = null;
    //
    // JComboBox jcb = new JComboBox(l);
    //
    // jcb.setRenderer(new DefaultListCellRenderer() {
    // @Override
    // public Component getListCellRendererComponent(JList list,
    // Object value,
    // int index,
    // boolean isSelected,
    // boolean cellHasFocus) {
    // if(value instanceof DeviceDriverProvider) {
    // DeviceDriverProvider ddp = (DeviceDriverProvider) value;
    // DeviceDriverProvider.DeviceType dt = ddp.getDeviceType();
    // return super.getListCellRendererComponent(list, dt, index, isSelected,
    // cellHasFocus);
    // }
    // else {
    // return super.getListCellRendererComponent(list, "Select One", index,
    // isSelected, cellHasFocus);
    // }
    // }
    // });
    //
    // return jcb;
    // }

    protected void set(Application app, DeviceDriverProvider dt) {
        switch (app) {
        case ICE_Device_Interface:
            deviceType.setVisible(true);
            deviceTypeLabel.setVisible(true);

            ice.ConnectionType selected = null;
            if (dt != null) {
                start.setText("Start " + dt.getDeviceType().getAlias());
                start.setVisible(true);
                selected = dt.getDeviceType().getConnectionType();
            } else {
                start.setVisible(false);
            }
            if (ice.ConnectionType.Serial.equals(selected)) {
                if (SerialProviderFactory.getDefaultProvider() instanceof TCPSerialProvider) {
                    addressLabel.setVisible(true);
                    address.setVisible(true);
                    serialPortsLabel.setVisible(false);
                    serialPorts.setVisible(false);
                } else {
                    addressLabel.setVisible(false);
                    address.setVisible(false);
                    serialPortsLabel.setVisible(true);
                    serialPorts.setVisible(true);
                }

            } else if (ice.ConnectionType.Network.equals(selected)) {
                addressLabel.setVisible(true);
                address.setVisible(true);
                serialPortsLabel.setVisible(false);
                serialPorts.setVisible(false);
            } else {
                addressLabel.setVisible(false);
                address.setVisible(false);
                serialPortsLabel.setVisible(false);
                serialPorts.setVisible(false);
            }
            break;
        case ICE_Supervisor:
        case ICE_ParticipantOnly:
            deviceType.setVisible(false);
            deviceTypeLabel.setVisible(false);
            addressLabel.setVisible(false);
            address.setVisible(false);
            serialPortsLabel.setVisible(false);
            serialPorts.setVisible(false);
            start.setVisible(true);
            start.setText("Start " + app);
            break;
        }
    }

    @SuppressWarnings("unused")
    private final static Logger log = LoggerFactory.getLogger(ConfigurationDialog.class);

    public static class DeviceDriverProviderCell extends ListCell<DeviceDriverProvider> {
        @Override
        protected void updateItem(DeviceDriverProvider item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(null);
            if (item != null) {
                setText(item.getDeviceType().toString());
            } else {
                setText("");
            }
        }
    }

    public ConfigurationDialog set(Configuration conf, final Stage currentStage) {
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
//        main.layout();

        if (null != conf) {
            if (null != conf.getApplication()) {
                applications.getSelectionModel().select(conf.getApplication());
            }
            if (null != conf.getDeviceFactory()) {
                deviceType.getSelectionModel().select(conf.getDeviceFactory());
            }
            domainId.setText(Integer.toString(conf.getDomainId()));

            if (null != conf.getApplication() && null != conf.getAddress()) {
                switch (conf.getApplication()) {
                case ICE_Device_Interface:
                    if (null != conf.getDeviceFactory()) {
                        ice.ConnectionType connType = conf.getDeviceFactory().getDeviceType().getConnectionType();
                        if (ice.ConnectionType.Network.equals(connType)) {
                            this.address.setText(conf.getAddress());
                        } else if (ice.ConnectionType.Serial.equals(connType)) {
                            this.serialPorts.getSelectionModel().select(conf.getAddress());
                            this.address.setText(conf.getAddress());
                        }
                    }
                case ICE_Supervisor:
                    break;
                default:
                    break;
                }
            }
        }
        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                quitPressed = true;
                currentStage.hide();
            }

        });
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // // basic validation of parameters.
                Application app = (Application) applications.getSelectionModel().getSelectedItem();
                if (Application.ICE_Device_Interface.equals(app)) {
                    DeviceDriverProvider dt = (DeviceDriverProvider) deviceType.getSelectionModel().getSelectedItem();
                    if (dt == null)
                        return;
                }
                quitPressed = false;
                currentStage.hide();
            }
        });

        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                set(applications.getSelectionModel().getSelectedItem(), deviceType.getSelectionModel().getSelectedItem());
            }

        };

        applications.setOnAction(handler);
        deviceType.setOnAction(handler);

        set((Application) applications.getSelectionModel().getSelectedItem(), deviceType.getSelectionModel().getSelectedItem());

        return this;
    }

    public ConfigurationDialog() {

    }

    private Configuration lastConf;

    public Configuration getLastConfiguration() {
        return lastConf;
    }
    
    public boolean getQuitPressed() {
        return quitPressed;
    }

    public static ConfigurationDialog showDialog(Configuration configuration) throws IOException {
        FXMLLoader loader = new FXMLLoader(ConfigurationDialog.class.getResource("ConfigurationDialog.fxml"));
        Parent ui = loader.load();
        ConfigurationDialog d = loader.getController();
        Stage currentStage = new Stage(StageStyle.UTILITY);
        d.set(configuration, currentStage);

        

        currentStage.initModality(Modality.APPLICATION_MODAL);
        currentStage.setTitle("MD PnP Demo Apps");
        currentStage.setAlwaysOnTop(true);
        currentStage.setScene(new Scene(ui));
        currentStage.showAndWait();

        String s = System.getProperty("mdpnp.ui");
        boolean headless = s != null && Boolean.parseBoolean(s);

        d.lastConf = new Configuration(headless, d.applications.getSelectionModel().getSelectedItem(), Integer.parseInt(d.domainId.getText()), d.deviceType
                .getSelectionModel().getSelectedItem(), d.address.getText());

        return d;
    }

    // public Configuration showDialog() {
    // pack();
    // setLocationRelativeTo(null);
    // setVisible(true);
    //
    // String address = null;
    // Application app = (Application) applications.getSelectedItem();
    // DeviceDriverProvider ddp =
    // (DeviceDriverProvider)deviceType.getSelectedItem();
    //
    // switch (app) {
    // case ICE_Device_Interface:
    // if(ddp != null) {
    // ice.ConnectionType selected = ddp.getDeviceType().getConnectionType();
    // if (ice.ConnectionType.Network.equals(selected)) {
    // address = this.address.getText();
    // } else if (ice.ConnectionType.Serial.equals(selected)) {
    // if (SerialProviderFactory.getDefaultProvider() instanceof
    // TCPSerialProvider) {
    // address = this.address.getText();
    // } else {
    // address = this.serialPorts.getSelectedItem().toString();
    // }
    // }
    // }
    // case ICE_Supervisor:
    // break;
    // default:
    // break;
    //
    // }
    //
    // // if mdpnp.ui is set to false, force the system to come up in the
    // headless mode.
    // // If not set, default to UI mode i.e headless==false.
    // //
    // String s = System.getProperty("mdpnp.ui");
    // boolean headless = s!=null && Boolean.parseBoolean(s);
    // lastConf = new Configuration(headless,
    // app,Integer.parseInt(domainId.getText()), ddp, address);
    //
    // // dispose();
    // return quitPressed ? null : lastConf;
    // }
}
