package org.mdpnp.devices.philips.intellivue.dataexport;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.RelativeTime;

public class EventReport implements Parseable, Formatable {
	private final ManagedObjectIdentifier identifier = new ManagedObjectIdentifier();
	private final RelativeTime time = new RelativeTime();
	private OIDType oid = OIDType.lookup(0);
	private int length;
	
	@Override
	public void parse(ByteBuffer bb) {
		identifier.parse(bb);
		time.parse(bb);
		oid = OIDType.lookup(Bits.getUnsignedShort(bb));
		length = Bits.getUnsignedShort(bb);
	}
	
	@Override
	public void format(ByteBuffer bb) {
		identifier.format(bb);
		time.format(bb);
		oid.format(bb);
		Bits.putUnsignedShort(bb, length);
	}

	
	@Override
	public String toString() {
		return "[identifier="+identifier+",time="+time+",oid="+oid+",length="+length+"]";
	}
}
