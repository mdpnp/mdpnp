package org.mdpnp.devices.philips.intellivue.data;

import org.mdpnp.devices.philips.intellivue.Formatable;

public interface EnumValue<T extends EnumParseable<T> & Formatable> extends Value {
	T getEnum();
	void setEnum(T t);
}
