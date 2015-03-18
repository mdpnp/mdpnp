package org.mdpnp.apps.testapp;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.mdpnp.apps.testapp.IceApplicationProvider.AppType;

public class AppTypeCellFactory implements Callback<GridView<AppType>,GridCell<AppType>> {

    private final EventHandler<MouseEvent> clickHandler;
    
    public AppTypeCellFactory(final EventHandler<MouseEvent> clickHandler) {
        this.clickHandler = clickHandler;
    }
    
    @Override
    public GridCell<AppType> call(GridView<AppType> param) {
        final AppTypeGridCell cell = new AppTypeGridCell();
        cell.setOnMouseClicked(clickHandler);
        return cell;
    }
}
