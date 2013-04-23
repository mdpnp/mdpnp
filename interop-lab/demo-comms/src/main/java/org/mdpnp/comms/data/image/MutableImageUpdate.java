package org.mdpnp.comms.data.image;

import org.mdpnp.comms.MutableIdentifiableUpdate;

public interface MutableImageUpdate extends ImageUpdate, MutableIdentifiableUpdate<Image>  {
	boolean setWidth(int width);
	boolean setHeight(int height);
	boolean setRaster(byte[] raster);
}