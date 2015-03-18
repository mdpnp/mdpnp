package org.mdpnp.apps.testapp.export;

import javax.swing.event.EventListenerList;

public class DataFilter implements DataCollector.DataSampleEventListener {

    private final DeviceTreeModel controller;
    private boolean enabled = true;

    public DataFilter(DeviceTreeModel dtm) {
        controller = dtm;
    }

    public void handleDataSampleEvent(DataCollector.DataSampleEvent evt) throws Exception {
        Value value = (Value) evt.getSource();
        if(isEnabledFor(value))
            fireDataSampleEvent(evt);
    }

    private boolean isEnabledFor(Value value) {
        return enabled && controller.isEnabled(value);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    EventListenerList listenerList = new EventListenerList();

    public void addDataSampleListener(DataCollector.DataSampleEventListener l) {
        listenerList.add(DataCollector.DataSampleEventListener.class, l);
    }

    public void removeDataSampleListener(DataCollector.DataSampleEventListener l) {
        listenerList.remove(DataCollector.DataSampleEventListener.class, l);
    }

    public void fireDataSampleEvent(DataCollector.DataSampleEvent data) throws Exception{
        DataCollector.DataSampleEventListener listeners[] = listenerList.getListeners(DataCollector.DataSampleEventListener.class);
        for(DataCollector.DataSampleEventListener l : listeners) {
            l.handleDataSampleEvent(data);
        }
    }
}
