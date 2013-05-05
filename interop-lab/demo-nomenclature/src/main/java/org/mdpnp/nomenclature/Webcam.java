package org.mdpnp.nomenclature;

import org.mdpnp.data.image.Image;
import org.mdpnp.data.image.ImageImpl;

public interface Webcam {
	Image LIVE_FRAME = new ImageImpl(Webcam.class, "LIVE_FRAME");
}
