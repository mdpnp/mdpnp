package org.mdpnp.apps.testapp.device;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import org.mdpnp.guis.javafx.CompositeDevicePanel;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;

public class DeviceView {
    @FXML BorderPane main;
    @FXML CompositeDevicePanel cdp;
    
    public void set(DeviceDataMonitor dataMonitor) {
        cdp.setModel(dataMonitor);
    }
    public DeviceDataMonitor getModel() {
        return cdp.getModel();
    }
}
