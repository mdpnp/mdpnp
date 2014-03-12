package org.mdpnp.gip.ui.units;

/**
 * @author Jeff Plourde
 *
 */
public class VolumeUnitsFactory {
	public final static VolumeUnits milliliters = new VolumeUnitsMultiplierImpl("milliliters", "mL", 1000000000.0);
//	public final static VolumeUnits microliters = new VolumeUnitsMultiplierImpl("microliters", "mcl", 1000000.0);
	public final static VolumeUnits nanoliters = new VolumeUnitsMultiplierImpl("nanoliters", "nl", 1000.0);
	public final static VolumeUnits picoliters = new VolumeUnitsMultiplierImpl("picoliters", "pl", 1.0);
	
	public static final VolumeUnits[] volumes = new VolumeUnits[] {
		milliliters,
//		microliters,
		nanoliters,
		picoliters
	};
	
	public VolumeUnitsFactory() {
	}
	public VolumeUnits[] getVolumes() {
		return volumes;
	}
}
