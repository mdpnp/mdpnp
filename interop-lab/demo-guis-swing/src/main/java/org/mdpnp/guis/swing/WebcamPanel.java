package org.mdpnp.guis.swing;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Set;

import javax.swing.ImageIcon;

import org.mdpnp.data.IdentifiableUpdate;
import org.mdpnp.data.Identifier;
import org.mdpnp.data.image.ImageUpdate;
import org.mdpnp.messaging.Gateway;
import org.mdpnp.nomenclature.Webcam;

public class WebcamPanel extends DevicePanel {

	private BufferedImage bufferedImage;
	private final ImageIcon imageIcon = new ImageIcon();
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		imageIcon.paintIcon(this, g, 0, 0);
	}
	
	public WebcamPanel(Gateway gateway, String source) {
		super(gateway, source);
		registerAndRequestRequiredIdentifiedUpdates();
	}

	@Override
	public void setName(String name) {
		
	}

	@Override
	public void setGuid(String name) {
		
	}

	@Override
	public void setIcon(Image image) {
		
	}
	
	@Override
	protected void doUpdate(IdentifiableUpdate<?> update) {
		super.doUpdate(update);
		if(Webcam.LIVE_FRAME.equals(update.getIdentifier())) {
			ImageUpdate iu = (ImageUpdate) update;
			int width = iu.getWidth();
			int height = iu.getHeight();
			if(null == bufferedImage || width!=bufferedImage.getWidth() || height != bufferedImage.getHeight()) {
				bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				imageIcon.setImage(bufferedImage);
			}
			byte[] raster = iu.getRaster();

			IntBuffer intBuffer = ByteBuffer.wrap(raster).order(ByteOrder.BIG_ENDIAN).asIntBuffer();

			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					bufferedImage.setRGB(x, y, intBuffer.get()); 
				}
			}
			repaint();
		}
	}

	public static boolean supported(Set<Identifier> identifiers) {
		return identifiers.contains(Webcam.LIVE_FRAME);
	}
}
