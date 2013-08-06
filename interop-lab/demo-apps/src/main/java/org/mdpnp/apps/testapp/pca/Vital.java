package org.mdpnp.apps.testapp.pca;

import ice.Numeric;

import org.mdpnp.apps.testapp.Device;

public class Vital {
	private final String name;
	private final String units;
	final Integer numeric;
	private final Double advisory_minimum;
	private final Double advisory_maximum;
	private final Double critical_minimum;
	private final Double critical_maximum;
	
	private Device lastSource;
	private ice.Numeric value = (Numeric) ice.Numeric.create();
	
	public ice.Numeric getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
	public Integer getNumeric() {
		return numeric;
	}
	public Double getAdvisory_maximum() {
		return advisory_maximum;
	}
	public Double getAdvisory_minimum() {
		return advisory_minimum;
	}
	public Double getCritical_maximum() {
		return critical_maximum;
	}
	public Double getCritical_minimum() {
		return critical_minimum;
	}
	public String getUnits() {
		return units;
	}
	public Device getLastSource() {
		return lastSource;
	}
	public void set(ice.Numeric n, Device lastSource) {
	    this.lastSource = lastSource;
	    this.value.copy_from(n);
	}
	public boolean isSet() {
        return lastSource != null;
    }
	public void unset() {
	    this.lastSource = null;
	}
	public Vital (String name, String units, Integer numeric, Double advisory_minimum, Double advisory_maximum, Double critical_minimum, Double critical_maximum) {
		this.name = name;
		this.units = units;
		this.numeric = numeric;
		this.advisory_maximum = advisory_maximum;
		this.advisory_minimum = advisory_minimum;
		this.critical_maximum = critical_maximum;
		this.critical_minimum = critical_minimum;
	}
	
}