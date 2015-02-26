package org.mdpnp.apps.testapp.diag;

import javax.swing.event.ListDataListener;
import javax.swing.table.TableModel;

import org.mdpnp.rtiapi.data.InstanceModel;
import org.mdpnp.rtiapi.data.InstanceTableModel;

@SuppressWarnings("serial")
public class AlertInstanceTableModel extends InstanceTableModel<ice.Alert, ice.AlertDataReader> implements TableModel, ListDataListener {
    public AlertInstanceTableModel(final InstanceModel<ice.Alert, ice.AlertDataReader> instanceModel) {
        super(instanceModel);
    }

    @Override
    public int getColumnCount() {
        return 3; 
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
        case 0:
            return "UDI";
        case 1:
            return "Identifier";
        case 2:
            return "Text";
        default:
            return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
        case 0:
        case 1:
        case 2:
            return String.class;
        default:
            return null;
        }
    }

    @Override
    public Object getValueAt(ice.Alert data, int columnIndex) {
        switch(columnIndex) {
        case 0:
            return data.unique_device_identifier;
        case 1:
            return data.identifier;
        case 2:
            return data.text;
        }
        return null;
    }


}
