package org.mdpnp.apps.testapp;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import org.controlsfx.control.GridCell;
import org.mdpnp.apps.testapp.IceApplicationProvider.AppType;

public final class AppTypeGridCell extends GridCell<AppType> {
    private Parent root;

    @FXML private Label text;

    @FXML private ImageView icon;
    
    public AppTypeGridCell() {
        
    }
    
    @Override
    protected void updateItem(AppType item, boolean empty) {
        super.updateItem(item, empty);

        if(null == root) {
            FXMLLoader loader = new FXMLLoader(AppTypeGridCell.class.getResource("AppTypeListCell.fxml"));
            loader.setController(this);
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(null == item) {
            setText(null);
            setGraphic(null);
            return;
        }
        text.setText(item.getName());
        icon.setImage(item.getIcon());
        setUserData(item);
        setText(item.getName());
        setGraphic(root);
    }
}