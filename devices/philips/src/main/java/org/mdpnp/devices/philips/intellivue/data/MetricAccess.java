package org.mdpnp.devices.philips.intellivue.data;

public enum MetricAccess {
	/**
	 * The intermitted availability bit is set, if the observed values not always
     * available (e.g. only if a measurement is explicitly started).
	 */
	AVAIL_INTERMITTEND,
	/**
	 * observed value is updated periodically
	 */
	UPD_PERIODIC,
	/**
	 * observed value is updated episodically (exactly one update mode (UPD_) must
     * be set
	 */
	UPD_EPISODIC,
	/**
	 * indicates that the measurement is non continuous (this is
     * different from the update mode)
	 */
	MSMT_NONCONTINUOUS;
	
	public final static MetricAccess valueOf(int x) {
		switch(x) {
		case 0x8000:
			return AVAIL_INTERMITTEND;
		case 0x4000:
			return UPD_PERIODIC;
		case 0x2000:
			return UPD_EPISODIC;
		case 0x1000:
			return MSMT_NONCONTINUOUS;
		default:
			return null;
		}
	}
	
	public int asInt() {
		switch(this) {
		case AVAIL_INTERMITTEND:
			return 0x8000;
		case UPD_PERIODIC:
			return 0x4000;
		case UPD_EPISODIC:
			return 0x2000;
		case MSMT_NONCONTINUOUS:
			return 0x1000;
		default:
			throw new IllegalArgumentException("Unknown MetricAccess:"+this);
		}
	}
}
