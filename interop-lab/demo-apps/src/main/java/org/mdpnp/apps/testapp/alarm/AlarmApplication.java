package org.mdpnp.apps.testapp.alarm;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.util.Date;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import org.mdpnp.apps.testapp.alarm.AlarmHistoryModel.HistoricAlarm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class AlarmApplication {    
    protected static final Logger log = LoggerFactory.getLogger(AlarmApplication.class);
    
    
    @FXML protected TableView<HistoricAlarm> table;

    public AlarmApplication() {
//        table.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {
//            DateFormat formatter;
//
//            public void setValue(Object value) {
//                if (formatter==null) {
//                    formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
//                }
//                setText((value == null) ? "" : formatter.format(value));
//            }
//        });
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        add(new JScrollPane(table), BorderLayout.CENTER);
    }
    
    public void setModel(ObservableList<HistoricAlarm> model) {
        table.setItems(model);
    }

    public void stop() {

    }


}
