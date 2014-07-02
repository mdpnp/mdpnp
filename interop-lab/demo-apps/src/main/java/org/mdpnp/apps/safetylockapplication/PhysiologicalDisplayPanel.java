package org.mdpnp.apps.safetylockapplication;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.mdpnp.apps.safetylockapplication.Resources.DisplayMode;

public class PhysiologicalDisplayPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private DisplayMode mode;
	private ResizeableLabelPanelWithBorder descriptor;
	private ResizeableLabelPanelWithBorder value;
	private ResizeableLabelPanelWithBorder quality;
	private static final Dimension QUANTITATIVE_DESCRIPTOR_DISPLAY_SIZE = new Dimension(175, 30);
	private static final Dimension QUALITATIVE_DESCRIPTOR_DISPLAY_SIZE = new Dimension(175+40, 30);
	private static final Dimension VALUE_DISPLAY_SIZE = new Dimension(40, 30);
	private static final Dimension QUALITY_DISPLAY_SIZE = new Dimension(175+40, 20);
	private static final int PIXEL_SEPARATION = 3;
	private boolean isAvailable;
	
	private ArrayList<String> goodQualityDescriptors;
	private ArrayList<String> mediocreQualityDescriptors;
	
	public PhysiologicalDisplayPanel(String physiologicalDescriptor)
	{
		this(physiologicalDescriptor, DisplayMode.QUANTITATIVE);
	}
	
	public PhysiologicalDisplayPanel(String physiologicalDescriptor, DisplayMode mode)
	{
		this.mode = mode;
		if (mode == DisplayMode.QUANTITATIVE)
			descriptor = new ResizeableLabelPanelWithBorder(physiologicalDescriptor, QUANTITATIVE_DESCRIPTOR_DISPLAY_SIZE);
		else if (mode == DisplayMode.QUALITATIVE)
			descriptor = new ResizeableLabelPanelWithBorder(physiologicalDescriptor, QUALITATIVE_DESCRIPTOR_DISPLAY_SIZE);
		value = new ResizeableLabelPanelWithBorder("X", VALUE_DISPLAY_SIZE);
		quality = new ResizeableLabelPanelWithBorder("NULL", QUALITY_DISPLAY_SIZE);
		
		goodQualityDescriptors = new ArrayList<String>();
		
		appearMissing();
		setBackground(Color.BLACK);
		setLayout(new GridBagLayout());
		setupGridDisplay();
	}
	
	public void setGoodQualityDescriptors(ArrayList<String> descriptors)
	{
		goodQualityDescriptors = descriptors;
	}
	
	public void setMediocreQualityDescriptors(ArrayList<String> descriptors)
	{
		mediocreQualityDescriptors = descriptors;
	}
	
	/**
	 * @return
	 * @uml.property  name="isAvailable"
	 */
	public boolean isAvailable()
	{
		return isAvailable;
	}
	
	public void updateValue(Integer value)
	{
		if (mode == DisplayMode.QUANTITATIVE)
		{
			if (value.intValue() < 0)
			{
				this.value.updateLabel("X");
				appearMissing();
				isAvailable = false;
			}
			else if (value.intValue() >= 0)
			{
				appearAvailable();
				this.value.updateLabel(value.toString());
				isAvailable = true;
			}
		}
	}
	
	public void updateQuality(String quality)
	{
		if (mode == DisplayMode.QUALITATIVE && quality != null)
		{
			boolean good = false;
			boolean mediocre = false;
			for (String descriptor : goodQualityDescriptors)
			{
				if (quality.toLowerCase().contains(descriptor.toLowerCase()))
				{
					appearAvailable();
					good = true;
					mediocre = false;
					isAvailable = true;
					break;
				}
			}
			if (good == false && mediocreQualityDescriptors != null)
				for (String descriptor : mediocreQualityDescriptors)
				{
					if (quality.toLowerCase().contains(descriptor.toLowerCase()))
					{
						appearMediocre();
						mediocre = true;
						isAvailable = false;
						break;
					}
				}
			if (good == false && mediocre == false) {
				appearMissing();
				isAvailable = false;
				quality = "NULL";
			}
			
			this.quality.updateLabel(quality);
		}
	}
	
	public void changeMode(DisplayMode mode)
	{
		this.mode = mode;
		if (mode == DisplayMode.QUALITATIVE)
			descriptor.setSize(QUALITATIVE_DESCRIPTOR_DISPLAY_SIZE);
		else if (mode == DisplayMode.QUANTITATIVE)
			descriptor.setSize(QUANTITATIVE_DESCRIPTOR_DISPLAY_SIZE);
		setupGridDisplay();
	}
	
	private void setupGridDisplay()
	{
		removeAll();
		if (mode == DisplayMode.QUANTITATIVE)
		{
			GridBagConstraints gc = new GridBagConstraints();
			
			gc.gridx = 0;
			gc.gridy = 0;
			gc.weightx = 1;
			gc.weighty = 1;
			gc.insets = new Insets(0,PIXEL_SEPARATION,0,0);
			add(descriptor, gc);
			
			gc.gridx = 1;
			add(value, gc);
		}
		else if (mode == DisplayMode.QUALITATIVE)
		{
			GridBagConstraints gc = new GridBagConstraints();
			
			gc.gridx = 0;
			gc.gridy = 0;
			gc.weightx = 1;
			gc.weighty = 1;
			add(descriptor, gc);
			
			gc.insets = new Insets(PIXEL_SEPARATION,0,0,0);
			gc.gridy = 1;
			add(quality, gc);
		}
		revalidate();
	}
	
	private void appearMissing()
	{
		applyMissingStyle(descriptor);
		applyMissingStyle(value);
		applyMissingStyle(quality);
	}
	
	private void appearAvailable()
	{
		applyAvailableStyle(descriptor);
		applyAvailableStyle(value);
		applyAvailableStyle(quality);
	}
	
	private void appearMediocre()
	{
		applyMediocreStyle(descriptor);
		applyMediocreStyle(value);
		applyMediocreStyle(quality);
	}
	
	private void applyMissingStyle(ResizeableLabelPanelWithBorder resizeableLabelPanelWithBorder)
	{
		resizeableLabelPanelWithBorder.setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		resizeableLabelPanelWithBorder.setTextColor(Resources.valueMissingTextColor);
		resizeableLabelPanelWithBorder.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
	}
	
	private void applyAvailableStyle(ResizeableLabelPanelWithBorder resizeableLabelPanelWithBorder)
	{
		resizeableLabelPanelWithBorder.setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		resizeableLabelPanelWithBorder.setTextColor(Resources.valueAvailableTextColor);
		resizeableLabelPanelWithBorder.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
	}
	
	public void applyMediocreStyle(ResizeableLabelPanelWithBorder resizeableLabelPanelWithBorder)
	{
		resizeableLabelPanelWithBorder.setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		resizeableLabelPanelWithBorder.setTextColor(Resources.valueMediocreTextColor);
		resizeableLabelPanelWithBorder.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
	}
}
