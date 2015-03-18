package org.mdpnp.apps.testapp.alarm;

import ice.Alert;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.mdpnp.apps.testapp.MyAlert;

import com.rti.dds.subscription.SampleInfo;

public class HistoricAlarm extends MyAlert {
    private StringProperty type;
    public String getType() {
        return typeProperty().get();
    }
    public void setType(String type) {
        typeProperty().set(type);
    }
    public StringProperty typeProperty() {
        if(null == type) {
            type = new SimpleStringProperty(this, "type");
        }
        return type;
    }

    public HistoricAlarm(ice.Alert a, SampleInfo s, String type) {
        super(a, s);
        typeProperty().set(type);
    }
    public void update(Alert a, SampleInfo s, String type) {
        super.update(a, s);
        typeProperty().set(type);
    }
}