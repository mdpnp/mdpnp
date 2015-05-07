package org.mdpnp.apps.fxbeans;

import javafx.beans.property.*;
import org.mdpnp.apps.fxbeans.AbstractFx;
import com.rti.dds.subscription.SampleInfo;
import org.mdpnp.apps.fxbeans.Updatable;

public class GlobalAlarmLimitObjectiveFx extends AbstractFx<ice.GlobalAlarmLimitObjective> implements Updatable<ice.GlobalAlarmLimitObjective> {
    public GlobalAlarmLimitObjectiveFx() {
    }

    private StringProperty metric_id;

    public StringProperty metric_idProperty() {
        if(null == this.metric_id) {
            this.metric_id = new SimpleStringProperty(this, "metric_id");
        }
        return metric_id;
    }

    public String getMetric_id() {
        return metric_idProperty().get();
    }

    public void setMetric_id(String metric_id) {
        this.metric_idProperty().set(metric_id);
    }

    private ObjectProperty<ice.LimitType> limit_type;

    public ObjectProperty<ice.LimitType> limit_typeProperty() {
        if(null == this.limit_type) {
            this.limit_type = new SimpleObjectProperty<ice.LimitType>(this, "limit_type");
        }
        return limit_type;
    }

    public ice.LimitType getLimit_type() {
        return limit_typeProperty().get();
    }

    public void setLimit_type(ice.LimitType limit_type) {
        this.limit_typeProperty().set(limit_type);
    }

    private StringProperty unit_identifier;

    public StringProperty unit_identifierProperty() {
        if(null == this.unit_identifier) {
            this.unit_identifier = new SimpleStringProperty(this, "unit_identifier");
        }
        return unit_identifier;
    }

    public String getUnit_identifier() {
        return unit_identifierProperty().get();
    }

    public void setUnit_identifier(String unit_identifier) {
        this.unit_identifierProperty().set(unit_identifier);
    }

    private FloatProperty value;

    public FloatProperty valueProperty() {
        if(null == this.value) {
            this.value = new SimpleFloatProperty(this, "value");
        }
        return value;
    }

    public float getValue() {
        return valueProperty().get();
    }

    public void setValue(float value) {
        this.valueProperty().set(value);
    }

    @Override
    public void update(ice.GlobalAlarmLimitObjective v, SampleInfo s) {
        setMetric_id(v.metric_id);
        setLimit_type(v.limit_type);
        setUnit_identifier(v.unit_identifier);
        setValue(v.value);
        super.update(v, s);
    }
}
