package org.mdpnp.apps.testapp.diag;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import org.mdpnp.apps.testapp.MyAlert;
import org.mdpnp.apps.testapp.MyNumeric;
import org.mdpnp.apps.testapp.MySampleArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagnosticApplication {
    
    protected static final Logger log = LoggerFactory.getLogger(DiagnosticApplication.class);
        
    @FXML protected TableView<MyNumeric> numericTable;
    @FXML protected TableView<MySampleArray> sampleArrayTable;
    @FXML protected TableView <MyAlert> patientAlertTable, technicalAlertTable;
    
    public DiagnosticApplication() {
    }
    
    
    public void stop() {
        
    }

    public void setModel(Diagnostic diagnostic) {
        numericTable.setItems(diagnostic.getNumericModel());
        sampleArrayTable.setItems(diagnostic.getSampleArrayModel());
        patientAlertTable.setItems(diagnostic.getPatientAlertModel());
        technicalAlertTable.setItems(diagnostic.getTechnicalAlertModel());
        
    }
}

