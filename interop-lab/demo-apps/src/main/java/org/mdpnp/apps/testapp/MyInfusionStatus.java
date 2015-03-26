package org.mdpnp.apps.testapp;

import java.util.Date;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.SampleInfo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MyInfusionStatus {
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
    public StringProperty ice_idProperty() {
        if(null == ice_id) {
            ice_id = new SimpleStringProperty(this, "ice_id", "");
        }
        return ice_id;
    }
    public String getIce_id() {
        return ice_idProperty().get();
    }
    public void setIce_id(String ice_id) {
        ice_idProperty().set(ice_id);
    }
    
    private BooleanProperty infusionActive = new SimpleBooleanProperty(this, "infusionActive", false);
    private StringProperty drug_name = new SimpleStringProperty(this, "drug_name", "");
    private IntegerProperty drug_mass_mcg = new SimpleIntegerProperty(this, "drug_mass_mcg", 0);
    private IntegerProperty solution_volume_ml = new SimpleIntegerProperty(this, "solution_volume_ml", 0);
    private IntegerProperty volume_to_be_infused_ml = new SimpleIntegerProperty(this, "volume_to_be_infused_ml", 0);
    private IntegerProperty infusion_duration_seconds = new SimpleIntegerProperty(this, "infusion_duration_seconds", 0);
    private FloatProperty infusion_fraction_complete = new SimpleFloatProperty(this, "infusion_fraction_complete", 0);
    public final BooleanProperty infusionActiveProperty() {
        return this.infusionActive;
    }
    public final boolean isInfusionActive() {
        return this.infusionActiveProperty().get();
    }
    public final void setInfusionActive(final boolean infusionActive) {
        this.infusionActiveProperty().set(infusionActive);
    }
    public final StringProperty drug_nameProperty() {
        return this.drug_name;
    }
    public final java.lang.String getDrug_name() {
        return this.drug_nameProperty().get();
    }
    public final void setDrug_name(final java.lang.String drug_name) {
        this.drug_nameProperty().set(drug_name);
    }
    public final IntegerProperty drug_mass_mcgProperty() {
        return this.drug_mass_mcg;
    }
    public final int getDrug_mass_mcg() {
        return this.drug_mass_mcgProperty().get();
    }
    public final void setDrug_mass_mcg(final int drug_mass_mcg) {
        this.drug_mass_mcgProperty().set(drug_mass_mcg);
    }
    public final IntegerProperty solution_volume_mlProperty() {
        return this.solution_volume_ml;
    }
    public final int getSolution_volume_ml() {
        return this.solution_volume_mlProperty().get();
    }
    public final void setSolution_volume_ml(final int solution_volume_ml) {
        this.solution_volume_mlProperty().set(solution_volume_ml);
    }
    public final IntegerProperty volume_to_be_infused_mlProperty() {
        return this.volume_to_be_infused_ml;
    }
    public final int getVolume_to_be_infused_ml() {
        return this.volume_to_be_infused_mlProperty().get();
    }
    public final void setVolume_to_be_infused_ml(final int volume_to_be_infused_ml) {
        this.volume_to_be_infused_mlProperty().set(volume_to_be_infused_ml);
    }
    public final IntegerProperty infusion_duration_secondsProperty() {
        return this.infusion_duration_seconds;
    }
    public final int getInfusion_duration_seconds() {
        return this.infusion_duration_secondsProperty().get();
    }
    public final void setInfusion_duration_seconds(final int infusion_duration_seconds) {
        this.infusion_duration_secondsProperty().set(infusion_duration_seconds);
    }
    public final FloatProperty infusion_fraction_completeProperty() {
        return this.infusion_fraction_complete;
    }
    public final float getInfusion_fraction_complete() {
        return this.infusion_fraction_completeProperty().get();
    }
    public final void setInfusion_fraction_complete(final float infusion_fraction_complete) {
        this.infusion_fraction_completeProperty().set(infusion_fraction_complete);
    }
    
    private InstanceHandle_t key; 
    public MyInfusionStatus(ice.InfusionStatus v, SampleInfo s) {
        this.key = new InstanceHandle_t(s.instance_handle);
        update(v, s);
    }
    
    
    private Date _source_timestamp = new Date();
    
    
    public void update(ice.InfusionStatus v, SampleInfo s) {
        _source_timestamp.setTime(s.source_timestamp.sec * 1000L + s.source_timestamp.nanosec / 1000000L);
        setSource_timestamp(_source_timestamp);
        setIce_id(v.ice_id);
        setInfusionActive(v.infusionActive);
        setDrug_name(v.drug_name);
        setDrug_mass_mcg(v.drug_mass_mcg);
        setSolution_volume_ml(v.solution_volume_ml);
        setVolume_to_be_infused_ml(v.volume_to_be_infused_ml);
        setInfusion_duration_seconds(v.infusion_duration_seconds);
        setInfusion_fraction_complete(v.infusion_fraction_complete);
    }
    
    public InstanceHandle_t getHandle() {
        return key;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MyInfusionStatus) {
            return key.equals(((MyInfusionStatus)obj).key);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return key.hashCode();
    }
    
}
