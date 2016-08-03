package org.mdpnp.apps.testapp.export;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javax.swing.event.EventListenerList;

public class DataFilter {

    private final DeviceTreeModel controller;
    private boolean enabled = true;

    public DataFilter(DeviceTreeModel dtm) {
        controller = dtm;
    }

    @Subscribe
    public void handleDataSampleEvent(NumericsDataCollector.NumericSampleEvent evt) throws Exception {
        Value value = evt.getValue();
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

    private final EventBus eventBus = new EventBus();

    public void addDataSampleListener(Object l) {
        eventBus.register(l);
    }

    public void removeDataSampleListener(Object l) {
        eventBus.unregister(l);
    }

    public void fireDataSampleEvent(NumericsDataCollector.NumericSampleEvent data) throws Exception{
        eventBus.post(data);
    }
}
