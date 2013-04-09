package org.mdpnp.gip.ui.units;

public abstract class AbstractUnitsMultiplierImpl implements Units {
	private final String name;
	private final String abbreviatedName;
	private final double multiplier;
	
	public AbstractUnitsMultiplierImpl(final String name, final double multiplier) {
		this(name, name, multiplier);
	}
	
	public AbstractUnitsMultiplierImpl(final String name, final String abbreviatedName, final double multiplier) {
		this.name = name;
		this.abbreviatedName = abbreviatedName;
		this.multiplier = multiplier;
	}
	@Override
	public Double from(Double v) {
		return null == v ? null : (v / multiplier);
	}
	@Override
	public Double to(Double v) {
		return null == v ? null : (v * multiplier);
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return getName();
	}
	@Override
	public String getAbbreviatedName() {
		return abbreviatedName;
	}
}
