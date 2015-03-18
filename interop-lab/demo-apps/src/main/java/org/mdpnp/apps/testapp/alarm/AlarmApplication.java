package org.mdpnp.apps.testapp.alarm;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmApplication {    
    protected static final Logger log = LoggerFactory.getLogger(AlarmApplication.class);
    
    
    @FXML protected TableView<HistoricAlarm> table;

    public AlarmApplication() {
    }
    
    public void setModel(ObservableList<HistoricAlarm> model) {
        table.setItems(model);
    }

    public void stop() {

    }


}
