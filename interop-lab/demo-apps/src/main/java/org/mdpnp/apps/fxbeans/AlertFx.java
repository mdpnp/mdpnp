package org.mdpnp.apps.fxbeans;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.rti.dds.subscription.SampleInfo;

public class AlertFx extends AbstractFx<ice.Alert> implements Updatable<ice.Alert> {
    
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
    
    public AlertFx() {
    }

    @Override
    public void update(ice.Alert a, SampleInfo s) {
        setUnique_device_identifier(a.unique_device_identifier);
        setIdentifier(a.identifier);
        setText(a.text);
        // The source_timestamp is convenient to key collection updates from so we fire it last
        super.update(a, s);
    }

}
