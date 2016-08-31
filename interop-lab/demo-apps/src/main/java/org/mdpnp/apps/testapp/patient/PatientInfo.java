package org.mdpnp.apps.testapp.patient;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Date;


public class PatientInfo {

    public static final String UNKNOWN_NAME="Unknown to EMR";

    public enum Gender { M, F, U };

    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty mrn = new SimpleStringProperty();
    private final ObjectProperty<Date> dob = new SimpleObjectProperty<>();
    private final ObjectProperty<Gender> gender = new SimpleObjectProperty<>();

    public PatientInfo(String mrn, String fn, String ln, Gender g, Date d) {

        if(mrn == null || fn == null || ln == null || g == null || d == null)
            throw new IllegalArgumentException("Patient specification is invalid");

        this.mrn.setValue(mrn);
        this.firstName.setValue(fn);
        this.lastName.setValue(ln);
        this.gender.setValue(g);
        this.dob.setValue(d);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getMrn() {
        return mrn.get();
    }

    public StringProperty mrnProperty() {
        return mrn;
    }

    public Date getDob() {
        return dob.get();
    }

    public ObjectProperty<Date> dobProperty() {
        return dob;
    }

    public Gender getGender() {
        return gender.get();
    }

    public ObjectProperty<Gender> genderProperty() {
        return gender;
    }

    @Override
    public String toString() {
        String first = firstName.get();
        String last = lastName.get();
        if(null == first || "".equals(first)) {
            if(null == last || "".equals(last)) {
                return "Unknown";
            } else {
                return last;
            }
        } else {
            if(null == last || "".equals(last)) {
                return first;
            } else {
                return firstName.getValue() + " " + lastName.getValue();
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PatientInfo) {
            return getMrn().equals(((PatientInfo)obj).getMrn());
        } else {
            return false;
        }
    }

    ice.Patient asIcePatient() {
        ice.Patient p = new ice.Patient();
        p.mrn = getMrn();
        p.family_name = getLastName();
        p.given_name = getFirstName();
        return p;
    }

    @Override
    public int hashCode() {
        return getMrn().hashCode();
    }

}
