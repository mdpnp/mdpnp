package org.mdpnp.devices.philips.intellivue.data;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum SampleArrayFixedValId implements OrdinalEnum.IntType {
	/**
	 * Not specified
	 */
	SA_FIX_UNSPEC(0),
	/**
	 * Invalid sample mask
	 */
	SA_FIX_INVALID_MASK(1),
	/**
	 * Pace pulse detected
	 */
	SA_FIX_PACER_MASK(2),
	/**
	 * Defib marker in this sample
	 */
	SA_FIX_DEFIB_MARKER_MASK(3),
	/**
	 * Indicates saturation condition in this sample.
	 */
	SA_FIX_SATURATION(4),
	/**
	 * Indicates QRS rigger around this sample
	 */
	SA_FIX_QRS_MASK(5),;
	
	private final int x;
	
	private SampleArrayFixedValId(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, SampleArrayFixedValId> map = OrdinalEnum.buildInt(SampleArrayFixedValId.class);
	
	public static SampleArrayFixedValId valueOf(int x) {
		return map.get(x);
	}
	
	public int asInt() {
		return x;
	}
}
