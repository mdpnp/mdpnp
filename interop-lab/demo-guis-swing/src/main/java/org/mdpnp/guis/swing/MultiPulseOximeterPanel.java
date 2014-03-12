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

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Set;

import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.guis.waveform.swing.SwingWaveformPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.SampleInfo;

@SuppressWarnings("serial")
public class MultiPulseOximeterPanel extends DevicePanel {
    private WaveformPanel[] plethPanel;
    private final WaveformUpdateWaveformSource[] plethWave;

    private static final int N = 12;

    protected void buildComponents() {
        // WaveformPanelFactory fact = new WaveformPanelFactory();
        this.plethPanel = new WaveformPanel[N];
        setLayout(new GridLayout(N, 1));
        for (int i = 0; i < N; i++) {
            // plethPanel[i] = fact.createWaveformPanel();
            plethPanel[i] = new SwingWaveformPanel();
        }

        setForeground(Color.green);
        setBackground(Color.black);
        setOpaque(true);
    }

    public MultiPulseOximeterPanel() {
        setBackground(Color.black);
        setOpaque(true);
        buildComponents();
        plethWave = new WaveformUpdateWaveformSource[N];
        for (int i = 0; i < N; i++) {
            plethWave[i] = new WaveformUpdateWaveformSource();
            plethPanel[i].setSource(plethWave[i]);
            plethPanel[i].start();
        }
    }

    @Override
    public void destroy() {
        for (int i = 0; i < N; i++) {
            plethPanel[i].setSource(null);
            plethPanel[i].stop();
        }
        super.destroy();
    }

    public static boolean supported(Set<String> names) {
        return names.contains(rosetta.MDC_PULS_OXIM_PLETH.VALUE);
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PulseOximeterPanel.class);

    // private final Date date = new Date();

    @Override
    public void sampleArray(SampleArray sampleArray, String metric_id, SampleInfo sampleInfo) {
        if (aliveAndValidData(sampleInfo)) {
            if (sampleArray.instance_id >= 0 && sampleArray.instance_id < N) {
                plethWave[sampleArray.instance_id].applyUpdate(sampleArray, sampleInfo);
            }
        } else {
            if (sampleArray.instance_id >= 0 && sampleArray.instance_id < N) {
                plethWave[sampleArray.instance_id].reset();
            }
        }
    }

    @Override
    public void infusionStatus(InfusionStatus infusionStatus, SampleInfo sampleInfo) {

    }

    @Override
    public void connected() {
        for (WaveformUpdateWaveformSource wuws : plethWave) {
            wuws.reset();
        }
    }

    @Override
    public void numeric(Numeric numeric, String metric_id, SampleInfo sampleInfo) {
        // NO OP
    }
}
