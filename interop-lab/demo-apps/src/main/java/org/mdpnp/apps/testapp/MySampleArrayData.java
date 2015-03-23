package org.mdpnp.apps.testapp;

import ice.SampleArray;
import ice.SampleArrayDataReader;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

import org.mdpnp.rtiapi.data.InstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModel;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.SampleInfo;

public class MySampleArrayData implements InstanceModelListener<SampleArray, SampleArrayDataReader> {
    
    private final ObservableList<Series<Number, Number>> data = FXCollections.observableArrayList();
    private final Map<InstanceHandle_t, Series<Number, Number>> byHandle = new HashMap<InstanceHandle_t, Series<Number,Number>>(); 
    
    public MySampleArrayData(final SampleArrayInstanceModel model) {
        model.iterateAndAddListener(this);
    }
    
    public ObservableList<Series<Number,Number>> getData() {
        return data;
    }
    
    public Series<Number,Number> getSeries(InstanceHandle_t handle) {
        return byHandle.get(handle);
    }

    @Override
    public void instanceAlive(InstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
            SampleInfo sampleInfo) {
        if(!byHandle.containsKey(sampleInfo.instance_handle)) {
            Series<Number,Number> series = new Series<Number,Number>();
            byHandle.put(new InstanceHandle_t(sampleInfo.instance_handle), series);
            this.data.add(series);
        }

    }

    @Override
    public void instanceNotAlive(InstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray keyHolder,
            SampleInfo sampleInfo) {
        Series<Number,Number> series = byHandle.remove(sampleInfo.instance_handle);
        if(null != series) {
            this.data.remove(series);
        }
        
    }

    @Override
    public void instanceSample(InstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
            SampleInfo sampleInfo) {
        Series<Number,Number> series = byHandle.get(sampleInfo.instance_handle);
        if(null == series) {
            series = new Series<Number,Number>();
            byHandle.put(new InstanceHandle_t(sampleInfo.instance_handle), series);
            this.data.add(series);
        }
        final int size = data.values.userData.size();
        long base = sampleInfo.source_timestamp.sec * 1000L + sampleInfo.source_timestamp.nanosec / 1000000L;
        double period = 1000.0 / data.frequency;
        ObservableList<Data<Number,Number>> list = series.getData();
        for(int i = size-1; i >= 0; i--) {
            double tm = base - i * period;
            double val = data.values.userData.getFloat(size-i-1);
            Data<Number, Number> d;
//            if(list.size()>maxData) {
//                d = list.remove(0);
//                
//                d.setXValue(tm);
//                d.setYValue(val);
//            } else {
                d = new Data<Number,Number>(tm, val);
//            }
            list.add(d);
        }
    }
}
