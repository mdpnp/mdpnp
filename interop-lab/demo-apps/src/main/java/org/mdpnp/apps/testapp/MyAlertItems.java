package org.mdpnp.apps.testapp;

import ice.Alert;
import ice.AlertDataReader;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.mdpnp.rtiapi.data.AlertInstanceModel;
import org.mdpnp.rtiapi.data.InstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;

import com.rti.dds.subscription.SampleInfo;

public class MyAlertItems implements InstanceModelListener<Alert, AlertDataReader> {
    private AlertInstanceModel model;
    protected final ObservableList<MyAlert> items = FXCollections.observableArrayList();
    protected final Map<String, MyAlert> alertsByKey = new HashMap<String, MyAlert>();
    
    public MyAlertItems() {
        
    }
    
    public MyAlertItems setModel(AlertInstanceModel model) {
        if(null != this.model) {
            if(model != null) {
                if(this.model.equals(model)) {
                    // Shortcut out if same model
                    return this;
                }
            }
            this.model.removeListener(this);
            Platform.runLater(new Runnable() {
                public void run() {
                    alertsByKey.clear();
                    items.clear();
                }
            });
            
        }
        this.model = model;
        if(null != this.model) {
            this.model.iterateAndAddListener(this);
        }
        return this;
    }
    
    public AlertInstanceModel getModel() {
        return model;
    }
    
    public ObservableList<MyAlert> getItems() {
        return items;
    }

    @Override
    public void instanceSample(InstanceModel<Alert, AlertDataReader> model, AlertDataReader reader, Alert data, SampleInfo sampleInfo) {
        final ice.Alert d = new ice.Alert(data);
        SampleInfo s = new SampleInfo();
        s.copy_from(sampleInfo);
        Platform.runLater(new Runnable() {
            public void run() {
                MyAlert n = alertsByKey.get(MyAlert.key(d));
                if(n != null) {
                    n.update(d, s);
                }
            }
        });
    }
    
    @Override
    public void instanceNotAlive(InstanceModel<Alert, AlertDataReader> model, AlertDataReader reader, Alert keyHolder, SampleInfo sampleInfo) {
        final ice.Alert key = new ice.Alert(keyHolder);
        Platform.runLater(new Runnable() {
            public void run() {
                MyAlert n = alertsByKey.remove(MyAlert.key(key));
                if(n != null) {
                    items.remove(n);
                }
            }
        });
    }
    
    @Override
    public void instanceAlive(InstanceModel<Alert, AlertDataReader> model, AlertDataReader reader, Alert data, SampleInfo sampleInfo) {
        final MyAlert n = new MyAlert(data, sampleInfo);
        Platform.runLater(new Runnable() {
            public void run() {
                alertsByKey.put(n.key(), n);
                items.add(n);
            }
        });
    }

}
