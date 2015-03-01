package org.mdpnp.apps.testapp;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import org.controlsfx.control.GridCell;
import org.mdpnp.apps.testapp.IceApplicationProvider.AppType;

public final class AppTypeGridCell extends GridCell<AppType> {
    private Parent root;

    private Label text;

    private ImageView icon;
    
    public AppTypeGridCell() {
        
    }
    
    @Override
    protected void updateItem(AppType item, boolean empty) {
        super.updateItem(item, empty);

        if(null == root) {
            FXMLLoader loader = new FXMLLoader(AppTypeGridCell.class.getResource("AppTypeListCell.fxml"));
            try {
                root = loader.load();
                text = (Label) root.lookup("#text");
                icon = (ImageView) root.lookup("#icon");
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