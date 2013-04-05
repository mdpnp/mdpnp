package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.mdpnp.devices.io.util.Bits;

public class String implements Value {
	private java.lang.String string = "";

	private final Charset unicode = Charset.forName("UTF-16");
	
	private static CharBuffer decode(ByteBuffer bb, int length) {
		CharBuffer cb = CharBuffer.allocate(length/2);
		while(length > 0) {
			length -= 2;
			char c = bb.getChar();
			if(c >= 32) {
				cb.put(c);
			}
		}
		cb.flip();
		return cb;
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		int length = Bits.getUnsignedShort(bb);
		
		this.string = decode(bb, length).toString();

		
	}
	
	@Override
	public void format(ByteBuffer bb) {
		byte[] b = this.string.getBytes(unicode);
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
