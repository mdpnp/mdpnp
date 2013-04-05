package org.mdpnp.devices.philips.intellivue.action.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.action.ObservationPoll;
import org.mdpnp.devices.philips.intellivue.action.SingleContextPoll;
import org.mdpnp.devices.philips.intellivue.util.Util;

public class SingleContextPollImpl implements SingleContextPoll {
	private int mdsContext;
	private final List<ObservationPoll> pollInfo = new ArrayList<ObservationPoll>();
	
	@Override
	public int getMdsContext() {
		return mdsContext;
	}

	@Override
	public List<ObservationPoll> getPollInfo() {
		return pollInfo;
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		mdsContext = Bits.getUnsignedShort(bb);
		Util.PrefixLengthShort.read(bb, pollInfo, true, ObservationPollImpl.class);
	}
	
	@Override
	public void format(ByteBuffer bb) {
	    Bits.putUnsignedShort(bb, mdsContext);
		Util.PrefixLengthShort.write(bb, pollInfo);
	}
	
	@Override
	public String toString() {
		return "[mdsContext="+mdsContext+",pollInfo="+pollInfo+"]";
	}
}