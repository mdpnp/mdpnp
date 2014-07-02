package org.mdpnp.apps.safetylockapplication;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


/*
 * Functionality:
 * ResizeableLabelPanelWithBorder is equivalent to a ResizeableLabelPanel in functionality,
 * 	except that it includes border capabilities
 * Use:
 * Use the member setOpaque(boolean) to alter transparency (default is true)
 * Use the member setBackground(Color) to alter background color (default is white)
 * Use the member setTextColor(Color) to alter the color of the text of the label (default is black)
 * Use the member inherited member setBorder(Border) to alter the border (default is no border)
 * Use the member setSize to alter the size
 */

public class ResizeableLabelPanelWithBorder extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @uml.property  name="resizeableLabelPanel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	ResizeableLabelPanel resizeableLabelPanel;
	
	public ResizeableLabelPanelWithBorder(String labelString, Dimension dimension)
	{
		resizeableLabelPanel = new ResizeableLabelPanel(labelString, dimension);
		setLayout(new GridLayout(1,1));
		add(resizeableLabelPanel);
	}
	
	public void updateLabel(String text)
	{
		resizeableLabelPanel.updateLabel(text);
		revalidate();
	}
	
	public void setOpaque(boolean isOpaque)
	{
		super.setOpaque(isOpaque);
		if (resizeableLabelPanel != null)
			resizeableLabelPanel.setOpaque(isOpaque);
	}
	
	public void setTextColor(Color color)
	{
		if (resizeableLabelPanel != null)
			resizeableLabelPanel.setTextColor(color);
	}
	
	public void setBackground(Color color)
	{
		super.setBackground(color);
		if (resizeableLabelPanel != null)
			resizeableLabelPanel.setBackground(color);
	}
	
	public void setSize(Dimension dimension)
	{
		super.setSize(dimension);
		resizeableLabelPanel.setSize(dimension);
	}
	
}
