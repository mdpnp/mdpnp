package org.mdpnp.devices.philips.intellivue.data;
import java.nio.ByteBuffer;

public interface EnumParseable<E> {
	E parse(ByteBuffer bb);
}
