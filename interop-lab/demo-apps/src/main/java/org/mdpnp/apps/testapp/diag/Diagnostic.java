package org.mdpnp.apps.testapp.diag;

import java.util.HashMap;
import java.util.Map;

import ice.Numeric;
import ice.NumericDataReader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.mdpnp.rtiapi.data.AlertInstanceModel;
import org.mdpnp.rtiapi.data.AlertInstanceModelImpl;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.InstanceModel;
import org.mdpnp.rtiapi.data.NumericInstanceModel;
import org.mdpnp.rtiapi.data.NumericInstanceModelImpl;
import org.mdpnp.rtiapi.data.NumericInstanceModelListener;
import org.mdpnp.rtiapi.data.QosProfiles;

import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;

public class Diagnostic {
    private final Subscriber subscriber;
    private final EventLoop eventLoop;
    private final NumericInstanceModel numericModel;
    private final AlertInstanceModel patientAlertModel, technicalAlertModel;
    
    private final ObservableList<MyNumeric> numerics = FXCollections.observableArrayList();
    private final Map<String, MyNumeric> numericsByKey = new HashMap<String, MyNumeric>();
    private final ObservableList<ice.Alert> patientAlerts = FXCollections.observableArrayList(), technicalAlerts = FXCollections.observableArrayList();
    
    public Diagnostic(Subscriber subscriber, EventLoop eventLoop) {
        this.subscriber = subscriber;
        this.eventLoop = eventLoop;
        numericModel = new NumericInstanceModelImpl(ice.NumericTopic.VALUE);
        numericModel.iterateAndAddListener(new NumericInstanceModelListener() {
            
            @Override
            public void instanceSample(InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
                final ice.Numeric d = new ice.Numeric(data);
                SampleInfo s = new SampleInfo();
                s.copy_from(sampleInfo);
                Platform.runLater(new Runnable() {
                    public void run() {
                        MyNumeric n = numericsByKey.get(MyNumeric.key(d));
                        if(n != null) {
                            n.update(d, s);
                        }
                    }
                });
            }
            
            @Override
            public void instanceNotAlive(InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric keyHolder, SampleInfo sampleInfo) {
                final ice.Numeric key = new ice.Numeric(keyHolder);
                Platform.runLater(new Runnable() {
                    public void run() {
                        MyNumeric n = numericsByKey.remove(MyNumeric.key(key));
                        if(n != null) {
                            numerics.remove(n);
                        }
                    }
                });
            }
            
            @Override
            public void instanceAlive(InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
                final MyNumeric n = new MyNumeric(data, sampleInfo);
                Platform.runLater(new Runnable() {
                    public void run() {
                        numericsByKey.put(n.key(), n);
                        numerics.add(n);
                    }
                });
            }
        });
        patientAlertModel = new AlertInstanceModelImpl(ice.PatientAlertTopic.VALUE);
        technicalAlertModel = new AlertInstanceModelImpl(ice.TechnicalAlertTopic.VALUE);
    }
    
    public ObservableList<MyNumeric> getNumericModel() {
        return numerics;
    }
    
    public ObservableList<ice.Alert> getPatientAlertModel() {
        return patientAlerts;
    }
    
    public ObservableList<ice.Alert> getTechnicalAlertModel() {
        return technicalAlerts;
    }
    
    public void start() {
        System.out.println("Starting the models");
        numericModel.start(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.numeric_data);
        patientAlertModel.start(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
        technicalAlertModel.start(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
    }
    
    public void stop() {
        numericModel.stop();
        patientAlertModel.stop();
        technicalAlertModel.stop();
    }
}
