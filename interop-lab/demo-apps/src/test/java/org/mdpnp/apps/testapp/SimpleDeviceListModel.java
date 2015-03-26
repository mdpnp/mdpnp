package org.mdpnp.apps.testapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SimpleDeviceListModel implements DeviceListModel {

    private ObservableList<Device> contents = FXCollections.observableArrayList();
    
    @Override
    public Device getByIceIdentifier(String ice_id) {
        for(Device d : contents) {
            if(ice_id.equals(d.getIceIdentifier())) {
                return d;
            }
        }
        Device d = new Device(ice_id);
        contents.add(d);
        return d;
    }

    @Override
    public ObservableList<Device> getContents() {
        return contents;
    }

}
