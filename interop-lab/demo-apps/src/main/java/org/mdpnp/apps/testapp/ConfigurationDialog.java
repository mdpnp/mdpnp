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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.mdpnp.apps.testapp.Configuration.Application;
import org.mdpnp.devices.DeviceDriverProvider;
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
    SettingsController settingsController;

    public boolean quitPressed;



    public Button getQuit() {
        return quit;
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
        settingsController.set(conf, true, currentStage);
        
        start.visibleProperty().bind(settingsController.readyProperty());
        start.textProperty().bind(settingsController.startProperty());
        
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
                Application app = settingsController.selectedAppProperty().get();
                if (Application.ICE_Device_Interface.equals(app)) {
                    DeviceDriverProvider dt = settingsController.selectedDeviceProperty().get();
                    if (dt == null)
                        return;
                }
                quitPressed = false;
                currentStage.hide();
            }
        });

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
    private Stage currentStage;

    public static ConfigurationDialog showDialog(Configuration configuration) throws IOException {
        FXMLLoader loader = new FXMLLoader(ConfigurationDialog.class.getResource("ConfigurationDialog.fxml"));
        Parent ui = loader.load();
        ConfigurationDialog d = loader.getController();
        d.currentStage = new Stage(StageStyle.UNDECORATED);
        d.set(configuration, d.currentStage);

        

        d.currentStage.initModality(Modality.APPLICATION_MODAL);
        d.currentStage.setTitle("MD PnP Demo Apps");
        d.currentStage.setAlwaysOnTop(true);
        d.currentStage.setScene(new Scene(ui));
        d.currentStage.sizeToScene();
        d.currentStage.centerOnScreen();
        d.currentStage.showAndWait();

        String s = System.getProperty("mdpnp.ui");
        boolean headless = s != null && Boolean.parseBoolean(s);

        d.lastConf = new Configuration(headless, 
                d.settingsController.selectedAppProperty().get(), 
                Integer.parseInt(d.settingsController.domainProperty().get()), 
                d.settingsController.selectedDeviceProperty().get(),
                d.settingsController.addressProperty().get());

        return d;
    }

}
