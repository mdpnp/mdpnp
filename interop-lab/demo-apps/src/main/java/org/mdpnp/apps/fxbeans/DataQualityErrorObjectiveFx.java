package org.mdpnp.apps.fxbeans;

import java.util.Date;

import com.rti.dds.subscription.SampleInfo;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DataQualityErrorObjectiveFx extends AbstractFx<ice.DataQualityErrorObjective> implements Updatable<ice.DataQualityErrorObjective>{
	public DataQualityErrorObjectiveFx() {
    }
	
    private StringProperty unique_device_identifier;

    public StringProperty unique_device_identifierProperty() {
        if(null == this.unique_device_identifier) {
            this.unique_device_identifier = new SimpleStringProperty(this, "unique_device_identifier");
        }
        return unique_device_identifier;
    }

    public String getUnique_device_identifier() {
        return unique_device_identifierProperty().get();
    }

    public void setUnique_device_identifier(String unique_device_identifier) {
        this.unique_device_identifierProperty().set(unique_device_identifier);
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
    
    private ObjectProperty<Date> presentation_time;
    public final ObjectProperty<Date> presentation_timeProperty() {
        if(null == presentation_time) {
            presentation_time = new SimpleObjectProperty<>(this, "presentation_time");
        }
        return this.presentation_time;
    }
    public final java.util.Date getPresentation_time() {
        return this.presentation_timeProperty().get();
    }
    public final void setPresentation_time(final java.util.Date presentation_time) {
        this.presentation_timeProperty().set(presentation_time);
    }

    private ObjectProperty<ice.DataQualityAttributeType> data_quality_attribute_type;

    public ObjectProperty<ice.DataQualityAttributeType> data_quality_attribute_typeProperty() {
        if(null == this.data_quality_attribute_type) {
            this.data_quality_attribute_type = new SimpleObjectProperty<ice.DataQualityAttributeType>(this, "data_quality_attribute_type");
        }
        return data_quality_attribute_type;
    }

    public ice.DataQualityAttributeType getData_quality_attribute_type() {
        return data_quality_attribute_typeProperty().get();
    }

    public void setData_quality_attribute_type(ice.DataQualityAttributeType data_quality_attribute_type) {
        this.data_quality_attribute_typeProperty().set(data_quality_attribute_type);
    }
    
    private FloatProperty value;
    public FloatProperty valueProperty() {
        if(null == value) {
            value = new SimpleFloatProperty(this, "value");
        }
        return value;
    }
    public float getValue() {
        return valueProperty().get();
    }
    public void setValue(float f) {
        valueProperty().set(f);
    }

    @Override
    public void update(ice.DataQualityErrorObjective v, SampleInfo s) {
    	setUnique_device_identifier(v.unique_device_identifier);
    	setMetric_id(v.metric_id);
    	setPresentation_time(new Date(v.presentation_time.sec * 1000L + v.presentation_time.nanosec / 1000000L));
        setData_quality_attribute_type(v.data_quality_attribute_type);
        setValue(v.value);
        super.update(v, s);
    }
}