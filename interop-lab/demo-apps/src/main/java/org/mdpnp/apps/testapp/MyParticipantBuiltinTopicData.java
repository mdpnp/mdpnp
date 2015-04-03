package org.mdpnp.apps.testapp;

import org.mdpnp.devices.TimeManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;

public class MyParticipantBuiltinTopicData {
    private StringProperty key;
    public String getKey() {
        return keyProperty().get();
    }
    public void setKey(String key) {
        keyProperty().set(key);
    }
    public StringProperty keyProperty() {
        if(null == key) {
            key = new SimpleStringProperty(this, "key");
        }
        return key;
    }
    
    private StringProperty name;
    public String getName() {
        return nameProperty().get();
    }
    public void setName(String name) {
        nameProperty().set(name);
    }
    public StringProperty nameProperty() {
        if(null == name) {
            name = new SimpleStringProperty(this, "name");
        }
        return name;
    }
    
    private StringProperty hostname;
    public String getHostname() {
        return hostnameProperty().get();
    }
    public void setHostname(String hostname) {
        hostnameProperty().set(hostname);
    }
    public StringProperty hostnameProperty() {
        if(null == hostname) {
            hostname = new SimpleStringProperty(this, "hostname");
        }
        return hostname;
    }
    
    public MyParticipantBuiltinTopicData(ParticipantBuiltinTopicData d) {
        update(d);
    }
    
    public void update(ParticipantBuiltinTopicData data) {
        keyProperty().set(data.key.toString());
        nameProperty().set(data.participant_name.name);
        hostnameProperty().set(TimeManager.getHostname(data));
    }
    
    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ParticipantBuiltinTopicData) {
            return this.key.equals(((ParticipantBuiltinTopicData)obj).key);
        } else {
            return false;
        }
    }
}
