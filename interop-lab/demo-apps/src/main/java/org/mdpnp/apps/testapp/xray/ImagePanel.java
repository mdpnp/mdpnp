package org.mdpnp.apps.testapp.xray;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class ImagePanel extends JComponent {
	
	protected int componentWidth, componentHeight;
	protected int imageWidth, imageHeight;
	protected Image image;
	
	public ImagePanel() {
		
		enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
	}
	
	public void setImage(Image image) {
		this.image = image;
		if(null!=image) {
			this.imageWidth = image.getWidth(this);
			this.imageHeight = image.getHeight(this);
		}
	}
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		super.processComponentEvent(e);
		switch(e.getID()) {
		case ComponentEvent.COMPONENT_RESIZED:
			componentWidth = getWidth();
			componentHeight = getHeight();
		}
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Image image = this.image;
		if(image != null) {
			g.drawImage(image, 0, 0, componentWidth, componentHeight, 0, 0, imageWidth, imageHeight, this);
		}
	}
}