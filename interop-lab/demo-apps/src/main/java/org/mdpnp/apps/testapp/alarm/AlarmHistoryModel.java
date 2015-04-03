package org.mdpnp.apps.testapp.alarm;

import ice.Alert;
import ice.AlertDataReader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.mdpnp.rtiapi.data.AlertInstanceModel;
import org.mdpnp.rtiapi.data.AlertInstanceModelImpl;
import org.mdpnp.rtiapi.data.AlertInstanceModelListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.QosProfiles;

import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;

public class AlarmHistoryModel {

    private final ObservableList<HistoricAlarm> contents = FXCollections.observableArrayList();
    
    public ObservableList<HistoricAlarm> getContents() {
        return contents;
    }
    
    private final AlertInstanceModel patientAlerts, technicalAlerts;
    
    class Listener implements AlertInstanceModelListener {
        private final String type;
        
        public Listener(final String type) {
            this.type = type;
        }

        @Override
        public void instanceAlive(ReaderInstanceModel<Alert, AlertDataReader> model, AlertDataReader reader, Alert data, SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceNotAlive(ReaderInstanceModel<Alert, AlertDataReader> model, AlertDataReader reader, Alert keyHolder, SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceSample(ReaderInstanceModel<Alert, AlertDataReader> model, AlertDataReader reader, Alert alert, SampleInfo sampleInfo) {
            final HistoricAlarm ha = new HistoricAlarm(alert, sampleInfo, type);
            Platform.runLater(new Runnable() {
                public void run() {
                    contents.add(0, ha);
                }
            });
        }
        
        
    }
    
    
    public void start(Subscriber subscriber, EventLoop eventLoop) {
        patientAlerts.startReader(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
        technicalAlerts.startReader(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
    }
    
    public void stop() {
        patientAlerts.stopReader();
        technicalAlerts.stopReader();
    }
    
    public AlarmHistoryModel(final String patientTopic, final String technicalTopic) {
        this(new AlertInstanceModelImpl(patientTopic), new AlertInstanceModelImpl(technicalTopic));
        
    }
    
    public AlarmHistoryModel(final AlertInstanceModel patientAlerts, final AlertInstanceModel technicalAlerts) {
        this.patientAlerts = patientAlerts;
        this.technicalAlerts = technicalAlerts;
        patientAlerts.iterateAndAddListener(new Listener("Patient"));
        technicalAlerts.iterateAndAddListener(new Listener("Technical"));
    }
    
}
