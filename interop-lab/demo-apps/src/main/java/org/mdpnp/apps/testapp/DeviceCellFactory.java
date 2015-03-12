package org.mdpnp.apps.testapp;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

public class DeviceCellFactory implements Callback<GridView<Device>,GridCell<Device>> {

    private final EventHandler<MouseEvent> clickHandler;
    
    public DeviceCellFactory(final EventHandler<MouseEvent> clickHandler) {
        this.clickHandler = clickHandler;
    }
    
    @Override
    public GridCell<Device> call(GridView<Device> param) {
        try {
            DeviceGridCell cell = new DeviceGridCell();
            cell.setOnMouseClicked(clickHandler);
            return cell;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
