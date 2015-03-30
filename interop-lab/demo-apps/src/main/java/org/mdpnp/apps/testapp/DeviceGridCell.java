package org.mdpnp.apps.testapp;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import org.controlsfx.control.GridCell;

public class DeviceGridCell extends GridCell<Device> {
    private Parent root;
    @FXML public ImageView icon;
    @FXML public GridPane overlay, outofsync;
    @FXML public Label text;
    
    // TODO values like these should be externalized into some global
    // application settings
    private static final long MAX_CLOCK_DIFFERENCE = 2000L;
    
    public DeviceGridCell() throws IOException {
    }
    
    
    
    @Override
    protected void updateItem(Device item, boolean empty) {
        super.updateItem(item, empty);

        if(null == root) {
            FXMLLoader loader = new FXMLLoader(DeviceGridCell.class.getResource("DeviceGridCell.fxml"));
            loader.setController(this);
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        if(null == getTooltip()) {
            setTooltip(new Tooltip(""));
        }
        
        tooltipProperty().get().textProperty().unbind();
        icon.imageProperty().unbind();
        overlay.visibleProperty().unbind();
        text.textProperty().unbind();
        outofsync.visibleProperty().unbind();
        textProperty().unbind();
        
        setUserData(item);
        
        if(null == item) {
            icon.setImage(null);
            overlay.setVisible(false);
            outofsync.setVisible(false);
            text.setText("");
            tooltipProperty().get().textProperty().set("");
            setText(null);
            setGraphic(null);
        } else {
            icon.imageProperty().bind(item.imageProperty());
            overlay.visibleProperty().bind(item.connectedProperty().not());
            outofsync.visibleProperty().bind(item.clockDifferenceProperty().greaterThan(MAX_CLOCK_DIFFERENCE).or(item.clockDifferenceProperty().lessThan(-MAX_CLOCK_DIFFERENCE)));
            text.textProperty().bind(item.makeAndModelProperty());
            tooltipProperty().get().textProperty().bind(item.hostnameProperty());
            textProperty().bind(item.makeAndModelProperty());
            setGraphic(root);
        }
        
    }
}