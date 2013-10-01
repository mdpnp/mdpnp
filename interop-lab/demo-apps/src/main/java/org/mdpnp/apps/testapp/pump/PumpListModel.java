package org.mdpnp.apps.testapp.pump;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

@SuppressWarnings({ "serial", "rawtypes" })
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
            listener.intervalAdded(new ListDataEvent(PumpListModel.this, ListDataEvent.INTERVAL_ADDED, 0, 0));
            listener.contentsChanged(new ListDataEvent(PumpListModel.this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1));
        }
        @Override
        public void pumpRemoved(PumpModel model, Pump pump) {
            listener.intervalRemoved(new ListDataEvent(PumpListModel.this, ListDataEvent.INTERVAL_REMOVED, 0, 0));
            if(getSize() > 0) {
                listener.contentsChanged(new ListDataEvent(PumpListModel.this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1));
            }

        }
        @Override
        public void pumpChanged(PumpModel model, Pump pump) {
            // IMPORTANT NOTE ... CONSUMERS OF THE LIST MODEL SHOULD NOT
            // USE VARIABLE STATE INFO IN THEIR RENDERINGS
//            listener.contentsChanged(e)
//            // TODO maybe remove this ... could just be noise for this purpose
//            fireContentsChanged(PumpListModel.this, 0, getSize() - 1);
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

    @Override
    public void addListDataListener(ListDataListener l) {
        model.addListener(new PumpListModelListener(l));
    }
    @Override
    public void removeListDataListener(ListDataListener l) {
        model.removeListener(new PumpListModelListener(l));
    }
}
