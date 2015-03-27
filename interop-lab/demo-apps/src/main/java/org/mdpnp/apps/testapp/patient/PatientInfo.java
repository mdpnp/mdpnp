package org.mdpnp.apps.testapp.patient;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    static class PatientInfoRowMapper implements RowMapper<PatientInfo> {
        public PatientInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            PatientInfo p = new PatientInfo(rs.getString("PATIENT_ID"));
            return p;
        }
    }

    //
    // DAO APIs
    //
    static List<PatientInfo> queryAll(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<PatientInfo> l = jdbcTemplate.query("select * from PATIENT_INFO", new PatientInfoRowMapper());
        return l;
    }

}
