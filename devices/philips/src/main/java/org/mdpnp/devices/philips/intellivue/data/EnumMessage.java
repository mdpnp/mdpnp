package org.mdpnp.devices.philips.intellivue.data;
import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.Formatable;

public interface EnumMessage<E> extends Formatable {
	E parse(ByteBuffer bb);
}
