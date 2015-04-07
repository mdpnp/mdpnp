package org.mdpnp.apps.testapp.diag;

import javafx.collections.ObservableList;

import org.mdpnp.apps.fxbeans.AlertFx;
import org.mdpnp.apps.fxbeans.AlertFxList;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;

public class Diagnostic {
    private final NumericFxList numerics;
    private final AlertFxList patientAlerts, technicalAlerts;
    private final SampleArrayFxList sampleArrays;
    
    public Diagnostic(AlertFxList patientAlerts, AlertFxList technicalAlerts, NumericFxList numerics, SampleArrayFxList sampleArrayList) {
        this.numerics = numerics;
        this.sampleArrays = sampleArrayList;
        this.patientAlerts = patientAlerts;
        this.technicalAlerts = technicalAlerts;
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
    
}
