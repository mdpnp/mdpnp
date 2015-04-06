package org.mdpnp.apps.testapp.diag;

import javafx.collections.ObservableList;

import org.mdpnp.apps.fxbeans.AlertFx;
import org.mdpnp.apps.fxbeans.AlertFxList;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;

import com.rti.dds.subscription.Subscriber;

public class Diagnostic {
    private final Subscriber subscriber;
    private final EventLoop eventLoop;
    private final NumericFxList numerics;
    private final AlertFxList patientAlerts, technicalAlerts;
    private final SampleArrayFxList sampleArrays;
    
    public Diagnostic(Subscriber subscriber, EventLoop eventLoop) {
        this.subscriber = subscriber;
        this.eventLoop = eventLoop;
        numerics = new NumericFxList(ice.NumericTopic.VALUE);
        patientAlerts = new AlertFxList(ice.PatientAlertTopic.VALUE);
        technicalAlerts = new AlertFxList(ice.TechnicalAlertTopic.VALUE);
        sampleArrays = new SampleArrayFxList(ice.SampleArrayTopic.VALUE);
    }
    
    public ObservableList<NumericFx> getNumericModel() {
        return numerics;
    }
    
    public ObservableList<AlertFx> getPatientAlertModel() {
        return patientAlerts;
    }
    
    public ObservableList<AlertFx> getTechnicalAlertModel() {
        return technicalAlerts;
    }
    
    public ObservableList<SampleArrayFx> getSampleArrayModel() {
        return sampleArrays;
    }
    
    public void start() {
        patientAlerts.start(subscriber, eventLoop, null, null, QosProfiles.ice_library, QosProfiles.state);
        technicalAlerts.start(subscriber, eventLoop, null, null, QosProfiles.ice_library, QosProfiles.state);
        sampleArrays.start(subscriber, eventLoop, null, null, QosProfiles.ice_library, QosProfiles.waveform_data);
        numerics.start(subscriber, eventLoop, null, null, QosProfiles.ice_library, QosProfiles.numeric_data);
    }
    
    public void stop() {
        patientAlerts.stop();
        technicalAlerts.stop();
        sampleArrays.stop();
        numerics.stop();
    }
}
