package org.mdpnp.messaging;

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

import org.mdpnp.comms.data.image.ImageUpdate;

@SuppressWarnings("serial")
public class DeviceIcon extends ImageIcon {
	
	
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
	
	public void setImage(ImageUpdate imageUpdate) {
		int width = imageUpdate.getWidth();
		int height = imageUpdate.getHeight();
		byte[] raster = imageUpdate.getRaster();
		
		if(raster != null) {
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
//			setImage(bi.getScaledInstance(63, 63, Image.SCALE_SMOOTH));
		} else {
			setImage(WHITE_SQUARE);
		}
	}

	public DeviceIcon(ImageUpdate imageUpdate) {
		super();
		setImage(imageUpdate);
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
		if(!connected) {
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
	
}
