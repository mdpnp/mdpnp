package org.mdpnp.apps.testapp;

import java.util.Date;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.rti.dds.subscription.SampleInfo;

public class MyAlert {
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
    
    private StringProperty ice_id;
    public String getIce_id() {
        return ice_idProperty().get();
    }
    public void setIce_id(String ice_id) {
        ice_idProperty().set(ice_id);
    }
    public StringProperty ice_idProperty() {
        if(null == ice_id) {
            ice_id = new SimpleStringProperty(this, "ice_id");
        }
        return ice_id;
    }
    
    private StringProperty identifier;
    public String getIdentifier() {
        return identifierProperty().get();
    }
    public void setIdentifier(String identifier) {
        identifierProperty().set(identifier);
    }
    public StringProperty identifierProperty() {
        if(null == identifier) {
            identifier = new SimpleStringProperty(this, "identifier");
        }
        return identifier;
    }
    
    private StringProperty text;
    public String getText() {
        return textProperty().get();
    }
    public void setText(String text) {
        textProperty().set(text);
    }
    public StringProperty textProperty() {
        if(null == text) {
            text = new SimpleStringProperty(this, "text");
        }
        return text;
    }
    
    private final String key;
    
    public final String key() {
        return this.key;
    }
    
    public static final String key(ice.Alert a) {
        return (a.ice_id+a.identifier).intern();
    }
    
    public MyAlert(ice.Alert a, SampleInfo s) {
        key = key(a);
        update(a, s);
    }
    
    private Date _source_timestamp = new Date();
    public void update(ice.Alert a, SampleInfo s) {
        _source_timestamp.setTime(s.source_timestamp.sec * 1000L + s.source_timestamp.nanosec / 1000000L);
        setSource_timestamp(_source_timestamp);
        setIce_id(a.ice_id);
        setIdentifier(a.identifier);
        setText(a.text);
    }
    
    @Override
    public int hashCode() {
        return key.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MyAlert) {
            return key.equals(((MyAlert)obj).key);
        } else {
            return false;
        }
    }    
}
