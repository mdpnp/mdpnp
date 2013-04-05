package org.mdpnp.devices.philips.intellivue.data;

public enum MetricCategory {
	/**
	 * not specified
	 */
	MCAT_UNSPEC,
	/**
	 * automatic measurement
	 */
	AUTO_MEASUREMENT,
	/**
	 * manual measurement
	 */
	MANUAL_MEASUREMENT,
	/**
	 * automatic setting
	 */
	AUTO_SETTING,
	/**
	 * manual setting
	 */
	MANUAL_SETTING,
	/**
	 * automatic calculation, e.g. differential temperature
	 */
	AUTO_CALCULATION,
	/**
	 * manual calculation
	 */
	MANUAL_CALCULATION,
	/**
	 * this measurement may change its category during
     * operation or may be used in various modes.
	 */
	MULTI_DYNAMIC_CAPABILITIES,
	/**
	 * measurement is automatically adjusted for patient temperature
	 */
	AUTO_ADJUST_PAT_TEMP,
	/**
	 * measurement manually adjusted for patient temperature
	 */
	MANUAL_ADJUST_PAT_TEMP,
	/**
	 * this is not a measurement, but an alarm limit setting
	 */
	AUTO_ALARM_LIMIT_SETTING;
	
	public static final MetricCategory valueOf(int x) {
		switch(x) {
		case 0:
			return MCAT_UNSPEC;
		case 1:
			return AUTO_MEASUREMENT;
		case 2:
			return MANUAL_MEASUREMENT;
		case 3:
			return AUTO_SETTING;
		case 4:
			return MANUAL_SETTING;
		case 5:
			return AUTO_CALCULATION;
		case 6:
			return MANUAL_CALCULATION;
		case 50:
			return MULTI_DYNAMIC_CAPABILITIES;
		case 128:
			return AUTO_ADJUST_PAT_TEMP;
		case 129:
			return MANUAL_ADJUST_PAT_TEMP;
		case 130:
			return AUTO_ALARM_LIMIT_SETTING;
		default:
			return null;
			
		}
	}
	
	public int asInt() {
		switch(this) {
		case MCAT_UNSPEC:
			return 0;
		case AUTO_MEASUREMENT:
			return 1;
		case MANUAL_MEASUREMENT:
			return 2;
		case AUTO_SETTING:
			return 3;
		case MANUAL_SETTING:
			return 4;
		case AUTO_CALCULATION:
			return 5;
		case MANUAL_CALCULATION:
			return 6;
		case MULTI_DYNAMIC_CAPABILITIES:
			return 50;
		case AUTO_ADJUST_PAT_TEMP:
			return 128;
		case MANUAL_ADJUST_PAT_TEMP:
			return 129;
		case AUTO_ALARM_LIMIT_SETTING:
			return 130;
		default:
			throw new IllegalArgumentException("Unknown MetricCategory:"+this);
		}
	}
}
