package org.mdpnp.gip.ui.units;

public class MassUnitsMultiplierImpl extends AbstractUnitsMultiplierImpl implements MassUnits {
	public MassUnitsMultiplierImpl(final String name, final double multiplier) {
		super(name, multiplier);
	}
	public MassUnitsMultiplierImpl(final String name, final String abbreviatedName, final double multiplier) {
		super(name, abbreviatedName, multiplier);
	}
}
