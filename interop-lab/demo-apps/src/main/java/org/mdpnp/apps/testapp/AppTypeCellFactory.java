package org.mdpnp.apps.testapp;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.mdpnp.apps.testapp.IceApplicationProvider.AppType;

public class AppTypeCellFactory implements Callback<GridView<AppType>,GridCell<AppType>> {

    @Override
    public GridCell<AppType> call(GridView<AppType> param) {
        return new AppTypeGridCell();
    }
}
