package org.mdpnp.apps.testapp.diag;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import org.mdpnp.apps.fxbeans.AlarmLimitFx;
import org.mdpnp.apps.fxbeans.AlertFx;
import org.mdpnp.apps.fxbeans.GlobalAlarmLimitObjectiveFx;
import org.mdpnp.apps.fxbeans.InfusionStatusFx;
import org.mdpnp.apps.fxbeans.LocalAlarmLimitObjectiveFx;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.testapp.validate.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagnosticApplication {
    
    protected static final Logger log = LoggerFactory.getLogger(DiagnosticApplication.class);
        
    @FXML protected TableView<NumericFx> numericTable;
    @FXML protected TableView<SampleArrayFx> sampleArrayTable;
    @FXML protected TableView <AlertFx> patientAlertTable, technicalAlertTable;
    @FXML protected TableView<Validation> validationTable;
    @FXML protected TableView<InfusionStatusFx> infusionStatusTable;
    @FXML protected TableView<AlarmLimitFx> alarmLimitTable;
    @FXML protected TableView<LocalAlarmLimitObjectiveFx> localAlarmLimitObjectiveTable;
    @FXML protected TableView<GlobalAlarmLimitObjectiveFx> globalAlarmLimitObjectiveTable;
    
    public DiagnosticApplication() {
    }
    
    
    public void stop() {
        
    }

    public void setModel(Diagnostic diagnostic) {
        numericTable.setItems(diagnostic.getNumericModel());
        sampleArrayTable.setItems(diagnostic.getSampleArrayModel());
        patientAlertTable.setItems(diagnostic.getPatientAlertModel());
        technicalAlertTable.setItems(diagnostic.getTechnicalAlertModel());
        validationTable.setItems(diagnostic.getValidationOracle());
        infusionStatusTable.setItems(diagnostic.getInfusionStatusList());
        alarmLimitTable.setItems(diagnostic.getAlarmLimitModel());
        localAlarmLimitObjectiveTable.setItems(diagnostic.getLocalAlarmLimitObjectiveModel());
        globalAlarmLimitObjectiveTable.setItems(diagnostic.getGlobalAlarmLimitObjectiveModel());
    }
}

