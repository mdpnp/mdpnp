/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
        heartrateBounds.setLayout(new GridLayout(3, 1));
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

        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
                0, 0);

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

        setForeground(Color.cyan);
        setBackground(Color.black);
        setOpaque(true);
    }

    public PulseOximeterPanel() {
        setBackground(Color.black);
        setOpaque(true);
        buildComponents();
        plethPanel.setSource(plethWave);

        pulsePanel.setSource(pulseWave);
        if (null != pulsePanel.cachingSource()) {
            pulsePanel.cachingSource().setFixedTimeDomain(120000L);
        }

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

    public static boolean supported(Set<String> names) {
        return names.contains(rosetta.MDC_PULS_OXIM_SAT_O2.VALUE) && names.contains(rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE);// &&
        // names.contains(ice.Physio._MDC_PULS_OXIM_PLETH);
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PulseOximeterPanel.class);
    private final Date date = new Date();

    @Override
    public void numeric(Numeric numeric, String metric_id, SampleInfo sampleInfo) {
        if (aliveAndValidData(sampleInfo)) {
            setInt(numeric, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, spo2, null);
            setInt(numeric, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, heartrate, null);
            if (rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(metric_id)) {
                pulseWave.applyUpdate(numeric, sampleInfo);
            }
            date.setTime(1000L * sampleInfo.source_timestamp.sec + sampleInfo.source_timestamp.nanosec / 1000000L);
            time.setText(dateFormat.format(date));
        } else {
            if (rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(metric_id)) {
                pulseWave.reset();
            }
        }
    }

    @Override
    public void sampleArray(SampleArray sampleArray, String metric_id, SampleInfo sampleInfo) {
        if (aliveAndValidData(sampleInfo)) {
            if (rosetta.MDC_PULS_OXIM_PLETH.VALUE.equals(metric_id)) {
                plethWave.applyUpdate(sampleArray, sampleInfo);
            }
            date.setTime(1000L * sampleInfo.source_timestamp.sec + sampleInfo.source_timestamp.nanosec / 1000000L);
            time.setText(dateFormat.format(date));
        } else {
            if (rosetta.MDC_PULS_OXIM_PLETH.VALUE.equals(metric_id)) {
                plethWave.reset();
            }
        }
    }

    @Override
    public void infusionStatus(InfusionStatus infusionStatus, SampleInfo sampleInfo) {

    }

    @Override
    public void connected() {
        plethWave.reset();
        pulseWave.reset();
    }
}
