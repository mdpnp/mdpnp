package org.mdpnp.apps.fxbeans;

import java.math.BigDecimal;

import com.rti.dds.subscription.SampleInfo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BypassStatusFx extends AbstractFx<ice.BypassStatus> implements Updatable<ice.BypassStatus> {

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

	private BooleanProperty bypassActive = new SimpleBooleanProperty(this, "bypassActive", false);
	private DoubleProperty bypass_flow_lmin = new SimpleDoubleProperty(this, "bypass_flow_lmin", 0);
	private DoubleProperty bypass_speed_rpm = new SimpleDoubleProperty(this, "bypass_speed_rpm", 0);
	private DoubleProperty blood_temp_celsius = new SimpleDoubleProperty(this, "blood_temp_celsius", 0);
	private DoubleProperty blood_press_mmhg = new SimpleDoubleProperty(this, "blood_press_mmhg", 0);
	private DoubleProperty volume_bypassed_ml = new SimpleDoubleProperty(this, "volume_bypassed_ml", 0);
	private IntegerProperty bypass_duration_seconds = new SimpleIntegerProperty(this, "bypass_duration_seconds", 0);

	public final BooleanProperty bypassActiveProperty() {
		return this.bypassActive;
	}

	public final boolean isBypassActive() {
		return this.bypassActiveProperty().get();
	}

	public final void setBypassActive(final boolean bypassActive) {
		this.bypassActiveProperty().set(bypassActive);
	}

	public final DoubleProperty bypass_flow_lminProperty() {
		return this.bypass_flow_lmin;
	}

	public final double getBypass_flow_lmin() {
		return this.bypass_flow_lminProperty().get();
	}

	public final void setBypass_flow_lmin(final double bypass_flow_lmin) {
		this.bypass_flow_lminProperty().set(bypass_flow_lmin);
	}

	public final DoubleProperty bypass_speed_rpmProperty() {
		return this.bypass_speed_rpm;
	}

	public final double getBypass_speed_rpm() {
		return this.bypass_speed_rpmProperty().get();
	}

	public final void setBypass_speed_rpm(final double bypass_speed_rpm) {
		this.bypass_speed_rpmProperty().set(bypass_speed_rpm);
	}

	public final DoubleProperty blood_temp_celsiusProperty() {
		return this.blood_temp_celsius;
	}

	public final double getBlood_temp_celsius() {
		return this.blood_temp_celsiusProperty().get();
	}

	public final void setBlood_temp_celsius(final double blood_temp_celsius) {
		this.blood_temp_celsiusProperty().set(blood_temp_celsius);
	}

	public final DoubleProperty blood_press_mmhgProperty() {
		return this.blood_press_mmhg;
	}

	public final double getBlood_press_mmhg() {
		return this.blood_press_mmhgProperty().get();
	}

	public final void setBlood_press_mmhg(final double blood_press_mmhg) {
		this.blood_press_mmhgProperty().set(blood_press_mmhg);
	}

	public final DoubleProperty volume_bypassed_mlProperty() {
		return this.volume_bypassed_ml;
	}

	public final double getVolume_bypassed_ml() {
		return this.volume_bypassed_mlProperty().get();
	}

	public final void setVolume_bypassed_ml(final double volume_bypassed_ml) {
		this.volume_bypassed_mlProperty().set(volume_bypassed_ml);
	}

	public final IntegerProperty bypass_duration_secondsProperty() {
		return this.bypass_duration_seconds;
	}

	public final int getBypass_duration_seconds() {
		return this.bypass_duration_secondsProperty().get();
	}

	public final void setBypass_duration_seconds(final int bypass_duration_seconds) {
		this.bypass_duration_secondsProperty().set(bypass_duration_seconds);
	}

	public BypassStatusFx() {
	}

	public void update(ice.BypassStatus v, SampleInfo s) {
		BigDecimal round;

		setUnique_device_identifier(v.unique_device_identifier);
		setBypassActive(v.bypassActive);
		
		round = new BigDecimal(v.bypass_flow_lmin);
		round = round.setScale(2, BigDecimal.ROUND_HALF_UP);
		v.bypass_flow_lmin = round.doubleValue();
		setBypass_flow_lmin(v.bypass_flow_lmin);
		
		round = new BigDecimal(v.bypass_speed_rpm);
		round = round.setScale(2, BigDecimal.ROUND_HALF_UP);
		v.bypass_speed_rpm = round.doubleValue();
		setBypass_speed_rpm(v.bypass_speed_rpm);
		setBlood_temp_celsius(v.blood_temp_celsius);
		setBlood_press_mmhg(v.blood_press_mmhg);
		
		round = new BigDecimal(v.volume_bypassed_ml);
		round = round.setScale(2, BigDecimal.ROUND_HALF_UP);
		v.volume_bypassed_ml = round.doubleValue();
		setVolume_bypassed_ml(v.volume_bypassed_ml);
		setBypass_duration_seconds(v.bypass_duration_seconds);
		super.update(v, s);
	}
}
