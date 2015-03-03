package org.mdpnp.apps.testapp;

import java.util.Date;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.SampleInfo;

public class MySampleArray {
    private ObjectProperty<Date> source_timestamp;
    public Date getSource_timestamp() {
        return source_timestampProperty().get();
    }
    public void setSource_timestamp(Date source_timestamp) {
        source_timestampProperty().set(source_timestamp);
    }
    public ObjectProperty<Date> source_timestampProperty() {
        if(null == source_timestamp) {
            source_timestamp = new SimpleObjectProperty<>(this, "source_timestamp");
        }
        return source_timestamp;
    }
    
    private StringProperty unique_device_identifier;
    public String getUnique_device_identifier() {
        return unique_device_identifierProperty().get();
    }
    public void setUnique_device_identifier(String unique_device_identifier) {
        unique_device_identifierProperty().set(unique_device_identifier);
    }
    public StringProperty unique_device_identifierProperty() {
        if(null == unique_device_identifier) {
            unique_device_identifier = new SimpleStringProperty(this, "unique_device_identifier");
        }
        return unique_device_identifier;
    }
    
    private StringProperty metric_id;
    public String getMetric_id() {
        return metric_idProperty().get();
    }
    public void setMetric_id(String metric_id) {
        this.metric_idProperty().set(metric_id);
    }
    public StringProperty metric_idProperty() {
        if(null == metric_id) {
            metric_id = new SimpleStringProperty(this, "metric_id");
        }
        return metric_id;
    }
    
    private StringProperty vendor_metric_id;
    public String getVendor_metric_id() {
        return vendor_metric_idProperty().get();
    }
    public void setVendor_metric_id(String vendor_metric_id) {
        vendor_metric_idProperty().set(vendor_metric_id);
    }
    public StringProperty vendor_metric_idProperty() {
        if(null == vendor_metric_id) {
            vendor_metric_id = new SimpleStringProperty(this, "vendor_metric_id");
        }
        return vendor_metric_id;
    }
    
    private IntegerProperty instance_id;
    public int getInstance_id() {
        return instance_idProperty().get();
    }
    public void setInstance_id(int instance_id) {
        instance_idProperty().set(instance_id);
    }
    public IntegerProperty instance_idProperty() {
        if(null == instance_id) {
            instance_id = new SimpleIntegerProperty(this, "instance_id");
        }
        return instance_id;
    }
    
    private StringProperty unit_id;
    public String getUnit_id() {
        return unit_idProperty().get();
    }
    public void setUnit_id(String unit_id) {
        unit_idProperty().set(unit_id);
    }
    public StringProperty unit_idProperty() {
        if(null == unit_id) {
            unit_id = new SimpleStringProperty(this, "unit_id");
        }
        return unit_id;
    }
    
    private LongProperty frequency;
    public LongProperty frequencyProperty() {
        if(null == frequency) {
            frequency = new SimpleLongProperty(this, "frequency");
        }
        return frequency;
    }
    public long getFrequency() {
        return frequencyProperty().get();
    }
    public void setFrequency(long frequency) {
        frequencyProperty().set(frequency);
    }
    
    
    private ListProperty<Float> values;
    public ListProperty<Float> valuesProperty() {
        if(null == values) {
            values = new SimpleListProperty<Float>(this, "values");
        }
        return values;
    }
    public ObservableList<Float> getValues() {
        return valuesProperty().get();
    }
    
    private ObjectProperty<Date> device_time;
    public Date getDevice_time() {
        return device_timeProperty().get();
    }
    public void setDevice_time(Date device_time) {
        device_timeProperty().set(device_time);
    }
    public ObjectProperty<Date> device_timeProperty() {
        if(null == device_time) {
            device_time = new SimpleObjectProperty<>(this, "device_time");
        }
        return device_time;
    }
    
    private final InstanceHandle_t handle;
    
    public InstanceHandle_t getHandle() {
        return handle;
    }
    
    public MySampleArray(ice.SampleArray v, SampleInfo s) {
        this.handle = new InstanceHandle_t(s.instance_handle);
        update(v, s);
    }
    
    private Date _source_timestamp = new Date();
    private Date _device_time = new Date();
    public void update(ice.SampleArray v, SampleInfo s) {
        _source_timestamp.setTime(s.source_timestamp.sec * 1000L + s.source_timestamp.nanosec / 1000000L);
        setSource_timestamp(_source_timestamp);
        setUnique_device_identifier(v.unique_device_identifier);
        setMetric_id(v.metric_id);
        setVendor_metric_id(v.vendor_metric_id);
        setInstance_id(v.instance_id);
        setUnit_id(v.unit_id);
        setFrequency(v.frequency);
//        valuesProperty().clear();
//        valuesProperty().addAll(v.values.userData);
        _device_time.setTime(v.device_time.sec * 1000L + v.device_time.nanosec / 1000000L);
        setDevice_time(_device_time);
    }
    
    @Override
    public int hashCode() {
        return handle.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MySampleArray) {
            return handle.equals(((MySampleArray)obj).handle);
        } else {
            return false;
        }
    }
}
