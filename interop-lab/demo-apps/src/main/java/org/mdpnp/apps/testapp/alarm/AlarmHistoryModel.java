package org.mdpnp.apps.testapp.alarm;

import ice.Alert;
import ice.AlertDataReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.mdpnp.rtiapi.data.AlertInstanceModel;
import org.mdpnp.rtiapi.data.AlertInstanceModelImpl;
import org.mdpnp.rtiapi.data.AlertInstanceModelListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.InstanceModel;

import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;

@SuppressWarnings("serial")
public class AlarmHistoryModel extends AbstractTableModel {

    static class HistoricAlarm {
        private final Date timestamp;
        private final String type;
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
            data.add(0,new HistoricAlarm(type, new Date(sampleInfo.source_timestamp.sec * 1000L + sampleInfo.source_timestamp.nanosec / 1000000L), new ice.Alert(alert)));
            fireTableRowsInserted(0, 0);
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
    
    protected final List<HistoricAlarm> data = Collections.synchronizedList(new ArrayList<HistoricAlarm>(100));
    
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        HistoricAlarm ha = data.get(rowIndex);
        switch(columnIndex) {
        case 0:
            return ha.getTimestamp();
        case 1:
            return ha.getAlert().unique_device_identifier;
        case 2:
            return ha.getType();
        case 3:
            return ha.getAlert().identifier;
        case 4:
            return ha.getAlert().text;
        default:
            return null;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
        case 0:
            return Date.class;
        case 1:
        case 2:
        case 3:
        case 4:
            return String.class;
        default:
            return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch(column) {
        case 0:
            return "Time";
        case 1:
            return "UDI";
        case 2:
            return "Type";
        case 3:
            return "Identifier";
        case 4:
            return "Value";
        default:
            return null;
        }
    }
}
