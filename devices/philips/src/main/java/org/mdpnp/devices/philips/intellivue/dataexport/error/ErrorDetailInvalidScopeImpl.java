package org.mdpnp.devices.philips.intellivue.dataexport.error;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class ErrorDetailInvalidScopeImpl implements ErrorDetailInvalidScope {

	private long scope;
	
	@Override
	public void parse(ByteBuffer bb) {
		scope = Bits.getUnsignedInt(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedInt(bb, scope);
	}

	@Override
	public long getScope() {
		return scope;
	}
	
	@Override
	public String toString() {
		return Long.toString(scope);
	}
	

}
