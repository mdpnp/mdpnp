package org.mdpnp.gip.ui.units;

/**
 * @author Jeff Plourde
 *
 */
public class LengthUnitsFactory {
	public final static LengthUnits millimeters = new LengthUnitsMultiplierImpl("millimeters","mm", 1.0);
	public final static LengthUnits centimeters = new LengthUnitsMultiplierImpl("centimeters","cm", 10.0);
	public final static LengthUnits meters = new LengthUnitsMultiplierImpl("meters", "m",1000.0);
	public final static LengthUnits inches = new LengthUnitsMultiplierImpl("inches", "in.", 25.4);
	
	
	public static final LengthUnits[] lengths = new LengthUnits[] {
		millimeters,
		centimeters,
		meters,
		inches
	};
	
	public LengthUnitsFactory() {
	}
	public LengthUnits[] getLengths() {
		return lengths;
	}
}
