package org.mdpnp.devices.philips.intellivue.data;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum MetricCategory implements OrdinalEnum.IntType {
	/**
	 * not specified
	 */
	MCAT_UNSPEC(0),
	/**
	 * automatic measurement
	 */
	AUTO_MEASUREMENT(1),
	/**
	 * manual measurement
	 */
	MANUAL_MEASUREMENT(2),
	/**
	 * automatic setting
	 */
	AUTO_SETTING(3),
	/**
	 * manual setting
	 */
	MANUAL_SETTING(4),
	/**
	 * automatic calculation, e.g. differential temperature
	 */
	AUTO_CALCULATION(5),
	/**
	 * manual calculation
	 */
	MANUAL_CALCULATION(6),
	/**
	 * this measurement may change its category during
     * operation or may be used in various modes.
	 */
	MULTI_DYNAMIC_CAPABILITIES(50),
	/**
	 * measurement is automatically adjusted for patient temperature
	 */
	AUTO_ADJUST_PAT_TEMP(128),
	/**
	 * measurement manually adjusted for patient temperature
	 */
	MANUAL_ADJUST_PAT_TEMP(129),
	/**
	 * this is not a measurement, but an alarm limit setting
	 */
	AUTO_ALARM_LIMIT_SETTING(130);
	
	private final int x;
	
	private static final Map<Integer, MetricCategory> map = OrdinalEnum.buildInt(MetricCategory.class);
		
	private MetricCategory(int x) {
	    this.x = x;
    }
	
	public static final MetricCategory valueOf(int x) {
	    return map.get(x);
	}
	
	public int asInt() {
	    return x;
	}
}
