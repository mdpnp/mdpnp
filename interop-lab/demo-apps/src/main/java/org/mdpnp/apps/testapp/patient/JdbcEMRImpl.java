package org.mdpnp.apps.testapp.patient;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.sql.DataSource;

import java.util.Iterator;
import java.util.List;

/**
 * @author mfeinberg
 */
class JdbcEMRImpl implements EMRFacade {

    private DataSource dataSource;
    private ObservableList<PatientInfo> patients = FXCollections.observableArrayList();

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
        Platform.runLater( () -> {
            patients.add(p);
        });
        return PatientInfo.createPatient(dataSource, p);
    }

    @Override
    public ObservableList<PatientInfo> getPatients() {
        return patients;
    }
    
    @Override
    public void refresh() {
        final List<PatientInfo> currentPatients = PatientInfo.queryAll(dataSource);
        Platform.runLater( () -> {
            patients.retainAll(currentPatients);
            Iterator<PatientInfo> itr = currentPatients.iterator();
            while(itr.hasNext()) {
                PatientInfo pi = itr.next();
                if(!patients.contains(pi)) {
                    patients.add(pi);
                }
            }
        });
    }
}