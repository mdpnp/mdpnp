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
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.SampleInfo;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class VentilatorPanel extends DevicePanel {
    private WaveformPanel flowPanel, pressurePanel, co2Panel;
    private final JLabel time = new JLabel(" "), respiratoryRate = new JLabel(" "), endTidalCO2 = new JLabel(" ");
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void destroy() {
        flowPanel.stop();
        pressurePanel.stop();
        co2Panel.stop();
        super.destroy();
    }

    protected void buildComponents() {
        WaveformPanelFactory fact = new WaveformPanelFactory();
        flowPanel = fact.createWaveformPanel();
        pressurePanel = fact.createWaveformPanel();
        co2Panel = fact.createWaveformPanel();

        JPanel waves = new JPanel(new GridLayout(3, 1));
        waves.setOpaque(false);

        waves.add(label("Flow", flowPanel.asComponent()));
        waves.add(label("Pressure", pressurePanel.asComponent()));
        waves.add(label("CO\u2082", co2Panel.asComponent()));

        add(waves, BorderLayout.CENTER);

        add(label("Last Sample: ", time, BorderLayout.WEST), BorderLayout.SOUTH);

        SpaceFillLabel.attachResizeFontToFill(this, endTidalCO2, respiratoryRate);

        JPanel numerics = new JPanel(new GridLayout(2, 1));
        SpaceFillLabel.attachResizeFontToFill(this, endTidalCO2, respiratoryRate);
        JPanel t;
        numerics.add(t = label("etCO\u2082", endTidalCO2));
        t.add(new JLabel(" "), BorderLayout.EAST);
        numerics.add(t = label("RespiratoryRate", respiratoryRate));
        t.add(new JLabel("BPM"), BorderLayout.EAST);
        add(numerics, BorderLayout.EAST);

        add(numerics, BorderLayout.EAST);

        flowPanel.setSource(flowWave);
        pressurePanel.setSource(pressureWave);
        co2Panel.setSource(etco2Wave);

        flowPanel.start();
        pressurePanel.start();
        co2Panel.start();

        setForeground(Color.white);
        setBackground(Color.black);
        setOpaque(true);

    }

    public VentilatorPanel() {
        super(new BorderLayout());
        buildComponents();

    }

    private final WaveformUpdateWaveformSource flowWave = new WaveformUpdateWaveformSource();
    private final WaveformUpdateWaveformSource pressureWave = new WaveformUpdateWaveformSource();
    private final WaveformUpdateWaveformSource etco2Wave = new WaveformUpdateWaveformSource();

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(VentilatorPanel.class);

    public static boolean supported(Set<String> identifiers) {
        return identifiers.contains(rosetta.MDC_PRESS_AWAY.VALUE) || identifiers.contains(ice.MDC_CAPNOGRAPH.VALUE);
    }

    @Override
    public void numeric(Numeric numeric, String metric_id, SampleInfo sampleInfo) {
        if (aliveAndValidData(sampleInfo)) {
            if (rosetta.MDC_RESP_RATE.VALUE.equals(metric_id)) {
                respiratoryRate.setText(Integer.toString((int) numeric.value));
            } else if (rosetta.MDC_AWAY_CO2_EXP.VALUE.equals(metric_id)) {
                endTidalCO2.setText(Integer.toString((int) numeric.value));
            }
        }
    }

    private final Date date = new Date();

    @Override
    public void sampleArray(SampleArray sampleArray, String metric_id, SampleInfo sampleInfo) {
        if (aliveAndValidData(sampleInfo)) {
            if (rosetta.MDC_FLOW_AWAY.VALUE.equals(metric_id)) {
                flowWave.applyUpdate(sampleArray, sampleInfo);
            } else if (rosetta.MDC_PRESS_AWAY.VALUE.equals(metric_id)) {
                pressureWave.applyUpdate(sampleArray, sampleInfo);
            } else if (ice.MDC_CAPNOGRAPH.VALUE.equals(metric_id)) {
                etco2Wave.applyUpdate(sampleArray, sampleInfo);
            }
            date.setTime(sampleInfo.source_timestamp.sec * 1000L + sampleInfo.source_timestamp.nanosec / 1000000L);

            time.setText(dateFormat.format(date));
        } else {
            if (rosetta.MDC_FLOW_AWAY.VALUE.equals(metric_id)) {
                flowWave.reset();
            } else if (rosetta.MDC_PRESS_AWAY.VALUE.equals(metric_id)) {
                pressureWave.reset();
            } else if (ice.MDC_CAPNOGRAPH.VALUE.equals(metric_id)) {
                etco2Wave.reset();
            }
        }
    }

    @Override
    public void infusionStatus(InfusionStatus infusionStatus, SampleInfo sampleInfo) {

    }

    @Override
    public void connected() {
        etco2Wave.reset();
        flowWave.reset();
        pressureWave.reset();
    }

}
