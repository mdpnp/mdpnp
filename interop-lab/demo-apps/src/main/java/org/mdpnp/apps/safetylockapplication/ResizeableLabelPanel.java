package org.mdpnp.apps.safetylockapplication;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/*
 * Functionality:
 * ResizeableLabelPanel is a JPanel for containing a modified JLabel called ResizeableLabel
 * ResizeableLabel automatically fills to the size of the ResizeableLabelPanel
 * This functionality holds whether or not ResizeableLabelPanel object is a member of
 * 	the domain of a layout manager that respects preferred and/or minimum size
 * Use:
 * Use the inherited member setOpaque(boolean) to alter transparency (default is true)
 * Use the inherited member setBackground(Color) to alter background color (default is white)
 * Use the member setTextColor(Color) to alter the color of the text of the label (default is black)
 * Use the member setSize to alter the size
 * Do not set a border - this will disrupt the described intended functionality
 * 	Use ResizeableLabelPanelWithBorder if a border is desired
 */

public class ResizeableLabelPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private ResizeableLabel label;
	
	public ResizeableLabelPanel(String labelString, Dimension dimension)
	{
		if (labelString != null)
			label = new ResizeableLabel(labelString);
		setLayout(new GridLayout(1,1));
		setBackground(Color.WHITE);
		add(label);
		
		if (dimension != null)
		{
			setPreferredSize(dimension);
			setMinimumSize(dimension);
		}
		revalidate();
	}
	
	public void updateLabel(String text)
	{
		if (text != null)
			label.setText(text);
	}
	
	//alter the color of the letters
	public void setTextColor(Color color)
	{
		if (label.getText() != null)
			label.setForeground(color);
	}
	
	public void setSize(Dimension dimension)
	{
		super.setSize(dimension);
		setPreferredSize(dimension);
		setMinimumSize(dimension);
		revalidate();
	}
	
	private class ResizeableLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		
		public static final int MIN_FONT_SIZE=3;
	    public static final int MAX_FONT_SIZE=1000;
	    Graphics g;
	 
	    public ResizeableLabel(String text) {
	        super(text);
	        init();
	        setHorizontalAlignment(SwingConstants.CENTER);
	        setForeground(Color.BLACK);
	    }
	 
	    protected void init() {
	        addComponentListener(new ComponentAdapter() {
	            public void componentResized(ComponentEvent e) {
	                adaptLabelFont(ResizeableLabel.this);
	            }
	        });
	    }
	 
	    protected void adaptLabelFont(JLabel l) {
	        if (g==null) {
	            return;
	        }
	        Rectangle r=l.getBounds();
	        int fontSize=MIN_FONT_SIZE;
	        Font f=l.getFont();
	 
	        Rectangle r1=new Rectangle();
	        Rectangle r2=new Rectangle();
	        while (fontSize<MAX_FONT_SIZE) {
	        	//originally passed fontSize and fontSize+1
	        	//but didn't result in the intended functionality working in all cases
	        	//i think this should do the trick
	            r1.setSize(getTextSize(l, f.deriveFont(f.getStyle(), fontSize+1)));
	            r2.setSize(getTextSize(l, f.deriveFont(f.getStyle(),fontSize+2)));
	            if (r.contains(r1) && ! r.contains(r2)) {
	                break;
	            }
	            fontSize++;
	        }
	 
	        setFont(f.deriveFont(f.getStyle(),fontSize));
	        repaint();
	    }
	 
	    private Dimension getTextSize(JLabel l, Font f) {
	        Dimension size=new Dimension();
	        g.setFont(f);
	        FontMetrics fm=g.getFontMetrics(f);
	        size.width=fm.stringWidth(l.getText());
	        size.height=fm.getHeight();
	 
	        return size;
	    }
	 
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        this.g=g;
	    }

	}
}
