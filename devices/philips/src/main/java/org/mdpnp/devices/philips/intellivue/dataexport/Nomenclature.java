package org.mdpnp.devices.philips.intellivue.dataexport;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.data.Value;

public class Nomenclature implements Value {

	private short majorVersion, minorVersion;
	
	public short getMajorVersion() {
		return majorVersion;
	}
	
	public short getMinorVersion() {
		return minorVersion;
	}
	
	public void setMajorVersion(short majorVersion) {
		this.majorVersion = majorVersion;
	}
	public void setMinorVersion(short minorVersion) {
		this.minorVersion = minorVersion;
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		if(0 != bb.get() || 0 != bb.get()) {
			throw new IllegalArgumentException("Nomenclature did not start with 0x0000");
		}
		majorVersion = Bits.getUnsignedByte(bb);
		minorVersion = Bits.getUnsignedByte(bb);
	}
	
	@Override
	public void format(ByteBuffer bb) {
		bb.put((byte)0x00);
		bb.put((byte)0x00);
		Bits.putUnsignedByte(bb, majorVersion);
		Bits.putUnsignedByte(bb, minorVersion);
	}
	
	@Override
	public String toString() {
		return "[majorVersion="+majorVersion+",minorVersion="+minorVersion+"]";
	}

}
