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

    enum Gender { male, female };

    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty id = new SimpleStringProperty();

    public PatientInfo(String id, String fn, String ln) {
        this.id.setValue(id);
        this.firstName.setValue(fn);
        this.lastName.setValue(ln);
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

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    @Override
    public String toString() {
        return firstName.getValue() + " " + lastName.getValue();

    }

    static class PatientInfoRowMapper implements RowMapper<PatientInfo> {
        public PatientInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            PatientInfo p = new PatientInfo(rs.getString("PATIENT_ID"), "", "");
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
