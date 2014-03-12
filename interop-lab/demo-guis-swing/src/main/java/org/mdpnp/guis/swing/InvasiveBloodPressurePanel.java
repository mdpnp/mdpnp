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
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;

import com.rti.dds.subscription.SampleInfo;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class InvasiveBloodPressurePanel extends DevicePanel {

    private final WaveformPanel[] panel;
    private final Date date = new Date();
    private final JLabel time = new JLabel(" "); // , heartRate = new
                                                 // JLabel(" "), respiratoryRate
                                                 // = new JLabel(" ");
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final static String[] WAVEFORMS = new String[] { rosetta.MDC_PRESS_BLD_ART.VALUE, rosetta.MDC_PRESS_BLD_ART_ABP.VALUE, };

    private final static String[] LABELS = new String[] { "ART", "ABP", };

    private final Map<String, WaveformUpdateWaveformSource> panelMap = new HashMap<String, WaveformUpdateWaveformSource>();

    public InvasiveBloodPressurePanel() {
        super(new BorderLayout());
        add(label("Last Sample: ", time, BorderLayout.WEST), BorderLayout.SOUTH);

        JPanel waves = new JPanel(new GridLayout(WAVEFORMS.length, 1));
        WaveformPanelFactory fact = new WaveformPanelFactory();
        panel = new WaveformPanel[WAVEFORMS.length];
        for (int i = 0; i < panel.length; i++) {
            waves.add(label(LABELS[i], (panel[i] = fact.createWaveformPanel()).asComponent())/*
                                                                                              * ,
                                                                                              * gbc
                                                                                              */);
            WaveformUpdateWaveformSource wuws = new WaveformUpdateWaveformSource();
            panel[i].setSource(wuws);
            panelMap.put(WAVEFORMS[i], wuws);
            panel[i].start();
        }
        add(waves, BorderLayout.CENTER);

        // JPanel numerics = new JPanel(new GridLayout(2, 1));
        // SpaceFillLabel.attachResizeFontToFill(this, heartRate,
        // respiratoryRate);
        // JPanel t;
        // numerics.add(t = label("Heart Rate", heartRate));
        // t.add(new JLabel("BPM"), BorderLayout.EAST);
        // numerics.add(t = label("RespiratoryRate", respiratoryRate));
        // t.add(new JLabel("BPM"), BorderLayout.EAST);
        // add(numerics, BorderLayout.EAST);

        setForeground(Color.red);
        setBackground(Color.black);
        setOpaque(true);
    }

    @Override
    public void destroy() {
        for (WaveformPanel wp : panel) {
            wp.stop();
        }
        super.destroy();
    }

    public static boolean supported(Set<String> identifiers) {
        for (String w : WAVEFORMS) {
            if (identifiers.contains(w)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void numeric(Numeric numeric, String metric_id, SampleInfo sampleInfo) {
        if (aliveAndValidData(sampleInfo)) {
            // if(rosetta.MDC_RESP_RATE.VALUE.equals(metric_id)) {
            // respiratoryRate.setText(Integer.toString((int)numeric.value));
            // } else if(rosetta.MDC_ECG_CARD_BEAT_RATE.VALUE.equals(metric_id))
            // {
            // heartRate.setText(Integer.toString((int)numeric.value));
            // }
        }
    }

    @Override
    public void sampleArray(SampleArray sampleArray, String metric_id, SampleInfo sampleInfo) {
        WaveformUpdateWaveformSource wuws = panelMap.get(metric_id);
        if (aliveAndValidData(sampleInfo)) {
            if (null != wuws) {
                wuws.applyUpdate(sampleArray, sampleInfo);
            }
            date.setTime(sampleInfo.source_timestamp.sec * 1000L + sampleInfo.source_timestamp.nanosec / 1000000L);
            time.setText(dateFormat.format(date));
        } else {
            if (null != wuws) {
                System.err.println("RESET RESET RESET");
                wuws.reset();
            }
        }
    }

    @Override
    public void infusionStatus(InfusionStatus infusionStatus, SampleInfo sampleInfo) {

    }

    @Override
    public void connected() {
        for (WaveformUpdateWaveformSource wuws : panelMap.values()) {
            wuws.reset();
        }
    }
}
