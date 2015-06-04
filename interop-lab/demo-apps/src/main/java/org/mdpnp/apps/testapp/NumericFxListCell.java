package org.mdpnp.apps.testapp;

import org.mdpnp.apps.fxbeans.NumericFx;

import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

public class NumericFxListCell extends ListCell<NumericFx> {
    private final DeviceListModel deviceListModel;
    
    public NumericFxListCell(final DeviceListModel deviceListModel) {
        this.deviceListModel = deviceListModel;
    }
    
    ImageView root;
    
    @Override
    protected void updateItem(NumericFx item, boolean empty) {
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
            Device device = deviceListModel.getByUniqueDeviceIdentifier(item.getUnique_device_identifier());
            if(null == device) {
                root.imageProperty().unbind();
                root.setImage(null);
                textProperty().bind(item.unique_device_identifierProperty());
            } else {
                getTooltip().textProperty().bind(item.unique_device_identifierProperty());
                root.imageProperty().bind(device.imageProperty());
                textProperty().bind(device.makeAndModelProperty());
            }
        }
    }
}
