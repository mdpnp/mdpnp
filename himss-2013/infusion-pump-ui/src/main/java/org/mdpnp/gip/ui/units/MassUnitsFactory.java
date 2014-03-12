package org.mdpnp.gip.ui.units;


/**
 * @author Jeff Plourde
 *
 */
public class MassUnitsFactory {
	public final static MassUnits pounds = new MassUnitsMultiplierImpl("pounds", "lbs"     ,  2204620000000000.0);
	public final static MassUnits kilograms = new MassUnitsMultiplierImpl("kilograms", "kg", 1000000000000000.0);
	public final static MassUnits stone = new MassUnitsMultiplierImpl("stone", "st",         15747300000000000.0);
	public final static MassUnits grams = new MassUnitsMultiplierImpl("grams", "g", 1000000000000.0);
	public final static MassUnits milligrams = new MassUnitsMultiplierImpl("milligrams", "mg", 1000000000.0);
	public final static MassUnits micrograms = new MassUnitsMultiplierImpl("micrograms", "mcg", 1000000.0);
	public final static MassUnits nanograms = new MassUnitsMultiplierImpl("nanograms", "ng", 1000.0);
	public final static MassUnits picograms = new MassUnitsMultiplierImpl("picograms", "pg", 1.0);
	
	public static final MassUnits[] masses = new MassUnits[] {
		pounds,
		kilograms,
		grams,
		milligrams,
		micrograms,
		nanograms,
		picograms
	};
	
	public MassUnitsFactory() {
	}
	public MassUnits[] getMasses() {
		return masses;
	}
}
