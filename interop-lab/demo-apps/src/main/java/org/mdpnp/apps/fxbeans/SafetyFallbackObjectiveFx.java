package org.mdpnp.apps.fxbeans;

import java.util.Date;

import com.rti.dds.subscription.SampleInfo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SafetyFallbackObjectiveFx extends AbstractFx<ice.SafetyFallbackObjective> implements Updatable<ice.SafetyFallbackObjective>{
	public SafetyFallbackObjectiveFx() {
    }

	private StringProperty identifier;

    public StringProperty identifierProperty() {
        if(null == this.identifier) {
            this.identifier = new SimpleStringProperty(this, "identifier");
        }
        return identifier;
    }

    public String getIdentifier() {
        return identifierProperty().get();
    }

    public void setIdentifier(String identifier) {
        this.identifierProperty().set(identifier);
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

    private ObjectProperty<ice.SafetyFallbackType> safety_fallback_type;

    public ObjectProperty<ice.SafetyFallbackType> safety_fallback_typeProperty() {
        if(null == this.safety_fallback_type) {
            this.safety_fallback_type = new SimpleObjectProperty<ice.SafetyFallbackType>(this, "safety_fallback_type");
        }
        return safety_fallback_type;
    }

    public ice.SafetyFallbackType getSafety_fallback_type() {
        return safety_fallback_typeProperty().get();
    }

    public void setSafety_fallback_type(ice.SafetyFallbackType safety_fallback_type) {
        this.safety_fallback_typeProperty().set(safety_fallback_type);
    }
    
    private StringProperty message;

    public StringProperty messageProperty() {
        if(null == this.message) {
            this.message = new SimpleStringProperty(this, "message");
        }
        return message;
    }

    public String getMessage() {
        return messageProperty().get();
    }

    public void setMessage(String message) {
        this.messageProperty().set(message);
    }

    @Override
    public void update(ice.SafetyFallbackObjective v, SampleInfo s) {
    	setIdentifier(v.identifier);
    	setUnique_device_identifier(v.unique_device_identifier);
    	setPresentation_time(new Date(v.presentation_time.sec * 1000L + v.presentation_time.nanosec / 1000000L));
        setSafety_fallback_type(v.safety_fallback_type);
        setMessage(v.message);
        super.update(v, s);
    }
}