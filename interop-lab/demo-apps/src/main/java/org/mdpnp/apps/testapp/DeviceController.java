package org.mdpnp.apps.testapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class DeviceController {
    @FXML public ImageView icon;
    @FXML public GridPane overlay, outofsync;
    @FXML public Label text;
    
    // TODO values like these should be externalized into some global
    // application settings
    private static final long MAX_CLOCK_DIFFERENCE = 2000L;
    
    public void bind(Device device) {
        icon.imageProperty().unbind();
        overlay.visibleProperty().unbind();
        text.textProperty().unbind();
        outofsync.visibleProperty().unbind();

        if(null != device) {
            icon.imageProperty().bind(device.imageProperty());
            overlay.visibleProperty().bind(device.connectedProperty().not());
            outofsync.visibleProperty().bind(device.clockDifferenceProperty().greaterThan(MAX_CLOCK_DIFFERENCE).or(device.clockDifferenceProperty().lessThan(-MAX_CLOCK_DIFFERENCE)));
            text.textProperty().bind(device.makeAndModelProperty());
        } else {
            icon.setImage(null);
            overlay.setVisible(false);
            outofsync.setVisible(false);
            text.setText("");
        }
    }

}
