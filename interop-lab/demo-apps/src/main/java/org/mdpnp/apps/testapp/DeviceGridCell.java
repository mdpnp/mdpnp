package org.mdpnp.apps.testapp;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tooltip;

import org.controlsfx.control.GridCell;

public class DeviceGridCell extends GridCell<Device> {
    private Parent root;
    private DeviceController deviceController;

    public DeviceGridCell() throws IOException {
    }
    
    @Override
    protected void updateItem(Device item, boolean empty) {
        super.updateItem(item, empty);

        if(null == root) {
            FXMLLoader loader = new FXMLLoader(DeviceController.class.getResource("Device.fxml"));
            try {
                root = loader.load();
                deviceController = loader.getController();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        deviceController.bind(item);
        
        if(null == getTooltip()) {
            setTooltip(new Tooltip(""));
        }
        
        tooltipProperty().get().textProperty().unbind();
        textProperty().unbind();
        
        setUserData(item);
        deviceController.bind(item);
        
        if(null == item) {
            tooltipProperty().get().textProperty().set("");
            setText(null);
            setGraphic(null);
        } else {
        	if(item.getComPort()!=null && item.getComPort().length()!=0) {
        		tooltipProperty().get().textProperty().bind ( Bindings.concat(item.hostnameProperty(), item.comPortProperty()));
        		textProperty().bind(Bindings.concat( item.makeAndModelProperty(), item.comPortProperty()));
        	} else {
        		tooltipProperty().get().textProperty().bind(item.hostnameProperty());
        		textProperty().bind(item.makeAndModelProperty());
        	}
            
            setGraphic(root);
        }
        
    }
}
