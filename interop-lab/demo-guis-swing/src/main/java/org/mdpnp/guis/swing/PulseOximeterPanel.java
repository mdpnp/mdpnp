/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.swing;

import ice.Numeric;
import ice.NumericDataReader;
import ice.NumericSeq;
import ice.NumericTypeSupport;
import ice.SampleArray;
import ice.SampleArrayDataReader;
import ice.SampleArraySeq;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.guis.waveform.NumericUpdateWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.guis.waveform.swing.GLWaveformPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jogamp.opengl.util.FPSAnimator;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.QueryCondition;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.TopicDescription;

@SuppressWarnings("serial")
public class PulseOximeterPanel extends DevicePanel {
	
	
	private JLabel spo2, heartrate, spo2Label, heartrateLabel;
	private JLabel spo2Low, spo2Up, heartrateLow, heartrateUp;
	private JPanel spo2Bounds, heartrateBounds;
	private JPanel spo2Panel, heartratePanel;
	private WaveformPanel pulsePanel;
	private WaveformPanel plethPanel;
	private JLabel time;
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
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

		add(time = new JLabel("TIME"), BorderLayout.SOUTH);
		
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

	public PulseOximeterPanel() {
		buildComponents();
		plethPanel.setSource(plethWave);
		
		pulsePanel.setSource(pulseWave);
		pulsePanel.cachingSource().setFixedTimeDomain(120000L);
		
	}
	
	private final WaveformUpdateWaveformSource plethWave = new WaveformUpdateWaveformSource();
	private final NumericUpdateWaveformSource pulseWave = new NumericUpdateWaveformSource(333L);
	
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


	
	public static boolean supported(Set<Integer> names) {
	    return names.contains(ice.MDC_PULS_OXIM_SAT_O2.VALUE) &&
	           names.contains(ice.MDC_PULS_OXIM_PULS_RATE.VALUE) && 
	           names.contains(ice.MDC_PULS_OXIM_PLETH.VALUE);
	}
	private static final Logger log = LoggerFactory.getLogger(PulseOximeterPanel.class);

    @Override
    public void numeric(Numeric numeric, SampleInfo sampleInfo) {
        setInt(numeric, ice.MDC_PULS_OXIM_SAT_O2.VALUE, spo2, null);
        setInt(numeric, ice.MDC_PULS_OXIM_PULS_RATE.VALUE, heartrate, null);
        if(ice.MDC_PULS_OXIM_PULS_RATE.VALUE == numeric.name) {
            pulseWave.applyUpdate(numeric);
        }
    }

    @Override
    public void sampleArray(SampleArray sampleArray, SampleInfo sampleInfo) {
        switch(sampleArray.name) {
        case ice.MDC_PULS_OXIM_PLETH.VALUE:
            plethWave.applyUpdate(sampleArray);
            break;
        }
        
    }
}
