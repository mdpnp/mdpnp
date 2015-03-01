package org.mdpnp.apps.testapp.alarm;

import ice.Alert;
import ice.AlertDataReader;

import java.util.Date;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.mdpnp.rtiapi.data.AlertInstanceModel;
import org.mdpnp.rtiapi.data.AlertInstanceModelImpl;
import org.mdpnp.rtiapi.data.AlertInstanceModelListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.InstanceModel;

import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;

public class AlarmHistoryModel {

    private final ObservableList<HistoricAlarm> contents = FXCollections.observableArrayList();
    
    public ObservableList<HistoricAlarm> getContents() {
        return contents;
    }
    
    static class HistoricAlarm {
        private Date timestamp;
        private String type;
        private final ice.Alert alert;
        public HistoricAlarm(final String type, final Date timestamp, final ice.Alert alert ) {
            this.type = type;
            this.timestamp = timestamp;
            this.alert = alert;
        }
        public ice.Alert getAlert() {
            return alert;
        }
        public Date getTimestamp() {
            return timestamp;
        }
        public String getType() {
            return type;
        }
    }
    
    private final AlertInstanceModel patientAlerts, technicalAlerts;
    
    class Listener implements AlertInstanceModelListener {
        private final String type;
        
        public Listener(final String type) {
            this.type = type;
        }

        @Override
        public void instanceAlive(InstanceModel<Alert, AlertDataReader> model, AlertDataReader reader, Alert data, SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceNotAlive(InstanceModel<Alert, AlertDataReader> model, AlertDataReader reader, Alert keyHolder, SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceSample(InstanceModel<Alert, AlertDataReader> model, AlertDataReader reader, Alert alert, SampleInfo sampleInfo) {
            final HistoricAlarm ha = new HistoricAlarm(type, new Date(sampleInfo.source_timestamp.sec * 1000L + sampleInfo.source_timestamp.nanosec / 1000000L), new ice.Alert(alert));
            System.out.println("CREATED AN ALERT INSTANCE");
            Platform.runLater(new Runnable() {
                public void run() {
                    contents.add(0, ha);
                }
            });
        }
        
        
    }
    
    
    public void start(Subscriber subscriber, EventLoop eventLoop) {
        patientAlerts.start(subscriber, eventLoop);
        technicalAlerts.start(subscriber, eventLoop);
    }
    
    public void stop() {
        patientAlerts.stop();
        technicalAlerts.stop();
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
