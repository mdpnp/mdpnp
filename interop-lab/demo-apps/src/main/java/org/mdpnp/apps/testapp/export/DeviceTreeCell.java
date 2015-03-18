package org.mdpnp.apps.testapp.export;

import javafx.scene.control.cell.CheckBoxTreeCell;

import org.mdpnp.apps.testapp.Device;

public class DeviceTreeCell extends CheckBoxTreeCell<Object> {
    @Override
    public void updateItem(Object item, boolean empty) {
        textProperty().unbind();
        
        super.updateItem(item, empty);
        
        if(item != null) {
            if(item instanceof Device) {
                textProperty().bind( ((Device)item).makeAndModelProperty());
            } else {
                textProperty().set(item.toString());
            }
        } else {
            textProperty().set("");
        }
    }
}
