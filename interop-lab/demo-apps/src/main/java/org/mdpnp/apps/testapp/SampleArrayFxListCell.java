package org.mdpnp.apps.testapp;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;

import org.mdpnp.apps.fxbeans.SampleArrayFx;

public class SampleArrayFxListCell extends ListCell<SampleArrayFx> {
    private final DeviceListModel deviceListModel;
    private DeviceController deviceController;
    
    public SampleArrayFxListCell(final DeviceListModel deviceListModel) {
        this.deviceListModel = deviceListModel;
    }
    
    private Parent root;
    
    @Override
    protected void updateItem(SampleArrayFx item, boolean empty) {
        super.updateItem(item, empty);
        if(null == root) {
            FXMLLoader loader = new FXMLLoader(DeviceController.class.getResource("Device.fxml"));
            try {
                root = loader.load();
                deviceController = loader.getController();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            setTooltip(new Tooltip(""));
            setGraphic(root);
        }
        
        if(null == item) {
            deviceController.bind(null);
            textProperty().unbind();
            getTooltip().textProperty().unbind();
            getTooltip().setText("");
            setText("");
            setGraphic(null);
        } else {
            getTooltip().textProperty().bind(item.unique_device_identifierProperty());
            Device device = deviceListModel.getByUniqueDeviceIdentifier(item.getUnique_device_identifier());
            if(null == device) {
                deviceController.bind(null);
                textProperty().bind(item.unique_device_identifierProperty());
            } else {
                deviceController.bind(device);
                textProperty().bind(device.makeAndModelProperty());
            }
            setGraphic(root);
        }
    }
}
