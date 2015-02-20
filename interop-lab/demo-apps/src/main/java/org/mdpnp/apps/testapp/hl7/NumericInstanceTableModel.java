package org.mdpnp.apps.testapp.hl7;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.mdpnp.rtiapi.data.NumericInstanceModel;

@SuppressWarnings("serial")
public class NumericInstanceTableModel extends AbstractTableModel implements TableModel, ListDataListener {
    private final NumericInstanceModel numericInstanceModel;
    
    public NumericInstanceTableModel(final NumericInstanceModel numericInstanceModel) {
        this.numericInstanceModel = numericInstanceModel;
        this.numericInstanceModel.addListDataListener(this);
    }
    
    @Override
    public int getRowCount() {
        return numericInstanceModel.getSize();
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        ice.Numeric data = numericInstanceModel.getElementAt(rowIndex);
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

    @Override
    public void intervalAdded(ListDataEvent e) {
        fireTableRowsInserted(e.getIndex0(), e.getIndex1());
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
    }

}
