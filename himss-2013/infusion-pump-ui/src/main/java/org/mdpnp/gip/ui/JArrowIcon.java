package org.mdpnp.gip.ui;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * @author Jeff Plourde
 *
 */
public class JArrowIcon implements Icon {

	public JArrowIcon() {
	}

	public JArrowIcon(int theta_degrees) {
		setRotation(theta_degrees);
	}

	private Stroke stroke = new BasicStroke(2f);
	private int theta_degrees = 0;
	
	public void setRotation(int theta_degrees) {
		this.theta_degrees = theta_degrees;
	}
	
	private final Dimension size = new Dimension(20,20);
	
	@Override
	public void paintIcon(Component c, Graphics g, int x_, int y_) {
		int x = size.width / 2 + x_;
		int y0 = size.height;
		int y1 = y_;
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(c.getForeground());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.rotate(Math.toRadians(theta_degrees), x_ + size.width / 2, y_ + size.height / 2);
		g2d.setStroke(stroke);

		g2d.drawLine(x, y_+y0, x, y1);
		g2d.drawLine(x, y1, x_+(int)(0.75*size.width), y_+(int)(0.35*y0));
		g2d.drawLine(x, y1, x_+(int)(0.25*size.width), y_+(int)(0.35*y0));
		
	}
	public static void main(String[] args) {
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(50, 50);
//		JLabel lbl = new JLabel(new JArrowIcon());
		JButton btn = new JButton();
		btn.setIcon(new JArrowIcon());
		
		frame.getContentPane().add(btn);
		frame.setVisible(true);
	}
	@Override
	public int getIconWidth() {
		return size.width;
	}
	@Override
	public int getIconHeight() {
		return size.height;
	}

	
}
