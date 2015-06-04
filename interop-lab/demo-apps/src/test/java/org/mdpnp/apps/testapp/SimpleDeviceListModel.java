package org.mdpnp.apps.testapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SimpleDeviceListModel implements DeviceListModel {

    private ObservableList<Device> contents = FXCollections.observableArrayList();
    
    @Override
    public Device getByUniqueDeviceIdentifier(String udi) {
        for(Device d : contents) {
            if(udi.equals(d.getUDI())) {
                return d;
            }
        }
        Device d = new Device(udi);
        contents.add(d);
        return d;
    }

    @Override
    public ObservableList<Device> getContents() {
        return contents;
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void tearDown() {
        // TODO Auto-generated method stub
        
    }

}
