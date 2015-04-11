package org.mdpnp.apps.testapp.diag;

import javafx.collections.ObservableList;

import org.mdpnp.apps.fxbeans.AlertFx;
import org.mdpnp.apps.fxbeans.AlertFxList;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.validate.ValidationOracle;

public class Diagnostic {
    private final NumericFxList numerics;
    private final AlertFxList patientAlerts, technicalAlerts;
    private final SampleArrayFxList sampleArrays;
    private final ValidationOracle validationOracle;
    
    public Diagnostic(AlertFxList patientAlerts, AlertFxList technicalAlerts, NumericFxList numerics, SampleArrayFxList sampleArrayList, ValidationOracle validationOracle) {
        this.numerics = numerics;
        this.sampleArrays = sampleArrayList;
        this.patientAlerts = patientAlerts;
        this.technicalAlerts = technicalAlerts;
        this.validationOracle = validationOracle;
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
    
    public ValidationOracle getValidationOracle() {
        return validationOracle;
    }
    
}
