package org.mdpnp.gip.ui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class JArrow extends JComponent {
	public JArrow() {
		setMinimumSize(new Dimension(50,50));
    	setPreferredSize(new Dimension(50,50));
	}
	
	public JArrow(int theta_degrees) {
		this();
		setRotation(theta_degrees);
	}
	
	public void setRotation(int degrees) {
		this.theta_degrees = degrees;
		repaint();
	}
	
	private final Dimension size = new Dimension();
	private Stroke stroke = new BasicStroke(6f);
	private int theta_degrees = 0;
	
	@Override
	protected void paintComponent(Graphics g) {
//		super.paintComponent(g);
		getSize(size);
				
		int x = size.width / 2;
		int y0 = size.height;
		int y1 = 0;
		
		Graphics2D g2d = (Graphics2D) g;
//		g2d.clearRect(0, 0, size.width, size.height);
		g.setColor(getForeground());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setTransform(AffineTransform.getRotateInstance(Math.toRadians(theta_degrees), size.width/2, size.height/2));
		g2d.setStroke(stroke);
		g.drawLine(x-3, y0, x-3, y1);
		g.drawLine(x-3, y1, (int)(0.75*size.width), (int)(0.35*y0));
		g.drawLine(x-3, y1, (int)(0.25*size.width), (int)(0.35*y0));
	}
	public static void main(String[] args) {
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(50, 50);
		frame.getContentPane().add(new JArrow(180));
		frame.setVisible(true);
	}
}
