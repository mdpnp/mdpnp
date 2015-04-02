package org.mdpnp.apps.testapp.patient;

import javafx.beans.property.Property;
import org.mdpnp.apps.testapp.Device;

import javax.sql.DataSource;

/**
 * @author mfeinberg
 */
public class DevicePatientAssociation {

    private final Device device;
    private final PatientInfo  patient;

    public DevicePatientAssociation(final Device d, PatientInfo p) {
        device = d;
        patient = p;
    }

    boolean isForDevice(Device d) {
        return device.getUDI().equals(d.getUDI());
    }

    Device getDevice() {
        return device;
    }

    PatientInfo getPatient() {
        return patient;
    }

    public Property<String> deviceNameProperty() {
        return device.makeAndModelProperty();
    }
    public String getDeviceName() {
        return device.makeAndModelProperty().getValue();
    }
    public Property<String> patientIdentifierProperty() {
        return patient.patientNameProperty();
    }
    public String getPatientIdentifier() {
        return patient.getPatientName();
    }
    public Property<String> deviceIdentifierProperty() {
        return device.serial_numberProperty();
    }

    //
    // DAO APIs
    //
    static DevicePatientAssociation update(DataSource dataSource, DevicePatientAssociation pi) {
        //JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        //List<PatientInfo> l = jdbcTemplate.query("select * from PATIENT_INFO", new PatientInfoRowMapper());
        return pi;
    }

    static int delete(DataSource dataSource, DevicePatientAssociation pi) {
        //JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        //List<PatientInfo> l = jdbcTemplate.query("select * from PATIENT_INFO", new PatientInfoRowMapper());
        return 0;
    }

}