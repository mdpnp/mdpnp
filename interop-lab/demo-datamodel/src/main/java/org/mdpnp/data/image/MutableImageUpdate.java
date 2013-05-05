package org.mdpnp.data.image;

import org.mdpnp.data.MutableIdentifiableUpdate;

public interface MutableImageUpdate extends ImageUpdate, MutableIdentifiableUpdate<Image>  {
	boolean setWidth(int width);
	boolean setHeight(int height);
	boolean setRaster(byte[] raster);
}