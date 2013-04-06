package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

public class EnumValueImpl<T extends EnumMessage<T>> implements EnumValue<T> {

	private T enumValue;
	
	public EnumValueImpl(T enumValue) {
		this.enumValue = enumValue;
	}
	
	@Override
	public void format(ByteBuffer bb) {
		enumValue.format(bb);
	}

	@Override
	public void parse(ByteBuffer bb) {
		enumValue = enumValue.parse(bb);
	}

	@Override
	public T getEnum() {
		return enumValue;
	}

	@Override
	public void setEnum(T t) {
		enumValue = t;
	}

	@Override
	public java.lang.String toString() {
		return null==enumValue?"null":enumValue.toString();
	}
}
