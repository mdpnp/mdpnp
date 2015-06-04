package org.mdpnp.apps.testapp;

import javafx.collections.ObservableList;

public interface DeviceListModel {
    Device getByUniqueDeviceIdentifier(String udi);
    ObservableList<Device> getContents();
    
    void start();
    void tearDown();

}