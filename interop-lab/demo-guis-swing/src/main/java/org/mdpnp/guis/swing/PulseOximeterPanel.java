/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mdpnp.data.IdentifiableUpdate;
import org.mdpnp.data.Identifier;
import org.mdpnp.data.enumeration.EnumerationUpdate;
import org.mdpnp.data.numeric.Numeric;
import org.mdpnp.data.numeric.NumericUpdate;
import org.mdpnp.data.text.TextUpdate;
import org.mdpnp.data.waveform.WaveformUpdate;
import org.mdpnp.devices.nonin.pulseox.DemoPulseOx;
import org.mdpnp.guis.waveform.NumericUpdateWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.guis.waveform.swing.GLWaveformPanel;
import org.mdpnp.messaging.Gateway;
import org.mdpnp.nomenclature.ConnectedDevice;
import org.mdpnp.nomenclature.PulseOximeter;
import org.mdpnp.nomenclature.ConnectedDevice.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jogamp.opengl.util.FPSAnimator;

@SuppressWarnings("serial")
public class PulseOximeterPanel extends DevicePanel {
	
	
	private JLabel spo2, heartrate, spo2Label, heartrateLabel, nameLabel, guidLabel;
	private JLabel spo2Low, spo2Up, heartrateLow, heartrateUp;
	private JPanel spo2Bounds, heartrateBounds;
	private JPanel spo2Panel, heartratePanel;
	private WaveformPanel pulsePanel;
	private WaveformPanel plethPanel;
	private JLabel time, connected;
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public void setName(String name) {
		if(built && name != null) {
			this.nameLabel.setText(name);
		}
	}
	
	public void setGuid(String guid) {
		if(built && guid != null) {
			this.guidLabel.setText(guid);
		}
	}

	
	protected void buildComponents() {
		spo2Bounds = new JPanel();
		spo2Bounds.setOpaque(false);
		spo2Bounds.setLayout(new GridLayout(3, 1));
		spo2Bounds.add(spo2Up = new JLabel("--"));
		spo2Bounds.add(spo2Low = new JLabel("--"));
		spo2Bounds.add(spo2Label = new JLabel("%SpO\u2082"));
		spo2Up.setVisible(false);
		spo2Low.setVisible(false);
		
		
		spo2Panel = new JPanel();
		spo2Panel.setOpaque(false);
		spo2Panel.setLayout(new BorderLayout());
		spo2Panel.add(spo2 = new JLabel("----"), BorderLayout.CENTER);
		spo2.setHorizontalAlignment(JLabel.RIGHT);
		spo2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		spo2Panel.add(spo2Bounds, BorderLayout.EAST);
		
		heartrateBounds = new JPanel();
		heartrateBounds.setOpaque(false);
		heartrateBounds.setLayout(new GridLayout(3,1));
		heartrateBounds.add(heartrateUp = new JLabel("--"));
		heartrateBounds.add(heartrateLow = new JLabel("--"));
		heartrateBounds.add(heartrateLabel = new JLabel("BPM"));
		heartrateUp.setVisible(false);
		heartrateLow.setVisible(false);
		
		heartratePanel = new JPanel();
		heartratePanel.setOpaque(false);
		heartratePanel.setLayout(new BorderLayout());
		heartratePanel.add(heartrate = new JLabel("----"), BorderLayout.CENTER);
		heartrate.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		heartrate.setHorizontalAlignment(JLabel.RIGHT);
		heartratePanel.add(heartrateBounds, BorderLayout.EAST);
		
		SpaceFillLabel.attachResizeFontToFill(this, spo2, heartrate);
		
		WaveformPanelFactory fact = new WaveformPanelFactory();
		
		plethPanel = fact.createWaveformPanel();
		pulsePanel = fact.createWaveformPanel();
		
		JPanel upper = new JPanel();
		upper.setOpaque(false);
		upper.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);

		upper.add(plethPanel.asComponent(), gbc);
		
		gbc.gridy = 1;
		upper.add(pulsePanel.asComponent(), gbc);
		
		gbc.gridy = 0;
		gbc.weightx = 0.1;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridx = 1;

		upper.add(spo2Panel, gbc);
		gbc.gridy = 1;
		upper.add(heartratePanel, gbc);
		
		setLayout(new BorderLayout());
		add(upper, BorderLayout.CENTER);
		
		JPanel lower = new JPanel();
		lower.setOpaque(false);
		lower.setLayout(new GridLayout(2, 1));
		
		
		lower.add(time = new JLabel("TIME"));
		time.setHorizontalAlignment(JLabel.RIGHT);
		lower.add(connected = new JLabel("ConnectState"));
		connected.setHorizontalAlignment(JLabel.RIGHT);
		add(lower, BorderLayout.SOUTH);
		
		
		JPanel headers = new JPanel();
		headers.setLayout(new GridLayout(2,1));
		headers.setOpaque(false);
		headers.add(nameLabel = new JLabel("NAME"));
		headers.add(guidLabel = new JLabel("GUID"));
		add(headers, BorderLayout.NORTH);
	
		nameLabel.setHorizontalAlignment(JLabel.RIGHT);
		guidLabel.setHorizontalAlignment(JLabel.RIGHT);
		if(plethPanel instanceof GLWaveformPanel) {
			((GLWaveformPanel)plethPanel).setAnimator(new FPSAnimator((GLAutoDrawable) plethPanel, FPSAnimator.DEFAULT_FRAMES_PER_INTERVAL));
			((GLWaveformPanel)plethPanel).getAnimator().start();
		}
		
