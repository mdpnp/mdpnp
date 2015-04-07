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
    
    public Diagnostic(Subscriber subscriber, EventLoop eventLoop, NumericFxList numerics, SampleArrayFxList sampleArrayList) {
        this.subscriber = subscriber;
        this.eventLoop = eventLoop;
        this.numerics = numerics;
        this.sampleArrays = sampleArrayList;
        patientAlerts = new AlertFxList(ice.PatientAlertTopic.VALUE);
        technicalAlerts = new AlertFxList(ice.TechnicalAlertTopic.VALUE);
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
    }
    
    public void stop() {
        patientAlerts.stop();
        technicalAlerts.stop();
    }
}
