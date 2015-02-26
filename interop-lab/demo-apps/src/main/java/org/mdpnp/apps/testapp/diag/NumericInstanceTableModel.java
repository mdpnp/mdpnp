package org.mdpnp.apps.testapp.diag;

import javax.swing.event.ListDataListener;
import javax.swing.table.TableModel;

import org.mdpnp.rtiapi.data.InstanceModel;
import org.mdpnp.rtiapi.data.InstanceTableModel;

@SuppressWarnings("serial")
public class NumericInstanceTableModel extends InstanceTableModel<ice.Numeric, ice.NumericDataReader> implements TableModel, ListDataListener {
    
    public NumericInstanceTableModel(final InstanceModel<ice.Numeric, ice.NumericDataReader> instanceModel) {
        super(instanceModel);
    }

    @Override
    public int getColumnCount() {
        return 6; 
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
        case 0:
            return "UDI";
        case 1:
            return "Metric Id";
        case 2:
            return "Vendor Id";
        case 3:
            return "Instance Id";
        case 4:
            return "Units";
        case 5:
            return "Value";
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
        case 3:
            return Integer.class;
        case 4:
            return String.class;
        case 5:
            return Float.class;
        default:
            return null;
        }
    }

    @Override
    public Object getValueAt(ice.Numeric data, int columnIndex) {
        switch(columnIndex) {
        case 0:
            return data.unique_device_identifier;
        case 1:
            return data.metric_id;
        case 2:
            return data.vendor_metric_id;
        case 3:
            return data.instance_id;
        case 4:
            return data.unit_id;
        case 5:
            return data.value;
        }
        return null;
    }

}
