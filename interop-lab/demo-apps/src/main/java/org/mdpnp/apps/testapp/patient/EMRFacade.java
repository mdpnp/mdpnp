package org.mdpnp.apps.testapp.patient;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import org.mdpnp.apps.testapp.ControlFlowHandler;
import org.mdpnp.devices.MDSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import ca.uhn.fhir.context.FhirContext;
import javafx.collections.ObservableList;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author mfeinberg
 */
public abstract class EMRFacade {

	/**
	 * An enum to track the different possible types.
	 * @author simon
	 *
	 */
	public static enum EMRType {
		UNKNOWN,
		JDBC_ONLY,
		FHIR_ONLY,
		JDBC_AND_FHIR,
		OPENEMR
	}

	/**
	 * An instance variable denoting which of the possible EMRType
	 * values this instance provides.  Used so that an OpenICE application
	 * class that is receiving a bean of type emr can tell if that bean is
	 * an OpenEMR instance without checking the class of the bean.
	 */
	protected EMRType emrType=EMRType.UNKNOWN;

	public EMRType getEMRType() {
		return emrType;
	}

    private final ListHandler listHandler;

    public EMRFacade(Executor executor) {
        listHandler = new ListHandler(FXCollections.observableArrayList(), executor);
    }

    public EMRFacade(ListHandler handler) {
        listHandler = handler;
    }

    public void refresh() {
        List<PatientInfo> currentPatients = fetchAllPatients();
        listHandler.refresh(currentPatients);
    }

    public ObservableList<PatientInfo> getPatients() {
        return listHandler.getPatients();
    }

    abstract List<PatientInfo> fetchAllPatients();

    public boolean createPatient(PatientInfo p) {
        listHandler.createPatient(p);
        return true;
    }

    public boolean deletePatient(PatientInfo p) {
        listHandler.deletePatient(p);
        return true;
    }

    public boolean addPatient(PatientInfo p) {
        listHandler.add(p);
        return true;
    }

