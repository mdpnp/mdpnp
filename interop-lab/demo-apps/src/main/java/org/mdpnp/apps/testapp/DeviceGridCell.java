package org.mdpnp.apps.testapp;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import org.controlsfx.control.GridCell;

public class DeviceGridCell extends GridCell<Device> {
    @FXML public ImageView icon, overlay;
    @FXML public Label text;
    
    public DeviceGridCell() throws IOException {
        FXMLLoader loader = new FXMLLoader(AppTypeGridCell.class.getResource("DeviceListCell.fxml"));
        loader.setController(this);
        setGraphic(loader.load());
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }
    
    
    
    @Override
    protected void updateItem(Device item, boolean empty) {
        super.updateItem(item, empty);

        icon.imageProperty().unbind();
        overlay.visibleProperty().unbind();
        text.textProperty().unbind();
        
        if(null == item) {
            icon.setImage(null);
            overlay.setVisible(true);
            text.setText("");
        } else {
            icon.imageProperty().bind(item.imageProperty());
            overlay.visibleProperty().bind(item.connectedProperty().not());
            text.textProperty().bind(item.makeAndModelProperty());
        }
        
    }
}