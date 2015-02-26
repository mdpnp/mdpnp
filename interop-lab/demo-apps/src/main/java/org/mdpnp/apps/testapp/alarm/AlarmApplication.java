package org.mdpnp.apps.testapp.alarm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class AlarmApplication extends JPanel {    
    protected static final Logger log = LoggerFactory.getLogger(AlarmApplication.class);
    protected final JTable table = new JTable();
    
    public AlarmApplication() {
        setLayout(new BorderLayout());
        
        table.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {
            DateFormat formatter;

            public void setValue(Object value) {
                if (formatter==null) {
                    formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
                }
                setText((value == null) ? "" : formatter.format(value));
            }
        });
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
    
    public void setModel(TableModel model) {
        table.setModel(model);
    }

    public void stop() {

    }


}
