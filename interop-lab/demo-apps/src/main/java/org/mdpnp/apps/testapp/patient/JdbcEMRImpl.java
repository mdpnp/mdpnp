package org.mdpnp.apps.testapp.patient;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author mfeinberg
 */
class JdbcEMRImpl implements EMRFacade {

    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }
    public void setDataSource(DataSource jdbcDB) {
        this.dataSource = jdbcDB;
    }


    public void deleteDevicePatientAssociation(DevicePatientAssociation assoc) {
        DevicePatientAssociation.delete(dataSource, assoc);
    }

    public DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc) {
        return DevicePatientAssociation.update(dataSource, assoc);
    }

    public boolean createPatient(PatientInfo p) {
        return PatientInfo.createPatient(dataSource, p);
    }

    @Override
    public List<PatientInfo> getPatients() {
        return PatientInfo.queryAll(dataSource);
    }
}