package org.mdpnp.apps.testapp.export;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class DataFilter {

    private final DeviceTreeModel controller;
    private boolean enabled = true;

    public DataFilter(DeviceTreeModel dtm) {
        controller = dtm;
    }

    @Subscribe
    public void handleDataSampleEvent(NumericsDataCollector.NumericSampleEvent evt) throws Exception {
        if(isEnabledFor(evt))
            fireDataSampleEvent(evt);
    }

    @Subscribe
    public void handleDataSampleEvent(SampleArrayDataCollector.SampleArrayEvent evt) throws Exception {
        if(isEnabledFor(evt))
            fireDataSampleEvent(evt);
    }

    @Subscribe
    public void handleDataSampleEvent(PatientAssessmentDataCollector.PatientAssessmentEvent evt) throws Exception {
        if(isEnabledFor(evt))
            fireDataSampleEvent(evt);
    }

    private boolean isEnabledFor(DataCollector.DataSampleEvent value) {
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

    public void fireDataSampleEvent(DataCollector.DataSampleEvent data) throws Exception{
        eventBus.post(data);
    }
}
