/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.rti.dds.subscription.Subscriber;

@SuppressWarnings("serial")
public abstract class DevicePanel extends JPanel {
    protected final Subscriber subscriber;
    protected final String udi;
	
	public DevicePanel(Subscriber subscriber, String udi) {
		super();
		this.subscriber = subscriber;
		this.udi = udi;
		setOpaque(false);
		
	}
	
//	public void registerAndRequestRequiredIdentifiedUpdates() {
//		gateway.addListener(this);
//		MutableIdentifierArrayUpdate miau = new MutableIdentifierArrayUpdateImpl(Device.REQUEST_IDENTIFIED_UPDATES);
//		miau.setValue(requiredIdentifiedUpdates().toArray(new Identifier[0]));
//		miau.setTarget(source);
//		gateway.update(this, miau);
//	}
//	
//	public Collection<Identifier> requiredIdentifiedUpdates() {
//		return Arrays.asList(new Identifier[] {Device.NAME, Device.GUID, Device.ICON});
//	}
	
	
	private static void setBackground(Container c, Color bg) {
		for(Component comp : c.getComponents()) {
			comp.setBackground(bg);
			if(comp instanceof Container) {
				setBackground((Container) comp, bg);
			}
		}
	}
	
	private static void setForeground(Container c, Color fg) {
		for(Component comp : c.getComponents()) {
			comp.setForeground(fg);
			if(comp instanceof Container) {
				setForeground((Container) comp, fg);
			}
		}
	}
	
	protected void setInt(Integer i, JLabel label, String alt) {
		label.setText(null == i ? alt : Integer.toString(i));
	}
	protected void setInt(Number n, JLabel label, String alt) {
		setInt(null == n ? null : n.intValue(), label, alt);
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		setForeground(this, fg);
	}
	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		setBackground(this, bg);
	}
	
	public abstract void setName(String name);
	public abstract void setGuid(String name);
	public abstract void setIcon(Image image);
	
	public void destroy() {
//		gateway.removeListener(this);
	}
	
//	@Override
//	public void update(IdentifiableUpdate<?> update) {
//		if(source == null || source.equals(update.getSource())) {
//			if(Device.NAME.equals(update.getIdentifier())) {
//				setName( ((TextUpdate)update).getValue());
//			} else if(Device.GUID.equals(update.getIdentifier())) {
//				setGuid( ((TextUpdate)update).getValue());
//			} else if(Device.ICON.equals(update.getIdentifier())) {
//				ImageUpdate imageUpdate = (ImageUpdate) update;
//				int width = imageUpdate.getWidth();
//				int height = imageUpdate.getHeight();
//				BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//				byte[] raster = imageUpdate.getRaster();
//				IntBuffer ib = ByteBuffer.wrap(raster).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
//				for(int y = 0; y < height; y++) {
//					for(int x = 0; x < width; x++) {
//						bi.setRGB(x, y, ib.get());
//					}
//				}
//				setIcon(bi);
//			}
//			doUpdate(update);
//		}
//	}
	
//	protected void doUpdate(IdentifiableUpdate<?> update) {
//		
//	}
	
}

