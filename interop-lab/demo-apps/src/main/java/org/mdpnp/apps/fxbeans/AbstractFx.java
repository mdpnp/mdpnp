package org.mdpnp.apps.fxbeans;

import java.util.Date;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.SampleInfo;

public abstract class AbstractFx<D extends Copyable> implements Updatable<D> {
    private InstanceHandle_t handle = InstanceHandle_t.HANDLE_NIL;
    
    private long someDelta;
    
    @Override
    public void update(D data, SampleInfo sampleInfo) {
        if(handle.is_nil()) {
            handle = new InstanceHandle_t(sampleInfo.instance_handle);
        } else if(!handle.equals(sampleInfo.instance_handle)) {
            // TODO this is weird
            handle.copy_from(sampleInfo.instance_handle);
        }
        long sourceLong=sampleInfo.source_timestamp.sec * 1000L + sampleInfo.source_timestamp.nanosec / 1000000L;
        long receptLong=sampleInfo.reception_timestamp.sec * 1000L + sampleInfo.reception_timestamp.nanosec / 1000000L;
        setSource_timestamp(new Date(sourceLong));
        setReception_timestamp(new Date(receptLong));
        long delta=(receptLong-sourceLong);
        setDelta(delta);
    }
    
    @Override
    public InstanceHandle_t getHandle() {
        return handle;
    }
    
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
    
    private ObjectProperty<Date> reception_timestamp;
    public Date getReception_timestamp() {
        return reception_timestampProperty().get();
    }
    public void setReception_timestamp(Date reception_timestamp) {
        reception_timestampProperty().set(reception_timestamp);
    }
    public ObjectProperty<Date> reception_timestampProperty() {
        if(null == reception_timestamp) {
            reception_timestamp = new SimpleObjectProperty<>(this, "reception_timestamp");
        }
        return reception_timestamp;
    }
    
    private SimpleLongProperty deltaProperty;
    public long getDelta() {
    	return deltaProperty().get();
    }
    public void setDelta(long delta) {
    	deltaProperty().set(delta);
    }
    public SimpleLongProperty deltaProperty() {
    	if(null == deltaProperty) {
    		deltaProperty = new SimpleLongProperty(this, "delta_property");
    	}
    	return deltaProperty;
    }
    
    
    public boolean equals(Object obj) {
        if(obj instanceof AbstractFx) {
            return handle.equals(((AbstractFx<?>)obj).handle);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return handle.hashCode();
    }

}
