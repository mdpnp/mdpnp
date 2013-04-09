package org.mdpnp.gip.ui.units;

public class TimeUnitsMultiplierImpl extends AbstractUnitsMultiplierImpl implements TimeUnits {
	public TimeUnitsMultiplierImpl(final String name, final double multiplier) {
		super(name, multiplier);
	}
	public TimeUnitsMultiplierImpl(final String name, final String abbreviatedName, final double multiplier) {
		super(name, abbreviatedName, multiplier);
	}
}
