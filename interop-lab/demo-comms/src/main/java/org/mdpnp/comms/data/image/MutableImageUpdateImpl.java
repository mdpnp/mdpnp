package org.mdpnp.comms.data.image;

import org.mdpnp.comms.MutableIdentifiableUpdateImpl;
import org.mdpnp.comms.Persistent;

@SuppressWarnings("serial")
public class MutableImageUpdateImpl extends MutableIdentifiableUpdateImpl<Image> implements MutableImageUpdate {
	private byte[] raster;
	private int width, height;
	
	public MutableImageUpdateImpl() {
	}
	
	public MutableImageUpdateImpl(Image identifier) {
		super(identifier);
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
	public boolean setWidth(int width) {
	    if(width == this.width) {
	        return false;
	    } else {
    		this.width = width;
    		return true;
	    }
	}

	@Override
	public boolean setHeight(int height) {
	    if(height == this.height) {
	        return false;
	    } else {
	        this.height = height;
	        return true;
	    }
	}

	@Override
	public boolean setRaster(byte[] raster) {
	    if(null == raster) {
	        if(null == this.raster) {
	            return false;
	        } else {
	            this.raster = raster;
	            return true;
	        }
	    } else {
	        if(null == this.raster) {
	            this.raster = raster;
	            return true;
	        } else {
	            // check reference equality only
	            if(raster == this.raster) {
	                return false;
	            } else {
	                this.raster = raster;
	                return true;
	            }
	        }
	    }
	}
	@Override
	public String toString() {
		return "[identifier="+getIdentifier()+",source="+getSource()+",target="+getTarget()+",width="+width+",height="+height+",raster.length="+raster.length+"]";
	}
}
