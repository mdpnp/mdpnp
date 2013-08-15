package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class PollProfileExtensions implements Value {
	
	private long pollProfileExtOptions = POLL_EXT_PERIOD_NU_1SEC | POLL_EXT_PERIOD_RTSA | POLL_EXT_NU_PRIO_LIST | POLL_EXT_ENUM;
	public static final long POLL_EXT_PERIOD_NU_1SEC = 0x80000000L;
	public static final long POLL_EXT_PERIOD_NU_AVG_12SEC = 0x40000000L;
	public static final long POLL_EXT_PERIOD_NU_AVG_60SEC = 0x20000000L;
	public static final long POLL_EXT_PERIOD_NU_AVG_300SEC = 0x10000000L;
	public static final long POLL_EXT_PERIOD_RTSA = 0x08000000L;
	public static final long POLL_EXT_ENUM = 0x04000000L;
	public static final long POLL_EXT_NU_PRIO_LIST = 0x02000000L;
	public static final long POLL_EXT_DYN_MODALITIES = 0x01000000L;
	
	
	private final AttributeValueList ext_attr = new AttributeValueList();

	@Override
	public void parse(ByteBuffer bb) {
		pollProfileExtOptions = Bits.getUnsignedInt(bb);
		ext_attr.parse(bb);
	}

	@Override
	public void format(ByteBuffer bb)  {
		Bits.putUnsignedInt(bb, pollProfileExtOptions);
		ext_attr.format(bb);
	}
	
	@Override
	public java.lang.String toString() {
	    return "[pollProfileExtOptions="+Long.toHexString(pollProfileExtOptions)+",ext_attr="+ext_attr+"]";
	}

}
