package org.mdpnp.comms.data.image;

import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.Persistent;

@Persistent(sparse=false)
public interface ImageUpdate extends IdentifiableUpdate<Image> {
	@Persistent
	byte[] getRaster();
	@Persistent
	int getWidth();
	@Persistent
	int getHeight();
}
