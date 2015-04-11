package org.mdpnp.apps.testapp.validate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.mdpnp.apps.fxbeans.NumericFx;

public class Validation {
//    private final String key;
//    private final int hashCode;
//    private final ReadOnlyStringProperty unique_device_identifier;
//    private final ReadOnlyStringProperty metric_id;
//    private final ReadOnlyIntegerProperty instance_id;
//    private final ReadOnlyStringProperty unit_id;
    private final ReadOnlyObjectProperty<NumericFx> numeric;
    private final BooleanProperty validated;
    
    public Validation(NumericFx numeric) {
        this.numeric = new SimpleObjectProperty<NumericFx>(this, "numeric", numeric);
        this.validated = new SimpleBooleanProperty(this, "validated", false);
    }
    
//    public Validation(final String unique_device_identifier, 
//                      final String metric_id,
//                      final int instance_id,
//                      final String unit_id) {
//        this.unique_device_identifier = new SimpleStringProperty(this, "unique_device_identifier", unique_device_identifier);
//        this.metric_id = new SimpleStringProperty(this, "metric_id", metric_id);
//        this.instance_id = new SimpleIntegerProperty(this, "instance_id", instance_id);
//        this.unit_id = new SimpleStringProperty(this, "unit_id", metric_id);
//        
//        this.key = unique_device_identifier + "-" + metric_id + "-" + instance_id + "-" + unit_id;
//        this.hashCode = this.key.hashCode();
//    }

//    public ReadOnlyStringProperty unique_device_identifierProperty() {
//        return this.unique_device_identifier;
//    }
//
//    public java.lang.String getUnique_device_identifier() {
//        return this.unique_device_identifierProperty().get();
//    }
//
//    public ReadOnlyStringProperty metric_idProperty() {
//        return this.metric_id;
//    }
//
//    public java.lang.String getMetric_id() {
//        return this.metric_idProperty().get();
//    }
//
//    public ReadOnlyIntegerProperty instance_idProperty() {
//        return this.instance_id;
//    }
//
//    public int getInstance_id() {
//        return this.instance_idProperty().get();
//    }
//
//    public ReadOnlyStringProperty unit_idProperty() {
//        return this.unit_id;
//    }
//
//    public java.lang.String getUnit_id() {
//        return this.unit_idProperty().get();
//    }

    public BooleanProperty validatedProperty() {
        return this.validated;
    }

    public boolean isValidated() {
        return this.validatedProperty().get();
    }

    public void setValidated(final boolean validated) {
        this.validatedProperty().set(validated);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Validation) {
            return this.numeric.equals(((Validation)obj).numeric);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return numeric.hashCode();
    }

    public ReadOnlyObjectProperty<NumericFx> numericProperty() {
        return this.numeric;
    }

    public org.mdpnp.apps.fxbeans.NumericFx getNumeric() {
        return this.numericProperty().get();
    }
    
    public ReadOnlyStringProperty unique_device_identifierProperty() {
        return getNumeric().unique_device_identifierProperty();
    }
    
    public ReadOnlyStringProperty metric_idProperty() {
        return getNumeric().metric_idProperty();
    }
    
    public ReadOnlyIntegerProperty instance_idProperty() {
        return getNumeric().instance_idProperty();
    }
    
    public ReadOnlyFloatProperty valueProperty() {
        return getNumeric().valueProperty();
    }
    
}
