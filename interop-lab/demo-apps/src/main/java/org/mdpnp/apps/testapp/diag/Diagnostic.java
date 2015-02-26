package org.mdpnp.apps.testapp.diag;

import org.mdpnp.rtiapi.data.AlertInstanceModel;
import org.mdpnp.rtiapi.data.AlertInstanceModelImpl;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.NumericInstanceModel;
import org.mdpnp.rtiapi.data.NumericInstanceModelImpl;
import org.mdpnp.rtiapi.data.QosProfiles;

import com.rti.dds.subscription.Subscriber;

public class Diagnostic {
    private final Subscriber subscriber;
    private final EventLoop eventLoop;
    private final NumericInstanceTableModel numericModel;
    private final AlertInstanceTableModel patientAlertModel, technicalAlertModel;
    
    public Diagnostic(Subscriber subscriber, EventLoop eventLoop) {
        this.subscriber = subscriber;
        this.eventLoop = eventLoop;
        NumericInstanceModel numericModel = new NumericInstanceModelImpl(ice.NumericTopic.VALUE);
        this.numericModel = new NumericInstanceTableModel(numericModel);
        AlertInstanceModel patientAlertModel = new AlertInstanceModelImpl(ice.PatientAlertTopic.VALUE);
        this.patientAlertModel = new AlertInstanceTableModel(patientAlertModel);
        AlertInstanceModel technicalAlertModel = new AlertInstanceModelImpl(ice.TechnicalAlertTopic.VALUE);
        this.technicalAlertModel = new AlertInstanceTableModel(technicalAlertModel);
    }
    
    public NumericInstanceTableModel getNumericModel() {
        return numericModel;
    }
    
    public AlertInstanceTableModel getPatientAlertModel() {
        return patientAlertModel;
    }
    
    public AlertInstanceTableModel getTechnicalAlertModel() {
        return technicalAlertModel;
    }
    
    public void start() {
        numericModel.getInstanceModel().start(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.numeric_data);
        patientAlertModel.getInstanceModel().start(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
        technicalAlertModel.getInstanceModel().start(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
    }
    
    public void stop() {
        numericModel.getInstanceModel().stop();
        patientAlertModel.getInstanceModel().stop();
        technicalAlertModel.getInstanceModel().stop();
    }
}
