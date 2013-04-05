package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class TextId implements Value {
	private long textId;

	@Override
	public void parse(ByteBuffer bb) {
		textId = Bits.getUnsignedInt(bb);
	}
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedInt(bb, textId);
	}
	
	public long getTextId() {
		return textId;
	}
	
	public void setTextId(long textId) {
		this.textId = textId;
	}
	
	@Override
	public java.lang.String toString() {
		return Long.toString(textId);
	}
	
}
