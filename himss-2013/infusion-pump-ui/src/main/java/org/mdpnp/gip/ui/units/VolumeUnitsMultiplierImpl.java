package org.mdpnp.gip.ui.units;

public class VolumeUnitsMultiplierImpl extends AbstractUnitsMultiplierImpl implements VolumeUnits {

	public VolumeUnitsMultiplierImpl(final String name, final double multiplier) {
		super(name, multiplier);
	}
	public VolumeUnitsMultiplierImpl(final String name, final String abbreviatedName, final double multiplier) {
		super(name, abbreviatedName, multiplier);
	}
}
