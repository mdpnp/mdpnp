package org.mdpnp.apps.fxbeans;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;

import com.rti.dds.subscription.SampleInfo;

public class InfusionStatusFx extends AbstractFx<ice.InfusionStatus> implements Updatable<ice.InfusionStatus> {

	private StringProperty unique_device_identifier;

	public StringProperty unique_device_identifierProperty() {
		if (null == unique_device_identifier) {
			unique_device_identifier = new SimpleStringProperty(this, "unique_device_identifier", "");
		}
		return unique_device_identifier;
	}

	public String getUnique_device_identifier() {
		return unique_device_identifierProperty().get();
	}

	public void setUnique_device_identifier(String unique_device_identifier) {
		unique_device_identifierProperty().set(unique_device_identifier);
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

	public InfusionStatusFx() {
	}

	public void update(ice.InfusionStatus v, SampleInfo s) {
		BigDecimal fraction = new BigDecimal(v.infusion_fraction_complete);
		fraction = fraction.setScale(2, BigDecimal.ROUND_HALF_UP);

		setUnique_device_identifier(v.unique_device_identifier);
		setInfusionActive(v.infusionActive);
		setDrug_name(v.drug_name);
		setDrug_mass_mcg(v.drug_mass_mcg);
		setSolution_volume_ml(v.solution_volume_ml);
		setVolume_to_be_infused_ml(v.volume_to_be_infused_ml);
		setInfusion_duration_seconds(v.infusion_duration_seconds);
		setInfusion_fraction_complete(fraction.floatValue());
		super.update(v, s);
	}
}
