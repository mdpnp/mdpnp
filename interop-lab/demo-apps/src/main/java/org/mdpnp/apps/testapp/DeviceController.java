package org.mdpnp.apps.testapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.beans.binding.Bindings;

import org.mdpnp.apps.testapp.DeviceAdapterCommand.HeadlessAdapter;

public class DeviceController {
    @FXML public ImageView icon;
    @FXML public Button closeSimulator;
    @FXML public Label text, outofsync, overlay;
    private Device device;
    
    // TODO values like these should be externalized into some global
    // application settings
    private static final long MAX_CLOCK_DIFFERENCE = 2000L;
    
    public void bind(Device device) {
        this.device = device;
        icon.imageProperty().unbind();
        overlay.visibleProperty().unbind();
        text.textProperty().unbind();
        outofsync.visibleProperty().unbind();
        closeSimulator.visibleProperty().unbind();

        if(null != device) {
            icon.imageProperty().bind(device.imageProperty());
            overlay.visibleProperty().bind(device.connectedProperty().not());
            outofsync.visibleProperty().bind(device.clockDifferenceProperty().greaterThan(MAX_CLOCK_DIFFERENCE).or(device.clockDifferenceProperty().lessThan(-MAX_CLOCK_DIFFERENCE)));
            text.textProperty().bind(Bindings.format("%s %s",device.makeAndModelProperty(),device.comPortProperty()));
            closeSimulator.visibleProperty().bind(device.headlessAdapterProperty().isNotNull());
        } else {
            icon.setImage(null);
            overlay.setVisible(false);
            outofsync.setVisible(false);
            text.setText("");
            closeSimulator.setVisible(false);
        }
    }

    @FXML public void clickCloseSimulator(ActionEvent event) {
        HeadlessAdapter da = device.getHeadlessAdapter();
        if(null != da) {
            new Thread(()->da.stop()).start();
            event.consume();
        }
    }

}
