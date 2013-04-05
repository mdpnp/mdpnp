package org.mdpnp.devices.philips.intellivue.data;

public enum SampleArrayFixedValId {
	/**
	 * Not specified
	 */
	SA_FIX_UNSPEC,
	/**
	 * Invalid sample mask
	 */
	SA_FIX_INVALID_MASK,
	/**
	 * Pace pulse detected
	 */
	SA_FIX_PACER_MASK,
	/**
	 * Defib marker in this sample
	 */
	SA_FIX_DEFIB_MARKER_MASK,
	/**
	 * Indicates saturation condition in this sample.
	 */
	SA_FIX_SATURATION,
	/**
	 * Indicates QRS rigger around this sample
	 */
	SA_FIX_QRS_MASK,;
	
	public static SampleArrayFixedValId valueOf(int x) {
		switch(x) {
		case 0:
			return SA_FIX_UNSPEC;
		case 1:
			return SA_FIX_INVALID_MASK;
		case 2:
			return SA_FIX_PACER_MASK;
		case 3:
			return SA_FIX_DEFIB_MARKER_MASK;
		case 4:
			return SA_FIX_SATURATION;
		case 5:
			return SA_FIX_QRS_MASK;
		default:
			return null;
		}
	}
	
	public int asInt() {
		switch(this) {
		case SA_FIX_UNSPEC:
			return 0;
		case SA_FIX_INVALID_MASK:
			return 1;
		case SA_FIX_PACER_MASK:
			return 2;
		case SA_FIX_DEFIB_MARKER_MASK:
			return 3;
		case SA_FIX_SATURATION:
			return 4;
		case SA_FIX_QRS_MASK:
			return 5;
		default:
			throw new IllegalArgumentException("Unknown SampleArrayFixedValId:"+this);
		
		}
	}
}
