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
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.mdpnp.guis.waveform.NumericWaveformSource;
import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.SampleInfo;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class PulseOximeterPanel extends DevicePanel {

    @SuppressWarnings("unused")
    private JLabel spo2, heartrate, spo2Label, heartrateLabel;
    private JLabel spo2Low, spo2Up, heartrateLow, heartrateUp;
    private JPanel spo2Bounds, heartrateBounds;
    private JPanel spo2Panel, heartratePanel;
    private WaveformPanel pulsePanel, plethPanel;
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
        spo2.setHorizontalTextPosition(SwingConstants.RIGHT);

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
        JLabel lbl;
        heartratePanel.add(lbl = new JLabel("Pulse Rate"), BorderLayout.NORTH);
        int w = lbl.getFontMetrics(lbl.getFont()).stringWidth("RespiratoryRate");
        lbl.setMinimumSize(new Dimension(w, lbl.getMinimumSize().height));
        lbl.setPreferredSize(lbl.getMinimumSize());
        heartratePanel.add(heartrate = new JLabel("----"), BorderLayout.CENTER);
        heartrate.setHorizontalTextPosition(SwingConstants.RIGHT);
        heartrate.setBorder(new EmptyBorder(5, 5, 5, 5));
        heartrate.setHorizontalAlignment(JLabel.RIGHT);
        heartratePanel.add(heartrateBounds, BorderLayout.EAST);

        SpaceFillLabel.attachResizeFontToFill(this, spo2, heartrate);

        WaveformPanelFactory fact = new WaveformPanelFactory();

        plethPanel = fact.createWaveformPanel();
        pulsePanel = fact.createWaveformPanel();

        JPanel upper = new JPanel(new GridLayout(2, 1));
        upper.setOpaque(false);
        upper.add(label("Plethysmogram", (Component) plethPanel));
        upper.add(label("Pulse Rate", (Component) pulsePanel));

        JPanel east = new JPanel(new GridLayout(2, 1));
        east.add(spo2Panel);
        east.add(heartratePanel);

        setLayout(new BorderLayout());
        add(upper, BorderLayout.CENTER);
        add(east, BorderLayout.EAST);

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
        
        plethPanel.start();
        pulsePanel.start();
    }

    private SampleArrayWaveformSource plethWave;
    private NumericWaveformSource pulseWave;

    @Override
    public void destroy() {
        plethPanel.setSource(null);
        pulsePanel.setSource(null);
        plethPanel.stop();
        pulsePanel.stop();
        
        if(deviceMonitor != null) {
            deviceMonitor.getNumericModel().removeListener(numericListener);
            deviceMonitor.getSampleArrayModel().removeListener(sampleArrayListener);
        }
        super.destroy();
    }

    public static boolean supported(Set<String> names) {
        return names.contains(rosetta.MDC_PULS_OXIM_SAT_O2.VALUE) && names.contains(rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE);// &&
        // names.contains(ice.Physio._MDC_PULS_OXIM_PLETH);
    }

    @Override
    public void set(DeviceDataMonitor deviceMonitor) {
        super.set(deviceMonitor);
        deviceMonitor.getNumericModel().iterateAndAddListener(numericListener);
        deviceMonitor.getSampleArrayModel().iterateAndAddListener(sampleArrayListener);
    }
    
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PulseOximeterPanel.class);
    private final Date date = new Date();
    
    private final InstanceModelListener<ice.Numeric, ice.NumericDataReader> numericListener = new InstanceModelListener<ice.Numeric, ice.NumericDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
            if (rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(data.metric_id)) {
                if(null == pulseWave) {
                    pulseWave = new NumericWaveformSource(model.getReader(), data);
                    pulsePanel.setSource(pulseWave);
                }
                date.setTime(1000L * data.presentation_time.sec + data.presentation_time.nanosec / 1000000L);
                time.setText(dateFormat.format(date));
            }
        }

        @Override
        public void instanceNotAlive(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric keyHolder,
                SampleInfo sampleInfo) {
        }

        @Override
        public void instanceSample(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
            setInt(data, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, spo2, null);
            setInt(data, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, heartrate, null);
            date.setTime(1000L * data.presentation_time.sec + data.presentation_time.nanosec / 1000000L);
            time.setText(dateFormat.format(date));
        }
        
    };
    
    private final InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader> sampleArrayListener = new InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
                SampleInfo sampleInfo) {
//            System.err.println(this+" I see you SampleArray:"+data.unique_device_identifier+" "+data.metric_id+" "+data.instance_id);
            if (rosetta.MDC_PULS_OXIM_PLETH.VALUE.equals(data.metric_id)) {
                if(null == plethWave) {
                    plethWave = new SampleArrayWaveformSource(deviceMonitor.getSampleArrayModel().getReader(), data);
                    plethPanel.setSource(plethWave);
                }
                date.setTime(1000L * data.presentation_time.sec + data.presentation_time.nanosec / 1000000L);
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
            
        }
        
    };
    

}
