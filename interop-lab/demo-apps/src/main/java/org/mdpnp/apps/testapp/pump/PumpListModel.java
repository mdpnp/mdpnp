package org.mdpnp.apps.testapp.pump;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class PumpListModel extends AbstractListModel implements ListModel {
    private final PumpModel model;
    
    private class PumpListModelListener implements PumpModelListener {
        private final ListDataListener listener;
        
        public PumpListModelListener(ListDataListener listener) {
            this.listener = listener;
        }
        @Override
        public int hashCode() {
            return listener.hashCode();
        }
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof PumpListModelListener) {
                return this.listener.equals(((PumpListModelListener)obj).listener);
            } else {
                return false;
            }
        }
        
        @Override
        public void pumpAdded(PumpModel model, Pump pump) {
            fireIntervalAdded(PumpListModel.this, 0, 0);
            fireContentsChanged(PumpListModel.this, 0, getSize()-1);
        }
        @Override
        public void pumpRemoved(PumpModel model, Pump pump) {
            fireIntervalRemoved(PumpListModel.this, 0, 0);
            if(getSize() > 0) {
                fireContentsChanged(PumpListModel.this, 0, getSize()-1);
            }
            
        }
        @Override
        public void pumpChanged(PumpModel model, Pump pump) {
            // TODO maybe remove this ... could just be noise for this purpose
            fireContentsChanged(PumpListModel.this, 0, getSize() - 1);
        }
        
    }
    
    public PumpListModel(PumpModel model) {
        this.model = model;
    }
    @Override
    public int getSize() {
        return model.getCount();
    }

    @Override
    public Object getElementAt(int index) {
        return model.getPump(index);
    }

}
