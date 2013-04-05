package org.mdpnp.devices.philips.intellivue.dataexport.error;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.data.OIDType;

public class ErrorDetailProcessingFailureImpl implements ErrorDetailProcessingFailure {

	private OIDType errorId;
	private int length;
	
	@Override
	public void parse(ByteBuffer bb) {
		this.errorId = OIDType.parse(bb);
		this.length = Bits.getUnsignedShort(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		errorId.format(bb);
		Bits.putUnsignedShort(bb, length);
	}

	@Override
	public OIDType getErrorId() {
		return errorId;
	}

	@Override
	public int getLength() {
		return length;
	}
	
	public String toString() {
		return "[errorId="+errorId+",length="+length+"]";
	};
	

}
