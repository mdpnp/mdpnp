package org.mdpnp.apps.testapp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes raw image raster data from ice.Image as an ImageIcon
 * suitable for use in Swing.  Also reflects connectivity state
 * when applicable
 * @author jplourde
 *
 */
@SuppressWarnings("serial")
public class DeviceIcon extends ImageIcon {
    private static final Logger log = LoggerFactory.getLogger(DeviceIcon.class);
	
	private static final BufferedImage WHITE_SQUARE = new BufferedImage(96, 96, BufferedImage.TYPE_4BYTE_ABGR);
	static {
		for(int y = 0; y < WHITE_SQUARE.getHeight(); y++) {
			for(int x = 0; x < WHITE_SQUARE.getWidth(); x++) {
				WHITE_SQUARE.setRGB(x, y, 0xFFFFFFFF);
			}
		}
	}

	public DeviceIcon() {
		super(WHITE_SQUARE);
	}

	
   public boolean isBlank() {
       return WHITE_SQUARE.equals(getImage());
   }
	
   public void setImage(ice.Image image) {
        int width = image.width;
        int height = image.height;
        byte[] raster = new byte[image.raster.size()];
        image.raster.toArrayByte(raster);
        
        if(raster.length < (width * height * 4)) {
            throw new IllegalArgumentException("the specified image is " + width + "x" + height + " and only " + raster.length + " bytes");
        }
        
        if(raster != null && width > 0 && height > 0) {
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            IntBuffer ib = ByteBuffer.wrap(raster).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    bi.setRGB(x, y, ib.get());
                }
            }
            BufferedImage after = new BufferedImage(3*width/4+1, 3*height/4+1, BufferedImage.TYPE_INT_ARGB);
            java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
            at.scale(0.75, 0.75);
            
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            after = scaleOp.filter(bi, after);
            setImage(after);
            log.debug("New image is width="+width+", height="+height);
//	          setImage(bi.getScaledInstance(63, 63, Image.SCALE_SMOOTH));
        } else {
            log.warn("width="+width+", height="+height+(raster==null?", raster is null":"")+", using WHITE_SQUARE");
            setImage(WHITE_SQUARE);
        }
    }
	
   public DeviceIcon(ice.Image image) {
        super();
        setImage(image);
    }
	public DeviceIcon(Image image) {
		super(image);
	}
	
	private static final double PI_OVER_4 = Math.PI / 4.0;
	private static final double FIVEPI_OVER_4 = 5.0 * Math.PI / 4.0;
	
	private static final double unitX1 = Math.cos(PI_OVER_4);
	private static final double unitY1 = Math.sin(PI_OVER_4);
	
	private static final double unitX2 = Math.cos(FIVEPI_OVER_4);
	private static final double unitY2 = Math.sin(FIVEPI_OVER_4);
	
	@Override
	public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
		super.paintIcon(c, g, x, y);
		if(!isConnected()) {
			int height = getIconHeight();
			int width = getIconWidth();
			g.setColor(Color.red);
			if(g instanceof Graphics2D) {
				((Graphics2D)g).setStroke(new BasicStroke(3.0f));
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
			}
			g.drawOval(x, y, width, height);
			
			g.drawLine((int)(x + unitX1 * width / 2 + width / 2), (int)(y + unitY1 * height / 2 + height / 2), (int)(x + unitX2 * width / 2 + width / 2) , (int)(y + unitY2 * height / 2 + height / 2));
		}
	}
	
	private boolean connected = false;
	
	public void setConnected(boolean connected) {
		if(connected ^ this.connected) {
			this.connected = connected;
		}
	}
	
	protected boolean isConnected() {
	    return this.connected;
	}
	
}
