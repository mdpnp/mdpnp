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
package org.mdpnp.guis.javafx;

import ice.SampleArray;
import ice.SampleArrayDataReader;

import java.util.Set;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformPane;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.SampleInfo;

/**
 * @author Jeff Plourde
 *
 */
public class MultiPulseOximeterPanel extends DevicePanel {
    private WaveformPanel[] plethPanel;
    private final SampleArrayWaveformSource[] plethWave;
    private final GridPane gridPane = new GridPane();

    private static final int N = 12;

    protected void buildComponents() {
         WaveformPanelFactory fact = new WaveformPanelFactory();
        this.plethPanel = new WaveformPanel[N];
        setCenter(gridPane);
        for (int i = 0; i < N; i++) {
             plethPanel[i] = fact.createWaveformPanel();
             ((JavaFXWaveformPane)plethPanel[i]).getCanvas().getGraphicsContext2D().setStroke(Color.CYAN);
        }
    }

    public MultiPulseOximeterPanel() {
        getStyleClass().add("multi-pulse-oximeter-panel");
        buildComponents();
        plethWave = new SampleArrayWaveformSource[N];
        for (int i = 0; i < N; i++) {
            plethPanel[i].start();
        }
    }

    @Override
    public void destroy() {
        for (int i = 0; i < N; i++) {
            plethPanel[i].setSource(null);
            plethPanel[i].stop();
        }
        deviceMonitor.getSampleArrayModel().removeListener(sampleArrayListener);
        super.destroy();
    }

    public static boolean supported(Set<String> names) {
        return names.contains(rosetta.MDC_PULS_OXIM_PLETH.VALUE);
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PulseOximeterPanel.class);

    @Override
    public void set(DeviceDataMonitor deviceMonitor) {
        super.set(deviceMonitor);
        deviceMonitor.getSampleArrayModel().iterateAndAddListener(sampleArrayListener);
    }
    
    private final InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader> sampleArrayListener = new InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
                SampleInfo sampleInfo) {
            if (data.instance_id >= 0 && data.instance_id < N) {
                plethPanel[data.instance_id].setSource(new SampleArrayWaveformSource(reader, data));
          }
        }
        
        @Override
        public void instanceNotAlive(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray keyHolder,
                SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceSample(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
                SampleInfo sampleInfo) {
        }
        
    };


}
