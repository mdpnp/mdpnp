package org.mdpnp.apps.testapp.patient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.sql.DataSource;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author mfeinberg
 */
class JdbcEMRImpl implements EMRFacade {

    private DataSource dataSource;
    private ObservableList<PatientInfo> patients = FXCollections.observableArrayList();
    private final Executor collectionUpdateHandler;

    public JdbcEMRImpl(Executor executor) {
        this.collectionUpdateHandler = executor;
    }
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
        collectionUpdateHandler.execute(() -> {
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
        collectionUpdateHandler.execute(() -> {
            patients.retainAll(currentPatients);
            Iterator<PatientInfo> itr = currentPatients.iterator();
            while (itr.hasNext()) {
                PatientInfo pi = itr.next();
                if (!patients.contains(pi)) {
                    patients.add(pi);
                }
            }
        });
    }
}