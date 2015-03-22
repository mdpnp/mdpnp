package org.mdpnp.apps.testapp.diag;

import javafx.collections.ObservableList;

import org.mdpnp.apps.testapp.MyAlert;
import org.mdpnp.apps.testapp.MyAlertItems;
import org.mdpnp.apps.testapp.MyNumeric;
import org.mdpnp.apps.testapp.MyNumericItems;
import org.mdpnp.apps.testapp.MySampleArray;
import org.mdpnp.apps.testapp.MySampleArrayItems;
import org.mdpnp.rtiapi.data.AlertInstanceModel;
import org.mdpnp.rtiapi.data.AlertInstanceModelImpl;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.NumericInstanceModel;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModel;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModelImpl;

import com.rti.dds.subscription.Subscriber;

public class Diagnostic {
    private final Subscriber subscriber;
    private final EventLoop eventLoop;
    private final NumericInstanceModel numericModel;
    private final MyNumericItems numericItems;
    private final AlertInstanceModel patientAlertModel, technicalAlertModel;
    private final MyAlertItems patientAlertItems, technicalAlertItems;
    private final SampleArrayInstanceModel sampleArrayModel;
    private final MySampleArrayItems sampleArrayItems;
    
    public Diagnostic(Subscriber subscriber, EventLoop eventLoop, NumericInstanceModel model) {
        this.subscriber = subscriber;
        this.eventLoop = eventLoop;
        numericModel = model;
        numericItems = new MyNumericItems().setModel(numericModel);
        patientAlertModel = new AlertInstanceModelImpl(ice.PatientAlertTopic.VALUE);
        patientAlertItems = new MyAlertItems().setModel(patientAlertModel);
        technicalAlertModel = new AlertInstanceModelImpl(ice.TechnicalAlertTopic.VALUE);
        technicalAlertItems = new MyAlertItems().setModel(technicalAlertModel);
        sampleArrayModel = new SampleArrayInstanceModelImpl(ice.SampleArrayTopic.VALUE);
        sampleArrayItems = new MySampleArrayItems().setModel(sampleArrayModel);
    }
    
    public ObservableList<MyNumeric> getNumericModel() {
        return numericItems.getItems();
    }
    
    public ObservableList<MyAlert> getPatientAlertModel() {
        return patientAlertItems.getItems();
    }
    
    public ObservableList<MyAlert> getTechnicalAlertModel() {
        return technicalAlertItems.getItems();
    }
    
    public ObservableList<MySampleArray> getSampleArrayModel() {
        return sampleArrayItems.getItems();
    }
    
    public void start() {
        patientAlertModel.start(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
        technicalAlertModel.start(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
        sampleArrayModel.start(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.waveform_data);
    }
    
    public void stop() {
        patientAlertModel.stop();
        technicalAlertModel.stop();
        sampleArrayModel.stop();
    }
}
