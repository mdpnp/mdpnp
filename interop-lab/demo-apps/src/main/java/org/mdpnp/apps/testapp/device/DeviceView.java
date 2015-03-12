package org.mdpnp.apps.testapp.device;

import org.mdpnp.apps.testapp.Device;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DeviceView {

    @FXML Label udi;
    
    private Device device;
    private DeviceDataMonitor dataMonitor;

    public void set(final String udi) {
        Platform.runLater( () -> {
            this.udi.setText(udi);
        });
    }
    
    public void set(Device device) {
        this.device = device;
        set(device.getUDI());
    }
    
    public void set(DeviceDataMonitor dataMonitor) {
        this.dataMonitor = dataMonitor;
    }
    public DeviceDataMonitor getModel() {
        return dataMonitor;
    }
}
