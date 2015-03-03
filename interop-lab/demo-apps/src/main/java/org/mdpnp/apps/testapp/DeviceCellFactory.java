package org.mdpnp.apps.testapp;

import java.io.IOException;

import javafx.util.Callback;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

public class DeviceCellFactory implements Callback<GridView<Device>,GridCell<Device>> {

    @Override
    public GridCell<Device> call(GridView<Device> param) {
        try {
            return new DeviceGridCell();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
