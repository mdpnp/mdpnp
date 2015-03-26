package org.mdpnp.apps.testapp;

import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

public class MySampleArrayListCell extends ListCell<MySampleArray> {
    private final DeviceListModel deviceListModel;
    
    public MySampleArrayListCell(final DeviceListModel deviceListModel) {
        this.deviceListModel = deviceListModel;
    }
    
    ImageView root;
    
    @Override
    protected void updateItem(MySampleArray item, boolean empty) {
        super.updateItem(item, empty);
        if(null == root) {
            root = new ImageView();
            root.setPreserveRatio(true);
            root.setFitWidth(100);
            setTooltip(new Tooltip(""));
            setGraphic(root);
        }
        if(null == item) {
            root.imageProperty().unbind();
            root.setImage(null);
            textProperty().unbind();
            getTooltip().textProperty().unbind();
            getTooltip().setText("");
            setText("");
        } else {
            Device device = deviceListModel.getByIceIdentifier(item.getIce_id());
            if(null == device) {
                root.imageProperty().unbind();
                root.setImage(null);
                textProperty().bind(item.ice_idProperty());
            } else {
                getTooltip().textProperty().bind(item.ice_idProperty());
                root.imageProperty().bind(device.imageProperty());
                textProperty().bind(device.makeAndModelProperty());
            }
        }
    }
}
