package org.mdpnp.apps.testapp.patient;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PatientInfo {

    private final StringProperty patientName = new SimpleStringProperty();

    public PatientInfo(String n) {
        patientName.setValue(n);
    }

    public String getPatientName() {
        return patientName.get();
    }

    public StringProperty patientNameProperty() {
        return patientName;
    }

    @Override
    public String toString() {
        return patientName.getValue();

    }
}
