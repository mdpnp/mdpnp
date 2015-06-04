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

import ice.Numeric;
import ice.NumericDataReader;
import ice.SampleArray;
import ice.SampleArrayDataReader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;

import com.rti.dds.subscription.SampleInfo;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class InvasiveBloodPressurePanel extends DevicePanel {

    private final WaveformPanel[] panels;
    private final Date date = new Date();
    private final JLabel time = new JLabel(" "); // , heartRate = new
                                                 // JLabel(" "), respiratoryRate
                                                 // = new JLabel(" ");
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final static String[] WAVEFORMS = new String[] { rosetta.MDC_PRESS_BLD.VALUE, rosetta.MDC_PRESS_BLD_ART.VALUE, rosetta.MDC_PRESS_BLD_ART_ABP.VALUE, };

    private final static String[] LABELS = new String[] { "IBP", "ART", "ABP", };

    private final Map<String, WaveformPanel> panelMap = new HashMap<String, WaveformPanel>();

    public InvasiveBloodPressurePanel() {
        super(new BorderLayout());
        add(label("Last Sample: ", time, BorderLayout.WEST), BorderLayout.SOUTH);

        JPanel waves = new JPanel(new GridLayout(WAVEFORMS.length, 1));
        WaveformPanelFactory fact = new WaveformPanelFactory();
        panels = new WaveformPanel[WAVEFORMS.length];
        for (int i = 0; i < panels.length; i++) {
            WaveformPanel panel = fact.createWaveformPanel();
            waves.add(label(LABELS[i], (Component) (panels[i] = panel)));

            panelMap.put(WAVEFORMS[i], panel);
            panels[i].start();
        }
        add(waves, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        int w = panel.getFontMetrics(panel.getFont()).stringWidth("RespiratoryRate");
        panel.setMinimumSize(new Dimension(w, panel.getMinimumSize().height));
        panel.setPreferredSize(panel.getMinimumSize());
        add(panel, BorderLayout.EAST);
        setForeground(Color.red);
        setBackground(Color.black);
        setOpaque(true);
    }

    @Override
    public void set(DeviceDataMonitor deviceMonitor) {
        super.set(deviceMonitor);
        deviceMonitor.getSampleArrayModel().iterateAndAddListener(sampleArrayListener);
        deviceMonitor.getNumericModel().iterateAndAddListener(numericListener);
    }
    
    @Override
    public void destroy() {
        for (WaveformPanel wp : panels) {
            wp.stop();
        }
        deviceMonitor.getSampleArrayModel().removeListener(sampleArrayListener);
        deviceMonitor.getNumericModel().removeListener(numericListener);
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
    private final InstanceModelListener<ice.Numeric, ice.NumericDataReader> numericListener = new InstanceModelListener<ice.Numeric, ice.NumericDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
        }

        @Override
        public void instanceNotAlive(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric keyHolder,
                SampleInfo sampleInfo) {
        }

        @Override
        public void instanceSample(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {

        }
        
    };
    private final InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader> sampleArrayListener = new InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
                SampleInfo sampleInfo) {
            WaveformPanel wuws = panelMap.get(data.metric_id);
            if(null != wuws) {
                if(null == wuws.getSource()) {
                    wuws.setSource(new SampleArrayWaveformSource(reader, data));
                }

                date.setTime(data.presentation_time.sec * 1000L + data.presentation_time.nanosec / 1000000L);
                time.setText(dateFormat.format(date));
          }
        }
        
        @Override
        public void instanceNotAlive(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray keyHolder,
                SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceSample(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
                SampleInfo sampleInfo) {
            if(sampleInfo.valid_data && panelMap.containsKey(data.metric_id)) {
                date.setTime(data.presentation_time.sec * 1000L + data.presentation_time.nanosec / 1000000L);
                time.setText(dateFormat.format(date));
            }
        }
        
    };



}