		if(pulsePanel instanceof GLWaveformPanel) {
			((GLWaveformPanel)pulsePanel).setAnimator(new FPSAnimator((GLAutoDrawable) pulsePanel, FPSAnimator.DEFAULT_FRAMES_PER_INTERVAL));
			((GLWaveformPanel)pulsePanel).getAnimator().start();
		}
		
		setForeground(Color.green);
		setBackground(Color.black);
	}
	
	

	private boolean built = false;
	public PulseOximeterPanel(Gateway gateway, String source) {
		super(gateway, source);
		buildComponents();
		plethPanel.setSource(plethWave);
		
		pulsePanel.setSource(pulseWave);
		pulsePanel.cachingSource().setFixedTimeDomain(120000L);
		
		built = true;
		
		registerAndRequestRequiredIdentifiedUpdates();

	}
	
	@Override
	public Collection<Identifier> requiredIdentifiedUpdates() {
		List<Identifier> ids = new ArrayList<Identifier>(super.requiredIdentifiedUpdates());
		ids.addAll(Arrays.asList(new Identifier[] {ConnectedDevice.STATE, ConnectedDevice.CONNECTION_INFO, PulseOximeter.PULSE, PulseOximeter.SPO2, PulseOximeter.PLETH}));
		return ids;
	}
	
	private final WaveformUpdateWaveformSource plethWave = new WaveformUpdateWaveformSource();
	private final NumericUpdateWaveformSource pulseWave = new NumericUpdateWaveformSource(333L);
	

	private ConnectedDevice.State connectedState;
	private String connectionInfo;
	
	@Override
	public void destroy() {
		if(plethPanel instanceof GLWaveformPanel) {
			GLWaveformPanel plethPanel = (GLWaveformPanel) this.plethPanel;
			plethPanel.getAnimator().stop();
			plethPanel.getAnimator().remove(plethPanel);
		}

		if(pulsePanel instanceof GLWaveformPanel) {
			GLWaveformPanel pulsePanel = (GLWaveformPanel) this.pulsePanel;
			pulsePanel.getAnimator().stop();
			pulsePanel.getAnimator().remove(pulsePanel);			
		}
		

		super.destroy();
	}
	
	private final void setInt(IdentifiableUpdate<?> nu, Numeric numeric, JLabel label, String def) {
		if(numeric.equals(nu.getIdentifier())) {
			
			setInt(((NumericUpdate)nu).getValue(), label, def);
			if(!label.isVisible()) {
				label.setVisible(true);
			}
		}
	}
	
	
	
	@Override
	protected void doUpdate(IdentifiableUpdate<?> n) {
		if(!built) {
			return;
		}
		if(null == n) {
			log.warn("null update ");
			return;
		}
		setInt(n, PulseOximeter.PULSE, this.heartrate, "---");
		setInt(n, PulseOximeter.SPO2, this.spo2, "---");
		setInt(n, PulseOximeter.PULSE_LOWER, this.heartrateLow, "--");
		setInt(n, PulseOximeter.PULSE_UPPER, this.heartrateUp, "--");
		setInt(n, PulseOximeter.SPO2_LOWER, this.spo2Low, "--");
		setInt(n, PulseOximeter.SPO2_UPPER, this.spo2Up, "--");
		if(PulseOximeter.PULSE.equals(n.getIdentifier())) {
			Date date = ((NumericUpdate)n).getUpdateTime();
			this.time.setText(null == date ? "---" : dateFormat.format(date));
		}
		if(PulseOximeter.PLETH.equals(n.getIdentifier())) {
			WaveformUpdate wu = (WaveformUpdate) n;
			plethWave.applyUpdate(wu);
		} else if(ConnectedDevice.STATE.equals(n.getIdentifier())) {
			connectedState = (State) ((EnumerationUpdate)n).getValue();
			connected.setText(""+connectedState+(null==connectionInfo?"":(" ("+connectionInfo+")")));
			plethWave.reset();
			pulseWave.reset();
		} else if(ConnectedDevice.CONNECTION_INFO.equals(n.getIdentifier())) {
			connectionInfo = ((TextUpdate)n).getValue();
			connected.setText(""+connectedState+(null==connectionInfo?"":(" ("+connectionInfo+")")));
		} else if(PulseOximeter.PULSE.equals(n.getIdentifier())) {
			NumericUpdate nu = (NumericUpdate) n;
			pulseWave.applyUpdate(nu);
		} else if(DemoPulseOx.OUT_OF_TRACK.equals(n.getIdentifier())) {
			EnumerationUpdate eu = (EnumerationUpdate) n;
			
			DemoPulseOx.Bool bool = (DemoPulseOx.Bool) eu.getValue();
			if(null == bool) {
				log.warn("Received null OUT_OF_TRACK");
				return;
			} else {
				switch(bool) {
				case False:
					plethPanel.setOutOfTrack(false);
					break;
				case True:
					plethPanel.setOutOfTrack(true);
					break;
				}
			}
		}
	}
	public static boolean supported(Set<Identifier> identifiers) {
		return identifiers.contains(PulseOximeter.SPO2);
	}
	private static final Logger log = LoggerFactory.getLogger(PulseOximeterPanel.class);
	@Override
	public void setIcon(Image image) {
		log.trace("setIcon");
		if(built && null != image) {
			nameLabel.setOpaque(false);
			nameLabel.setIcon(new ImageIcon(image));
		}
	}
}
