package org.mdpnp.apps.testapp;

import ice.Numeric;
import ice.NumericDataReader;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;
import org.mdpnp.rtiapi.data.NumericInstanceModel;

import com.rti.dds.subscription.SampleInfo;

public class MyNumericItems implements InstanceModelListener<Numeric, NumericDataReader> {
    private NumericInstanceModel model;
    protected final ObservableList<MyNumeric> items = FXCollections.observableArrayList();
    protected final Map<String, MyNumeric> numericsByKey = new HashMap<String, MyNumeric>();
    
    public MyNumericItems() {
        
    }
    
    public MyNumericItems setModel(NumericInstanceModel model) {
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
                    numericsByKey.clear();
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
    
    public NumericInstanceModel getModel() {
        return model;
    }
    
    public ObservableList<MyNumeric> getItems() {
        return items;
    }

    @Override
    public void instanceSample(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
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
    public void instanceNotAlive(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric keyHolder, SampleInfo sampleInfo) {
        final ice.Numeric key = new ice.Numeric(keyHolder);
        Platform.runLater(new Runnable() {
            public void run() {
                MyNumeric n = numericsByKey.remove(MyNumeric.key(key));
                if(n != null) {
                    items.remove(n);
                }
            }
        });
    }
    
    @Override
    public void instanceAlive(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
        final MyNumeric n = new MyNumeric(data, sampleInfo);
        Platform.runLater(new Runnable() {
            public void run() {
                numericsByKey.put(n.key(), n);
                items.add(n);
            }
        });
    }

}
