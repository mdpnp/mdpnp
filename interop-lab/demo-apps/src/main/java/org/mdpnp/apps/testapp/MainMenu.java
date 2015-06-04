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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import org.controlsfx.control.GridView;
import org.mdpnp.apps.testapp.IceApplicationProvider.AppType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * @author Jeff Plourde
 *
 */
public class MainMenu {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(MainMenu.class);
    
    @FXML
    protected GridView<IceApplicationProvider.AppType> appList;
    
    @FXML
    protected GridView<Device> deviceList;

    @FXML Label devicesLabel;

    @FXML Label devicesEmptyText;

    public MainMenu setTypes(IceApplicationProvider.AppType[] appTypes) {
        appList.setItems(FXCollections.observableArrayList(appTypes));
        return this;
    }
    
    public MainMenu setDevices(ObservableList<Device> devices) {
        deviceList.setItems(devices);        
        return this;
    }
    
    public MainMenu() {

    }

    public GridView<AppType> getAppList() {
        return appList;
    }

    public GridView<Device> getDeviceList() {
        return deviceList;
    }
    
    public Label getDevicesLabel() {
        return devicesLabel;
    }
    
    public Label getDevicesEmptyText() {
        return devicesEmptyText;
    }
}
