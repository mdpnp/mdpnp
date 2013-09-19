/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.swing;

import ice.InfusionStatus;
import ice.Numeric;
import ice.SampleArray;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.mdpnp.guis.waveform.NumericUpdateWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.SampleInfo;

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
        spo2Bounds.add(spo2Label = new JLabel("%"));
        spo2Up.setVisible(false);
        spo2Low.setVisible(false);


        spo2Panel = new JPanel();
        spo2Panel.setOpaque(false);
        spo2Panel.setLayout(new BorderLayout());
        spo2Panel.add(new JLabel("SpO\u2082"), BorderLayout.NORTH);
        spo2Panel.add(spo2 = new JLabel("----"), BorderLayout.CENTER);
        spo2.setHorizontalAlignment(JLabel.RIGHT);

        spo2.setBorder(new EmptyBorder(5, 5, 5, 5));
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
        heartratePanel.add(new JLabel("Pulse Rate"), BorderLayout.NORTH);
        heartratePanel.add(heartrate = new JLabel("----"), BorderLayout.CENTER);
        heartrate.setBorder(new EmptyBorder(5, 5, 5, 5));
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

        upper.add(label("Plethysmogram", plethPanel.asComponent()), gbc);

        gbc.gridy = 1;
        upper.add(label("Pulse Rate", pulsePanel.asComponent()), gbc);

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

        add(label("Last Sample: ", time = new JLabel("TIME"), BorderLayout.WEST), BorderLayout.SOUTH);

        setForeground(Color.green);
        setBackground(Color.black);
        setOpaque(true);
    }

    public PulseOximeterPanel() {
        setBackground(Color.black);
        setOpaque(true);
        buildComponents();
        plethPanel.setSource(plethWave);

        pulsePanel.setSource(pulseWave);
        pulsePanel.cachingSource().setFixedTimeDomain(120000L);

        plethPanel.start();
        pulsePanel.start();
    }

    private final WaveformUpdateWaveformSource plethWave = new WaveformUpdateWaveformSource();
    private final NumericUpdateWaveformSource pulseWave = new NumericUpdateWaveformSource(333L);

    @Override
    public void destroy() {
        plethPanel.setSource(null);
        pulsePanel.setSource(null);
        plethPanel.stop();
        pulsePanel.stop();
        super.destroy();
    }

    public static boolean supported(Set<Integer> names) {
        return names.contains(ice.Physio._MDC_PULS_OXIM_SAT_O2) &&
               names.contains(ice.Physio._MDC_PULS_OXIM_PULS_RATE);// &&
//	           names.contains(ice.Physio._MDC_PULS_OXIM_PLETH);
    }
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PulseOximeterPanel.class);
    private final Date date = new Date();
    @Override
    public void numeric(Numeric numeric, SampleInfo sampleInfo) {
        setInt(numeric, ice.Physio._MDC_PULS_OXIM_SAT_O2, spo2, null);
        setInt(numeric, ice.Physio._MDC_PULS_OXIM_PULS_RATE, heartrate, null);
        if(ice.Physio._MDC_PULS_OXIM_PULS_RATE == numeric.name) {
            pulseWave.applyUpdate(numeric);
        }
        date.setTime(1000L*sampleInfo.source_timestamp.sec+sampleInfo.source_timestamp.nanosec/1000000L);
        time.setText(dateFormat.format(date));
    }

    @Override
    public void sampleArray(SampleArray sampleArray, SampleInfo sampleInfo) {
        switch(sampleArray.name) {
        case ice.Physio._MDC_PULS_OXIM_PLETH:
            plethWave.applyUpdate(sampleArray);
            break;
        }
        date.setTime(1000L*sampleInfo.source_timestamp.sec+sampleInfo.source_timestamp.nanosec/1000000L);
        time.setText(dateFormat.format(date));
    }

    @Override
    public void infusionStatus(InfusionStatus infusionStatus, SampleInfo sampleInfo) {

    }
}
