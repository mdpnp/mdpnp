package org.mdpnp.apps.fxbeans;

import java.util.Date;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.rti.dds.subscription.SampleInfo;

public class SampleArrayFx extends AbstractFx<ice.SampleArray> {
    
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
    
    
    private ObjectProperty<Number[]> values;
    public ObjectProperty<Number[]> valuesProperty() {
        if(null == values) {
            values = new SimpleObjectProperty<Number[]>(this, "values", new Number[0]);
        }
        return values;
    }
    public Number[] getValues() {
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
    
    private ObjectProperty<Date> presentation_time;
    public ObjectProperty<Date> presentation_timeProperty() {
        if(null == presentation_time) {
            presentation_time = new SimpleObjectProperty<>(this, "presentation_time");
        }
        return presentation_time;
    }
    public java.util.Date getPresentation_time() {
        return this.presentation_timeProperty().get();
    }
    public void setPresentation_time(final java.util.Date presentation_time) {
        this.presentation_timeProperty().set(presentation_time);
    }
    
    public SampleArrayFx() {
    }
    
    private FloatProperty sqi_accuracy;
    public final FloatProperty sqi_accuracyProperty() {
    	if(null == sqi_accuracy) {
    		sqi_accuracy=new SimpleFloatProperty(this, "sqi_accuracy");
    	}
    	return this.sqi_accuracy;
    }
    public final float getSQI_accuracy() {
    	return this.sqi_accuracyProperty().get();
    }
    public final void setSQI_accuracy(final float accuracy) {
    	this.sqi_accuracyProperty().set(accuracy);
    }
    
    private FloatProperty sqi_accuracy_duration;
    public final FloatProperty sqi_accuracy_durationProperty() {
    	if(null == sqi_accuracy_duration) {
    		sqi_accuracy_duration=new SimpleFloatProperty(this, "sqi_accuracy_duration");
    	}
    	return this.sqi_accuracy_duration;
    }
    public final float getSQI_accuracy_duration() {
    	return this.sqi_accuracy_durationProperty().get();
    }
    public final void setSQI_accuracy_duration(final float accuracy_duration) {
    	this.sqi_accuracy_durationProperty().set(accuracy_duration);
    }
    
    private FloatProperty sqi_completeness;
    public final FloatProperty sqi_completenessProperty() {
    	if(null == sqi_completeness) {
    		sqi_completeness=new SimpleFloatProperty(this, "sqi_completeness");
    	}
    	return this.sqi_completeness;
    }
    public final float getSQI_completeness() {
    	return this.sqi_completenessProperty().get();
    }
    public final void setSQI_completeness(final float completeness) {
    	this.sqi_completenessProperty().set(completeness);
    }
    
    private FloatProperty sqi_precision;
    public final FloatProperty sqi_precisionProperty() {
    	if(null == sqi_precision) {
    		sqi_precision=new SimpleFloatProperty(this, "sqi_precision");
    	}
    	return this.sqi_precision;
    }
    public final float getSQI_precision() {
    	return this.sqi_precisionProperty().get();
    }
    public final void setSQI_precision(final float precision) {
    	this.sqi_precisionProperty().set(precision);
    }
    
    private FloatProperty sqi_frequency;
    public final FloatProperty sqi_frequencyProperty() {
    	if(null == sqi_frequency) {
    		sqi_frequency=new SimpleFloatProperty(this, "sqi_frequency");
    	}
    	return this.sqi_frequency;
    }
    public final float getSQI_frequency() {
    	return this.sqi_frequencyProperty().get();
    }
    public final void setSQI_frequency(final float frequency) {
    	this.sqi_frequencyProperty().set(frequency);
    }
    
    public void update(ice.SampleArray v, SampleInfo s) {
        
        setUnique_device_identifier(v.unique_device_identifier);
        setMetric_id(v.metric_id);
        setVendor_metric_id(v.vendor_metric_id);
        setInstance_id(v.instance_id);
        setUnit_id(v.unit_id);
        setFrequency(v.frequency);
        
        setSQI_accuracy(v.sqi.accuracy);
        setSQI_accuracy_duration(v.sqi.accuracy_duration);
        setSQI_completeness(v.sqi.completeness);
        setSQI_frequency(v.sqi.frequency);
        setSQI_precision(v.sqi.precision);
        
        
        Number[] values = new Number[v.values.userData.size()];
        for(int i = 0; i < values.length; i++) {
            values[i] = v.values.userData.getFloat(i);
        }
        valuesProperty().set(values);
        setDevice_time(new Date(v.device_time.sec * 1000L + v.device_time.nanosec / 1000000L));
        setPresentation_time(new Date(v.presentation_time.sec * 1000L + v.presentation_time.nanosec / 1000000L));
        super.update(v, s);
    }

}
