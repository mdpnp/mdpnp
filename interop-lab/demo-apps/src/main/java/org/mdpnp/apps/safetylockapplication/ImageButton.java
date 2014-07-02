package org.mdpnp.apps.safetylockapplication;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @uml.property  name="img"
	 */
	private Image img;

	  public ImageButton(String img)
	  {
	    this(new ImageIcon(img).getImage());
	  }
	  
	  public ImageButton(Image img)
	  {	
		this.img = img;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);	
		setSize(size);
		setLayout(null);
		setBorder(BorderFactory.createEmptyBorder());
	  }
	  
	  public void updateImage(String img)
	  {
		  updateImage(new ImageIcon(img).getImage());
	  }
	  
	  public void updateImage(Image img)
	  {
		this.img = img;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
		setBorder(BorderFactory.createEmptyBorder());
		repaint();
	  }

	  public void paintComponent(Graphics g) 
	  {
		  super.paintComponent(g);
		  g.drawImage(img, 0, 0, null);
	  }
	  	
	}