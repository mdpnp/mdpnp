package org.mdpnp.apps.testapp;

import ice.InfusionStatus;
import ice.InfusionStatusDataReader;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModelListener;
import org.mdpnp.rtiapi.data.InstanceModel;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.SampleInfo;

public class MyInfusionStatusItems implements InfusionStatusInstanceModelListener {
    private InfusionStatusInstanceModel model;
    protected final ObservableList<MyInfusionStatus> items = FXCollections.observableArrayList();
    protected final Map<InstanceHandle_t, MyInfusionStatus> byHandle = new HashMap<InstanceHandle_t, MyInfusionStatus>();
    
    public MyInfusionStatusItems() {
        
    }
    
    public MyInfusionStatusItems setModel(InfusionStatusInstanceModel model) {
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
    
    public InfusionStatusInstanceModel getModel() {
        return model;
    }
    
    public ObservableList<MyInfusionStatus> getItems() {
        return items;
    }

    @Override
    public void instanceSample(InstanceModel<InfusionStatus, InfusionStatusDataReader> model, InfusionStatusDataReader reader, InfusionStatus data, SampleInfo sampleInfo) {
        if(sampleInfo.valid_data) {
            final ice.InfusionStatus d = new ice.InfusionStatus(data);
            SampleInfo s = new SampleInfo();
            InstanceHandle_t handle = new InstanceHandle_t(sampleInfo.instance_handle);
            s.copy_from(sampleInfo);
            
            Platform.runLater(new Runnable() {
                public void run() {
                    MyInfusionStatus n = byHandle.get(handle);
                    if(null == n) {
                        n = new MyInfusionStatus(d, s);
                        byHandle.put(n.getHandle(), n);
                        items.add(n);
                    } else {
                        n.update(d, s);
                    }
                }
            });
        }
    }
    
    @Override
    public void instanceNotAlive(InstanceModel<InfusionStatus, InfusionStatusDataReader> model, InfusionStatusDataReader reader, InfusionStatus keyHolder, SampleInfo sampleInfo) {
        final InstanceHandle_t instance = new InstanceHandle_t(sampleInfo.instance_handle);
        Platform.runLater(new Runnable() {
            public void run() {
                MyInfusionStatus n = byHandle.remove(instance);
                if(n != null) {
                    items.remove(n);
                }
            }
        });
    }
    
    @Override
    public void instanceAlive(InstanceModel<InfusionStatus, InfusionStatusDataReader> model, InfusionStatusDataReader reader, InfusionStatus data, SampleInfo sampleInfo) {
    }
}
