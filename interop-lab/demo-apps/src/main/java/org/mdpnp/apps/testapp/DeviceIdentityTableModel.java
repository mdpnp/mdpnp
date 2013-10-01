package org.mdpnp.apps.testapp;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class DeviceIdentityTableModel extends AbstractTableModel implements
        ListDataListener {
    @SuppressWarnings("rawtypes")
    private final ListModel model;

    public DeviceIdentityTableModel(@SuppressWarnings("rawtypes") ListModel model) {
        this.model = model;
        model.addListDataListener(this);
    }

    @Override
    public int getRowCount() {
        return model.getSize();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ice.DeviceIdentity d = (ice.DeviceIdentity) model.getElementAt(rowIndex);
        switch (columnIndex) {
        case 0:
            return d.unique_device_identifier;
        case 1:
            return d.manufacturer;
        case 2:
            return d.model;
        default:
            return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "UDI";
        case 1:
            return "Manufacturer";
        case 2:
            return "Model";
        default:
            return null;
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
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