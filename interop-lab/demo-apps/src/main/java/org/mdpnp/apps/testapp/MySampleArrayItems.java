package org.mdpnp.apps.testapp;

import ice.SampleArray;
import ice.SampleArrayDataReader;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.mdpnp.rtiapi.data.InstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModel;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.SampleInfo;

public class MySampleArrayItems implements InstanceModelListener<SampleArray, SampleArrayDataReader> {
    private SampleArrayInstanceModel model;
    protected final ObservableList<MySampleArray> items = FXCollections.observableArrayList();
    protected final Map<InstanceHandle_t, MySampleArray> byHandle = new HashMap<InstanceHandle_t, MySampleArray>();
    
    public MySampleArrayItems() {
        
    }
    
    public MySampleArrayItems setModel(SampleArrayInstanceModel model) {
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
                    byHandle.clear();
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
    
    public SampleArrayInstanceModel getModel() {
        return model;
    }
    
    public ObservableList<MySampleArray> getItems() {
        return items;
    }

    @Override
    public void instanceSample(InstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data, SampleInfo sampleInfo) {
        final ice.SampleArray d = new ice.SampleArray(data);
        SampleInfo s = new SampleInfo();
        InstanceHandle_t handle = new InstanceHandle_t(sampleInfo.instance_handle);
        s.copy_from(sampleInfo);
        Platform.runLater(new Runnable() {
            public void run() {
                MySampleArray n = byHandle.get(handle);
                if(n != null) {
                    n.update(d, s);
                }
            }
        });
    }
    
    @Override
    public void instanceNotAlive(InstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray keyHolder, SampleInfo sampleInfo) {
        final InstanceHandle_t instance = new InstanceHandle_t(sampleInfo.instance_handle);
        Platform.runLater(new Runnable() {
            public void run() {
                MySampleArray n = byHandle.remove(instance);
                if(n != null) {
                    items.remove(n);
                }
            }
        });
    }
    
    @Override
    public void instanceAlive(InstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data, SampleInfo sampleInfo) {
        final MySampleArray n = new MySampleArray(data, sampleInfo);
        Platform.runLater(new Runnable() {
            public void run() {
                byHandle.put(n.getHandle(), n);
                items.add(n);
            }
        });
    }

}
