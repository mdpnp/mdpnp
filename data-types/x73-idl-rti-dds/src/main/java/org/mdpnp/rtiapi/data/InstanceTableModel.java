package org.mdpnp.rtiapi.data;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.subscription.DataReaderImpl;

@SuppressWarnings("serial")
public abstract class InstanceTableModel<D extends Copyable, R extends DataReaderImpl> extends AbstractTableModel implements TableModel, ListDataListener {
    private final InstanceModel<D,R> instanceModel;
    
    public InstanceTableModel(final InstanceModel<D, R> instanceModel) {
        this.instanceModel = instanceModel;
        this.instanceModel.addListDataListener(this);
    }
    
    @Override
    public int getRowCount() {
        return instanceModel.getSize();
    }
    
    public InstanceModel<D, R> getInstanceModel() {
        return instanceModel;
    }


    public abstract Object getValueAt(D data, int columnIndex);
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        D data = instanceModel.getElementAt(rowIndex);
        return getValueAt(data, columnIndex);
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
