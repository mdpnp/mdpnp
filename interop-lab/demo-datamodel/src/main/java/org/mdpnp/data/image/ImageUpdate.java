package org.mdpnp.data.image;

import org.mdpnp.data.IdentifiableUpdate;
import org.mdpnp.data.Persistent;

@Persistent(sparse=false)
public interface ImageUpdate extends IdentifiableUpdate<Image> {
	@Persistent
	byte[] getRaster();
	@Persistent
	int getWidth();
	@Persistent
	int getHeight();
}
