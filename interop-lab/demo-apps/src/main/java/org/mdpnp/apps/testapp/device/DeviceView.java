package org.mdpnp.apps.testapp.device;

import org.mdpnp.apps.device.CompositeDevicePanel;
import org.mdpnp.apps.device.DeviceDataMonitor;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

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
