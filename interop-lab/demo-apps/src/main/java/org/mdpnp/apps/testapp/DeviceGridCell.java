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
                setGraphic(root);
                text = (Label) root.lookup("#text");
                icon = (ImageView) root.lookup("#icon");
                overlay = (ImageView) root.lookup("#overlay");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("updateItem:"+item);
        if(null == item || empty) {
            text.textProperty().unbind();
            text.setText("");
            icon.imageProperty().unbind();
            icon.setImage(null);
            overlay.visibleProperty().unbind();
            overlay.setVisible(true);
            return;
        }
        text.textProperty().bind(item.makeAndModelProperty());
        icon.imageProperty().bind(item.imageProperty());
        overlay.visibleProperty().bind(item.connectedProperty().not());
//        setUserData(item);
//        textProperty().set(item.makeAndModelProperty().get());
        
    }
}