package org.mdpnp.comms.nomenclature;

import org.mdpnp.comms.data.image.Image;
import org.mdpnp.comms.data.image.ImageImpl;

public interface Webcam {
	Image LIVE_FRAME = new ImageImpl(Webcam.class, "LIVE_FRAME");
}
