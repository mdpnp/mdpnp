package org.mdpnp.comms.data.image;

import org.mdpnp.comms.MutableIdentifiableUpdateImpl;
import org.mdpnp.comms.Persistent;

@SuppressWarnings("serial")
public class MutableImageUpdateImpl extends MutableIdentifiableUpdateImpl<Image> implements MutableImageUpdate {

	private Image identifier;
	private byte[] raster;
	private int width, height;
	
	public MutableImageUpdateImpl() {
	}
	
	public MutableImageUpdateImpl(Image identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public byte[] getRaster() {
		return raster;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	@Persistent(key = true)
	public Image getIdentifier() {
		return identifier;
	}

	@Override
	public void setIdentifier(Image i) {
		this.identifier = i;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public void setRaster(byte[] raster) {
		this.raster = raster;
	}
	@Override
	public String toString() {
		return "[identifier="+identifier+",source="+getSource()+",target="+getTarget()+",width="+width+",height="+height+",raster.length="+raster.length+"]";
	}
}
