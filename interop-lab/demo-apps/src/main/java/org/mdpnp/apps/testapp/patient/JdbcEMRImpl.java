package org.mdpnp.apps.testapp.patient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author mfeinberg
 */
class JdbcEMRImpl extends EMRFacade {

    private static final Logger log = LoggerFactory.getLogger(JdbcEMRImpl.class);

    private DataSource dataSource;

    public JdbcEMRImpl(Executor executor) {
        super(executor);
    }

    public JdbcEMRImpl() {
        super(NOOP_HANDLER);
    }

    public DataSource getDataSource() {
        return dataSource;
    }
    public void setDataSource(DataSource jdbcDB) {
        this.dataSource = jdbcDB;
    }


    @Override
    public void deleteDevicePatientAssociation(DevicePatientAssociation assoc) {
        // NO-OP
    }

    @Override
    public DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc) {
        // NO-OP
        return assoc;
    }

    @Override
    public boolean createPatient(PatientInfo p) {
        return createPatient(dataSource, p) && super.createPatient(p);
    }

    @Override
    public boolean deletePatient(PatientInfo p) {
        return deletePatient(dataSource, p) && super.deletePatient(p);
    }

    public boolean updatePatient(PatientInfo p) {
        return updatePatient(dataSource, p);
    }


    @Override
    public List<PatientInfo> fetchAllPatients() {
        return fetchAllPatients(dataSource);
    }

    static class PatientInfoRowMapper implements RowMapper<PatientInfo> {
        public PatientInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            PatientInfo p = new PatientInfo(
                    rs.getString("MRN"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("LAST_NAME"),
                    PatientInfo.Gender.valueOf(rs.getString("GENDER")),
                    rs.getDate("DOB"));
            return p;
        }
    }

    //
    // DAO APIs
    //
    private static final String CREATE_SQL=
            "INSERT INTO PATIENT_INFO (MRN,FIRST_NAME,LAST_NAME,DOB,GENDER) VALUES  (?,?,?,?,?)";

    static boolean createPatient(DataSource dataSource, PatientInfo p) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(CREATE_SQL,
                new Object[]{
                        p.getMrn(),
                        p.getFirstName(),
                        p.getLastName(),
                        p.getDob(),
                        p.getGender().name()
                });

        return true;
    }

    static List<PatientInfo> fetchAllPatients(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<PatientInfo> l = jdbcTemplate.query("select * from PATIENT_INFO", new PatientInfoRowMapper());
        return l;
    }

    static final String DELSQL = "DELETE FROM PATIENT_INFO WHERE mrn = ?";
    static final String UPDATESQL = "UPDATE PATIENT_INFO SET FIRST_NAME=?,LAST_NAME=?,DOB=?,GENDER=? WHERE mrn = ?";

    static boolean deletePatient(DataSource dataSource, PatientInfo p) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Object[] params = {p.getMrn()};
        int rows = jdbcTemplate.update(DELSQL, params);
        return rows==1;
    }

    static boolean updatePatient(DataSource dataSource, PatientInfo p) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Object[] params = {p.getFirstName(),
                           p.getLastName(),
                           p.getDob(),
                           p.getGender().name(),
                           p.getMrn()};
        int[] types = {Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.VARCHAR, Types.VARCHAR};

        int rows = jdbcTemplate.update(UPDATESQL, params, types);
        log.info(rows + " row(s) updated.");
        return rows==1;
    }

}