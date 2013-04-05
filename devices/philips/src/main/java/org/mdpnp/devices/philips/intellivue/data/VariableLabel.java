package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public class VariableLabel implements Parseable, Formatable {
	private java.lang.String string = "";
	
	public static final Charset ASCII = Charset.forName("ASCII");

	public static final java.lang.String decode(ByteBuffer bb, int length) {
		CharBuffer cb = CharBuffer.allocate(length);
		while(--length >= 0) {
			byte b = bb.get();
			if(b >= 32) {
				cb.put( (char) b );
			}
		}
		cb.flip();
		return cb.toString();
		
	}
		
	@Override
	public void parse(ByteBuffer bb) {
		int length = Bits.getUnsignedShort(bb);
		this.string = decode(bb, length);
	}
	
	@Override
	public void format(ByteBuffer bb) {
		byte[] b = string.getBytes(ASCII);
		Bits.putUnsignedShort(bb, b.length);
		bb.put(b);
	}
	
	public java.lang.String getString() {
		return string;
	}
	public void setString(java.lang.String string) {
		this.string = string;
	}
	
	@Override
	public java.lang.String toString() {
		return string;
	}
}
