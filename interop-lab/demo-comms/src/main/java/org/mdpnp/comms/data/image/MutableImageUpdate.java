package org.mdpnp.comms.data.image;

import org.mdpnp.comms.MutableIdentifiableUpdate;

public interface MutableImageUpdate extends ImageUpdate, MutableIdentifiableUpdate<Image>  {
	void setWidth(int width);
	void setHeight(int height);
	void setRaster(byte[] raster);
}