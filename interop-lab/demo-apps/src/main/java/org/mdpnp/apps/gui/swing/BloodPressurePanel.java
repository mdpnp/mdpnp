/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.apps.gui.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.Identifier;
import org.mdpnp.comms.data.enumeration.EnumerationUpdate;
import org.mdpnp.comms.data.numeric.NumericUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdateImpl;
import org.mdpnp.comms.nomenclature.ConnectedDevice;
import org.mdpnp.comms.nomenclature.NoninvasiveBloodPressure;
import org.mdpnp.comms.nomenclature.NoninvasiveBloodPressure.NBPState;

@SuppressWarnings("serial")
public class BloodPressurePanel extends DevicePanel {
	
	private JLabel systolicLabel, diastolicLabel, pulseLabel, nameLabel;
	private JLabel systolic, diastolic, pulse;
	private JPanel systolicPanel, diastolicPanel, pulsePanel;
	private JLabel time;
	private JLabel nextInflation;
	private JButton inflate = new JButton("Inflate"); 
//	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public void setName(String name) {
		this.nameLabel.setText(name);
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	private String guid; 
	
	
	protected void buildComponents() {
		systolicLabel = new JLabel("mmHg");
				
		systolicPanel = new JPanel();
		systolicPanel.setOpaque(false);
		systolicPanel.setLayout(new BorderLayout());
		systolicPanel.add(systolic = new JLabel("---"), BorderLayout.CENTER);
		systolic.setHorizontalAlignment(JLabel.RIGHT);
		systolic.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		systolicPanel.add(systolicLabel, BorderLayout.EAST);
		
		diastolicLabel = new JLabel("mmHg");
		
		diastolicPanel = new JPanel();
		diastolicPanel.setOpaque(false);
		diastolicPanel.setLayout(new BorderLayout());
		diastolicPanel.add(diastolic = new JLabel("---"), BorderLayout.CENTER);
		diastolic.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		diastolic.setHorizontalAlignment(JLabel.RIGHT);
		diastolicPanel.add(diastolicLabel, BorderLayout.EAST);

		pulseLabel = new JLabel("BPM");
		
		pulsePanel = new JPanel();
		pulsePanel.setOpaque(false);
		pulsePanel.setLayout(new BorderLayout());
		pulsePanel.add(pulse = new JLabel("---"), BorderLayout.CENTER);
		pulse.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pulse.setHorizontalAlignment(JLabel.RIGHT);
		pulsePanel.add(pulseLabel, BorderLayout.EAST);
		
		nextInflation = new JLabel("...");
		nextInflation.setHorizontalAlignment(JLabel.CENTER);
		
		JPanel upper = new JPanel();
		upper.setOpaque(false);
		upper.setLayout(new GridLayout(4, 1));
		
		upper.add(systolicPanel);
		upper.add(diastolicPanel);
		upper.add(pulsePanel);
		upper.add(nextInflation);
		setLayout(new BorderLayout());
		add(upper, BorderLayout.CENTER);
		
		JPanel lower = new JPanel(new GridLayout(1, 2));
		
		lower.add(inflate);
		lower.add(time = new JLabel(""));
		
		time.setHorizontalAlignment(JLabel.RIGHT);
		add(lower, BorderLayout.SOUTH);
		
		
		add(nameLabel = new JLabel("NAME"), BorderLayout.NORTH);
		nameLabel.setHorizontalAlignment(JLabel.RIGHT);
		
		inflate.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        MutableTextUpdate mtu = new MutableTextUpdateImpl(NoninvasiveBloodPressure.REQUEST_NIBP, guid);
		        mtu.setSource("*");
		        mtu.setTarget(BloodPressurePanel.this.source);
		        
		        gateway.update(BloodPressurePanel.this, mtu);
		    }
		});
		
	}
	
	
	protected static float maxFontSize(JLabel label) {
		Font labelFont = label.getFont();
		String labelText = label.getText();

		int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
		int stringHeight = label.getFontMetrics(labelFont).getHeight();
		int componentWidth = label.getWidth();
		int componentHeight = label.getHeight();

		// Find out how much the font can grow in width.
		double widthRatio = (double)componentWidth / (double)stringWidth;
		double heightRatio = 1.0 * componentHeight / stringHeight;

		double smallerRatio = Math.min(widthRatio, heightRatio) - 0.5f;
		
		return (float) (labelFont.getSize2D() * smallerRatio);
	}
	
	protected static void resizeFontToFill(JLabel... label) {
		float fontSize = Float.MAX_VALUE;
		
		for(JLabel l : label) {
			fontSize = Math.min(fontSize, maxFontSize(l));
		}
		
		for(JLabel l : label) {
			l.setFont(l.getFont().deriveFont(fontSize));
		}
	}
	
	
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		if(e.getID() == ComponentEvent.COMPONENT_RESIZED) {
			resizeFontToFill(systolic, diastolic, pulse);
		}
		super.processComponentEvent(e);
	}
	
	@Override
	public Collection<Identifier> requiredIdentifiedUpdates() {
		List<Identifier> identifiers = new ArrayList<Identifier>();
		identifiers.addAll(Arrays.asList(new Identifier[] {ConnectedDevice.STATE, ConnectedDevice.CONNECTION_INFO, NoninvasiveBloodPressure.STATE, NoninvasiveBloodPressure.DIASTOLIC, NoninvasiveBloodPressure.SYSTOLIC, NoninvasiveBloodPressure.PULSE}));
		identifiers.addAll(super.requiredIdentifiedUpdates());
		return identifiers;
	}
	
	
	public BloodPressurePanel(Gateway gateway, String source) {
		super(gateway, source);
		buildComponents();
		enableEvents(ComponentEvent.COMPONENT_RESIZED);

		registerAndRequestRequiredIdentifiedUpdates();
		
	}
	
	public static boolean supported(Set<Identifier> identifiers) {
		return identifiers.contains(NoninvasiveBloodPressure.SYSTOLIC); 
	}
	
	@Override
	protected void doUpdate(IdentifiableUpdate<?> update) {
		if(NoninvasiveBloodPressure.SYSTOLIC.equals(update.getIdentifier())) {
			setInt( ((NumericUpdate)update).getValue(), this.systolic, "---");
		} else if(NoninvasiveBloodPressure.DIASTOLIC.equals(update.getIdentifier())) {
			setInt( ((NumericUpdate)update).getValue(), this.diastolic, "---");
		} else if(NoninvasiveBloodPressure.PULSE.equals(update.getIdentifier())) {
			setInt( ((NumericUpdate)update).getValue(), this.pulse, "---");
		} else if(NoninvasiveBloodPressure.INFLATION_PRESSURE.equals(update.getIdentifier())) {
			setInt( ((NumericUpdate)update).getValue(), this.systolic, "---");
		} else if(NoninvasiveBloodPressure.STATE.equals(update.getIdentifier())) {
			NoninvasiveBloodPressure.NBPState state = (NBPState) ((EnumerationUpdate)update).getValue();
			switch(state) {
			case Inflating:
			case Deflating:
				this.nextInflation.setText(state.toString());
				break;
			case Waiting:
			default:
			}
		} else if(NoninvasiveBloodPressure.NEXT_INFLATION_TIME_REMAINING.equals(update.getIdentifier())) {
			NumericUpdate nu = (NumericUpdate)update;
			Long nextInflation = null == nu.getValue() ? null : nu.getValue().longValue();
			if(null != nextInflation) {
				long seconds = (nextInflation % 60000L / 1000L);
				this.nextInflation.setText((int)Math.floor(1.0 * nextInflation / 60000.0) + ":" + (seconds<10?"0":"") + seconds + " MIN");
			}
		}
	}
	@Override
	public void setIcon(Image image) {
		// TODO Auto-generated method stub
		
	}
}
