package org.mdpnp.gip.ui.units;

/**
 * @author Jeff Plourde
 *
 */
public class TimeUnitsFactory {
	public final static TimeUnits hours = new TimeUnitsMultiplierImpl("hours", "hr", 3600000.0);
	public final static TimeUnits minutes = new TimeUnitsMultiplierImpl("minutes", "min", 60000.0);
	public final static TimeUnits seconds = new TimeUnitsMultiplierImpl("seconds", "s", 1000.0);
	public final static TimeUnits milliseconds = new TimeUnitsMultiplierImpl("milliseconds", "ms", 1.0);
	
	public static final TimeUnits[] times = new TimeUnits[] {
		hours,
		minutes,
		seconds,
		milliseconds
	};
	
	public TimeUnitsFactory() {
	}
	public TimeUnits[] getTimes() {
		return times;
	}
}
