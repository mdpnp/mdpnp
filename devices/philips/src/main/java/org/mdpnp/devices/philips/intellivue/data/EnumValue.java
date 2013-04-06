package org.mdpnp.devices.philips.intellivue.data;


public interface EnumValue<T extends EnumMessage<T>> extends Value {
	T getEnum();
	void setEnum(T t);
}
