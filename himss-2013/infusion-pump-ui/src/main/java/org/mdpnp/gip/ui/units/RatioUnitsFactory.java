package org.mdpnp.gip.ui.units;

/**
 * @author Jeff Plourde
 *
 */
public class RatioUnitsFactory {
	public final static RatioUnits mLPerHour = new RatioUnitsImpl(VolumeUnitsFactory.milliliters, TimeUnitsFactory.hours);
	public final static RatioUnits mcgPerMinute = new RatioUnitsImpl(MassUnitsFactory.micrograms, TimeUnitsFactory.minutes);
	public final static RatioUnits mgPerKgPerHour = new RatioUnitsImpl(MassUnitsFactory.milligrams, MassUnitsFactory.kilograms, TimeUnitsFactory.hours);
	public final static RatioUnits mcgPerKgPerHour = new RatioUnitsImpl(MassUnitsFactory.micrograms, MassUnitsFactory.kilograms, TimeUnitsFactory.hours);
	
	public final static RatioUnits mgPerHour = new RatioUnitsImpl(MassUnitsFactory.milligrams, TimeUnitsFactory.hours);
	public final static RatioUnits gPerHour = new RatioUnitsImpl(MassUnitsFactory.grams, TimeUnitsFactory.hours);
	public final static RatioUnits mgPerMinute = new RatioUnitsImpl(MassUnitsFactory.milligrams, TimeUnitsFactory.minutes);
	
	public static final RatioUnits[] ratios = new RatioUnits[] {
		mLPerHour,
		mcgPerMinute,
		mgPerKgPerHour,
		mgPerHour,
		mgPerMinute,
		gPerHour,
		mcgPerKgPerHour
	};
	
	public RatioUnitsFactory() {
	}
	public RatioUnits[] getRatios() {
		return ratios;
	}
}
