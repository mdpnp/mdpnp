package org.mdpnp.apps.testapp.co2;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

@SuppressWarnings({ "serial", "rawtypes" })
public class CapnoListModel extends AbstractListModel implements ListModel {
    private final CapnoModel model;

    private class CapnoListModelListener implements CapnoModelListener {
        private final ListDataListener listener;

        public CapnoListModelListener(ListDataListener listener) {
            this.listener = listener;
        }
        @Override
        public int hashCode() {
            return listener.hashCode();
        }
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof CapnoListModelListener) {
                return this.listener.equals(((CapnoListModelListener)obj).listener);
            } else {
                return false;
            }
        }

        @Override
        public void capnoAdded(CapnoModel model, Capno capno) {
            listener.intervalAdded(new ListDataEvent(CapnoListModel.this, ListDataEvent.INTERVAL_ADDED, 0, 0));
            listener.contentsChanged(new ListDataEvent(CapnoListModel.this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1));
        }
        @Override
        public void capnoRemoved(CapnoModel model, Capno capno) {
            listener.intervalRemoved(new ListDataEvent(CapnoListModel.this, ListDataEvent.INTERVAL_REMOVED, 0, 0));
            if(getSize() > 0) {
                listener.contentsChanged(new ListDataEvent(CapnoListModel.this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1));
            }

        }
        @Override
        public void capnoChanged(CapnoModel model, Capno pump) {
            // NOT RELAYING CHANGES TO CONTENTS
            // DO NOT RENDER VARIABLE STATE
        }

    }

    public CapnoListModel(CapnoModel model) {
        this.model = model;
    }
    @Override
    public int getSize() {
        return model.getCount();
    }

    @Override
    public Object getElementAt(int index) {
        return model.getCapno(index);
    }
    @Override
    public void addListDataListener(ListDataListener l) {
        model.addCapnoListener(new CapnoListModelListener(l));
    }
    @Override
    public void removeListDataListener(ListDataListener l) {
        model.removeCapnoListener(new CapnoListModelListener(l));
    }

}