    public abstract void deleteDevicePatientAssociation(DevicePatientAssociation assoc);
    public abstract DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc);

    static class ListHandler {

        private final ObservableList<PatientInfo> patientList;
        private final Executor collectionUpdateHandler;

        private ListHandler(ObservableList<PatientInfo> patients, Executor executor) {
            patientList = patients;
            collectionUpdateHandler = executor;
        }

        public void refresh(List<PatientInfo> currentPatients) {

            collectionUpdateHandler.execute(() -> {
                patientList.retainAll(currentPatients);
                Iterator<PatientInfo> itr = currentPatients.iterator();
                while (itr.hasNext()) {
                    PatientInfo pi = itr.next();
                    if (!patientList.contains(pi)) {
                        patientList.add(pi);
                    }
                }
            });
        }

        public ObservableList<PatientInfo> getPatients() {
            return patientList;
        }

        public void createPatient(PatientInfo p) {
            collectionUpdateHandler.execute(() -> {
                patientList.add(p);
            });
        }

        public void deletePatient(PatientInfo p) {
            collectionUpdateHandler.execute(() -> {
                patientList.remove(p);
            });
        }

        public void add(PatientInfo p) {
            collectionUpdateHandler.execute(() -> {
                if (!patientList.contains(p)) {
                    patientList.add(p);
                }
            });
        }
    }

    static final ListHandler NOOP_HANDLER = new ListHandler(null, null)
    {
        @Override
        public void createPatient(PatientInfo p) {
        }

        @Override
        public void deletePatient(PatientInfo p) {
        }

        @Override
        public ObservableList<PatientInfo> getPatients() {
            throw new UnsupportedOperationException("UNDEFINED");
        }

        @Override
        public void refresh(List<PatientInfo> currentPatients) {
        }

        @Override
        public void add(PatientInfo p) {
        }
    };

    public static class EMRFacadeFactory implements FactoryBean<EMRFacade> {

        private static final Logger log = LoggerFactory.getLogger(EMRFacade.class);

        private DataSource         jdbcDB;
        private String             fhirEMRUrl;
        private String             openEMRUrl;
        private FhirContext        fhirContext;
        private ControlFlowHandler controlFlowHandler;
        private MDSHandler         mdsHandler;

        public String getUrl() {
            return fhirEMRUrl;
        }
        public void setUrl(String url) {
            fhirEMRUrl = url;
        }
        
		public String getOpenEMRUrl() {
			return openEMRUrl;
		}
		public void setOpenEMRUrl(String openEMRUrl) {
			this.openEMRUrl = openEMRUrl;
		}

        public FhirContext getFhirContext() {
            return fhirContext;
        }
        public void setFhirContext(FhirContext fhirContext) {
            this.fhirContext = fhirContext;
        }

        public DataSource getJdbcDB() {
            return jdbcDB;
        }
        public void setJdbcDB(DataSource jdbcDB) {
            this.jdbcDB = jdbcDB;
        }

        public void setControlFlowHandler(ControlFlowHandler controlFlowHandler) {
            this.controlFlowHandler = controlFlowHandler;
        }
        public ControlFlowHandler getControlFlowHandler() {
            return controlFlowHandler;
        }

        public MDSHandler getMdsHandler() {
            return mdsHandler;
        }
        public void setMdsHandler(MDSHandler mdsHandler) {
            this.mdsHandler = mdsHandler;
        }

        EMRFacade instance = null;

        @Override
        public EMRFacade getObject() throws Exception {
            if(instance == null) {
				//For now, OpenEMR becomes the number one choice.
				if(openEMRUrl!=null && openEMRUrl.length()>0) {
					instance=new OpenEMRImpl(new ExecutorFx());
					((OpenEMRImpl)instance).setUrl(openEMRUrl);
				}

				else if(fhirEMRUrl == null || fhirEMRUrl.isEmpty()) {
                    if(jdbcDB == null)
                        throw new IllegalStateException("JDBC database cannot be null");
                    instance = new JdbcEMRImpl(new ExecutorFx());
                    ((JdbcEMRImpl) instance).setDataSource(jdbcDB);
                }
                else if(jdbcDB == null) { // but there is a non-empty fhir url...
                    if (!FhirEMRImpl.isServerThere(fhirEMRUrl))
                        throw new IllegalStateException("No fhir server at >" + fhirEMRUrl + "< url");
                    instance = new FhirEMRImpl(new ExecutorFx());
                    ((FhirEMRImpl) instance).setUrl(fhirEMRUrl);
                    ((FhirEMRImpl) instance).setFhirContext(fhirContext);
                }
                else { // both are listed
                    if(FhirEMRImpl.isServerThere(fhirEMRUrl)) {
                        instance = new JdbcFhirEMRImpl(new ExecutorFx());
                        ((JdbcFhirEMRImpl) instance).setDataSource(jdbcDB);
                        ((JdbcFhirEMRImpl) instance).setUrl(fhirEMRUrl);
                        ((JdbcFhirEMRImpl) instance).setFhirContext(fhirContext);
                    }
                    else {
                        boolean ok = controlFlowHandler.confirmError(
                                "FHIR server not found",
                                "Requested jdbc/fhir-backed EMR, but there is no server at >" +
                                fhirEMRUrl + "< url; " + "defaulting to jdbc-only",
                                true);
                        if(!ok)
                            throw new ControlFlowHandler.ConfirmedError("No fhir server at " + fhirEMRUrl);

                        instance = new JdbcEMRImpl(new ExecutorFx());
                        ((JdbcEMRImpl)instance).setDataSource(jdbcDB);
                    }
                }
            }

            new Thread( () -> instance.refresh(), "EMRFacade refresh").start();

            // If factory is configured with MDS handler to listen to external
            // events, register a listener to sync the list with changes made by other
            // supervisors
            //
            if(mdsHandler != null) {
                mdsHandler.addPatientListener(new MDSHandler.Patient.PatientListener() {
                    public void handlePatientChange(MDSHandler.Patient.PatientEvent evt) {

                        ice.Patient state = (ice.Patient) evt.getSource();
                        PatientInfo pi = new PatientInfo(state.mrn, state.given_name, state.family_name, PatientInfo.Gender.U, new Date());
                        instance.addPatient(pi);
                    }
                });
            }

            return instance;
        }

        @Override
        public Class<?> getObjectType() {
            return EMRFacade.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    public static class ExecutorFx implements Executor {

        @Override
        public void execute(Runnable command) {
            Platform.runLater(command);
        }
    }
}
