package org.mdpnp.apps.testapp;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import org.controlsfx.control.GridCell;

public class DeviceGridCell extends GridCell<Device> {
    private Parent root;
    
    public Label text;

    public ImageView icon, overlay;
    
    public DeviceGridCell() {

    }
    
    @Override
    protected void updateItem(Device item, boolean empty) {
        super.updateItem(item, empty);

        if(null == root) {
            FXMLLoader loader = new FXMLLoader(AppTypeGridCell.class.getResource("DeviceListCell.fxml"));
            try {
                root = loader.load();
                text = (Label) root.lookup("#text");
                icon = (ImageView) root.lookup("#icon");
                overlay = (ImageView) root.lookup("#overlay");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(null == item) {
            setText(null);
            overlay.setVisible(false);
            setGraphic(null);
            return;
        }
        text.setText(item.getMakeAndModel());
        icon.setImage(item.getIcon());
        overlay.setVisible(!item.isConnected());
        root.setUserData(item);
        setText(item.getMakeAndModel());
        setGraphic(root);
    }
}