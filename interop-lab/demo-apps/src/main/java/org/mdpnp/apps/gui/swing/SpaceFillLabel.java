package org.mdpnp.apps.gui.swing;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class SpaceFillLabel extends JLabel {
	public SpaceFillLabel() {
		this("");
	}
	
	public SpaceFillLabel(String text) {
		super(text);
		enableEvents(ComponentEvent.COMPONENT_EVENT_MASK);
	}
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		super.processComponentEvent(e);
		switch(e.getID()) {
		
		case ComponentEvent.COMPONENT_RESIZED:
			resizeFontToFill(this);
			break;
		}
	}
	
	@Override
	public void setText(String text) {
		String existingText = getText();
		boolean b = text != null && existingText != null && !text.equals(existingText);
		super.setText(text);
		if(b) {
			resizeFontToFill(this);
		}
	}
	
	protected static float maxFontSize(JLabel label) {
		Font labelFont = label.getFont();
		if(null == labelFont) {
			return -1.0f;
		}
		FontMetrics fontMetrics = label.getFontMetrics(labelFont);
		if(fontMetrics == null) {
			return -1.0f;
		}
		
		String labelText = label.getText();

		int stringWidth = fontMetrics.stringWidth(labelText);
		int stringHeight = fontMetrics.getHeight();
		int componentWidth = label.getWidth();
		int componentHeight = label.getHeight();

		// Find out how much the font can grow in width.
		double widthRatio = (double)componentWidth / (double)stringWidth;
		double heightRatio = 1.0 * componentHeight / stringHeight;

		double smallerRatio = Math.min(widthRatio, heightRatio) - 0.1f;
		
		return (float) (labelFont.getSize2D() * smallerRatio);
	}
	
	public static ComponentListener attachResizeFontToFill(JComponent component, final JLabel... label) {
		ComponentListener cl;
		component.addComponentListener(cl = new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				resizeFontToFill(label);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				
			}
			
		});
		return cl;
	}
	
	public static void resizeFontToFill(JLabel... label) {
		float fontSize = Float.MAX_VALUE;
		
		for(JLabel l : label) {
			fontSize = Math.min(fontSize, maxFontSize(l));
		}
		if(fontSize > 0.0f)  {
			
			for(JLabel l : label) {
				Font f = l.getFont();
				if(f != null) {
					l.setFont(f.deriveFont(fontSize));
				}
			}
		}
	}
}
