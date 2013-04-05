package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class OperatingMode implements Value  {
	private int bitfield;
	
	private static final int OPMODE_UNSPEC = 0x8000;
	private static final int MONITORING = 0x4000;
	private static final int DEMO = 0x2000;
	private static final int SERVICE = 0x1000;
	private static final int OPMODE_STANDBY = 0x0002;
	private static final int CONFIG = 0x0001;
	
	public boolean isUnspecified() {
		return 0 != (OPMODE_UNSPEC & bitfield);
	}
	public boolean isMonitoring() {
		return 0 != (MONITORING & bitfield);
	}
	public boolean isDemo() {
		return 0 != (DEMO & bitfield);
	}
	public boolean isService() {
		return 0 != (SERVICE & bitfield);
	}
	public boolean isStandby() {
		return 0 != (OPMODE_STANDBY & bitfield);
	}
	public boolean isConfig() {
		return 0 != (CONFIG & bitfield);
	}
	
	@Override
	public java.lang.String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		
		if(isUnspecified()) {
			sb.append("OPMODE_UNSPEC ");
		}
		if(isMonitoring()) {
			sb.append("MONITORING ");
		}
		if(isDemo()) {
			sb.append("DEMO ");
			
		}
		if(isService()) {
			sb.append("SERVICE ");
		}
		if(isStandby()) {
			sb.append("STANDBY ");
		}
		if(isConfig()) {
			sb.append("CONFIG ");
		}
		sb.append("]");
		
		return sb.toString();
	}



	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, bitfield);
	}



	@Override
	public void parse(ByteBuffer bb) {
		bitfield = Bits.getUnsignedShort(bb);
	}
}
