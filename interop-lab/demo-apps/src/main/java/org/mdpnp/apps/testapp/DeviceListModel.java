package org.mdpnp.apps.testapp;

import javafx.collections.ObservableList;

public interface DeviceListModel {
    Device getByIceIdentifier(String ice_id);
    ObservableList<Device> getContents();

}