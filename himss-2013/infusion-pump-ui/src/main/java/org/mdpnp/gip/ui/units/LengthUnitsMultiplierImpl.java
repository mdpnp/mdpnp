package org.mdpnp.gip.ui.units;

public class LengthUnitsMultiplierImpl extends AbstractUnitsMultiplierImpl implements LengthUnits {
	public LengthUnitsMultiplierImpl(final String name, final double multiplier) {
		super(name, multiplier);
	}
	public LengthUnitsMultiplierImpl(final String name, final String abbreviatedName, final double multiplier) {
		super(name, abbreviatedName, multiplier);
	}
}
