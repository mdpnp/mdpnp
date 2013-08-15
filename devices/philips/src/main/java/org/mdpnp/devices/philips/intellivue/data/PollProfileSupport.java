package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;

public class PollProfileSupport implements Value {
	private long pollProfileRevision = POLL_PROFILE_REV_0;
	public static final long POLL_PROFILE_REV_0 = 0x80000000L;
	
	private final RelativeTime minPollPeriod = new RelativeTime();
	
	
	private long maxMtuRx = 1400L, maxMtuTx = 1400L, maxBwTx = 0xFFFFFFFFL;
	private long pollProfileOptions = P_OPT_DYN_CREATE_OBJECTS | P_OPT_DYN_DELETE_OBJECTS;
	public static final long P_OPT_DYN_CREATE_OBJECTS = 0x40000000L;
	public static final long P_OPT_DYN_DELETE_OBJECTS = 0x20000000L;
	
	private final AttributeValueList optionalPackages = new AttributeValueList();
	
	private final Attribute<PollProfileExtensions> pollProfileExtensions = AttributeFactory.getPollProfileExtensions();
	
	@Override
	public java.lang.String toString() {
	    return "[pollProfileRevision="+pollProfileRevision+",minPollPeriod="+minPollPeriod+",maxMtuRx="+maxMtuRx+",maxMtuTx="+maxMtuTx+",maxBwTx="+maxBwTx+",pollProfileOptions="+Long.toHexString(pollProfileOptions)+",optionaPackages="+optionalPackages+",pollProfileExtensions="+pollProfileExtensions+"]";
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		pollProfileRevision = Bits.getUnsignedInt(bb);
		minPollPeriod.parse(bb);
		maxMtuRx = Bits.getUnsignedInt(bb);
		maxMtuTx = Bits.getUnsignedInt(bb);
		maxBwTx  = Bits.getUnsignedInt(bb);
		pollProfileOptions = Bits.getUnsignedInt(bb);
		optionalPackages.parse(bb);
		
		optionalPackages.get(pollProfileExtensions);
	}
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedInt(bb, pollProfileRevision);
		minPollPeriod.format(bb);
		Bits.putUnsignedInt(bb, maxMtuRx);
		Bits.putUnsignedInt(bb, maxMtuTx);
		Bits.putUnsignedInt(bb, maxBwTx);
		Bits.putUnsignedInt(bb, pollProfileOptions);
		
		optionalPackages.reset();
		optionalPackages.add(pollProfileExtensions);
		optionalPackages.format(bb);
	}

	public RelativeTime getMinPollPeriod() {
		return minPollPeriod;
	}
	public long getMaxBwTx() {
		return maxBwTx;
	}
	public long getMaxMtuRx() {
		return maxMtuRx;
	}
	public long getMaxMtuTx() {
		return maxMtuTx;
	}
	public void setMaxBwTx(long maxBwTx) {
		this.maxBwTx = maxBwTx;
	}
	public void setMaxMtuRx(long maxMtuRx) {
		this.maxMtuRx = maxMtuRx;
	}
	public void setMaxMtuTx(long maxMtuTx) {
		this.maxMtuTx = maxMtuTx;
	}
	public PollProfileExtensions getPollProfileExtensions() {
		return pollProfileExtensions.getValue();
	}

}
