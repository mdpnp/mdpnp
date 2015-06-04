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

import ice.Numeric;
import ice.NumericDataReader;
import ice.SampleArray;
import ice.SampleArrayDataReader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformPane;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;

import com.rti.dds.subscription.SampleInfo;

/**
 * @author Jeff Plourde
 *
 */
public class ElectroCardioGramPanel extends DevicePanel {

    private final Date date = new Date();
    private final Label time = new Label(" "), heartRate = new Label(" "), respiratoryRate = new Label(" ");
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final static String[] ECG_WAVEFORMS = new String[] { ice.MDC_ECG_LEAD_I.VALUE, ice.MDC_ECG_LEAD_II.VALUE,
            ice.MDC_ECG_LEAD_III.VALUE };

    @SuppressWarnings("unused")
    private final static String[] ECG_LABELS = new String[] { "ECG LEAD I", "ECG LEAD II", "ECG LEAD III", "ECG LEAD AVF", "ECG LEAD AVL",
            "ECG LEAD AVR" };

    private final static Set<String> ECG_WAVEFORMS_SET = new HashSet<String>();
    static {
        ECG_WAVEFORMS_SET.addAll(Arrays.asList(ECG_WAVEFORMS));
    }
    
    private final Map<String, WaveformPanel> panelMap = new HashMap<String, WaveformPanel>();
    private final GridPane waves = new GridPane();
    
    public ElectroCardioGramPanel() {
        getStyleClass().add("electro-cardiogram-panel");
        
        setBottom(labelLeft("Last Sample: ", time));
        setCenter(waves);

        GridPane numerics = new GridPane();
        BorderPane t;
        numerics.add(t = label("Heart Rate", heartRate), 0, 0);
        t.setRight(new Label("BPM"));
        numerics.add(t = label("RespiratoryRate", respiratoryRate), 0, 1);
        t.setRight(new Label("BPM"));
        setRight(numerics);
    }

    @Override
    public void destroy() {
        for (WaveformPanel wp : panelMap.values()) {
            wp.stop();
        }
        deviceMonitor.getNumericModel().removeListener(numericListener);
        deviceMonitor.getSampleArrayModel().removeListener(sampleArrayListener);
        super.destroy();
    }

    public static boolean supported(Set<String> identifiers) {
        for (String w : ECG_WAVEFORMS) {
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
            final String str = Integer.toString((int)data.value);
            

            if (rosetta.MDC_TTHOR_RESP_RATE.VALUE.equals(data.metric_id)) {
                Platform.runLater( () -> {respiratoryRate.setText(str);});
            } else if (rosetta.MDC_ECG_HEART_RATE.VALUE.equals(data.metric_id)) {
                Platform.runLater( () -> {heartRate.setText(str);});
            }
        }
        
    };
    
    public void set(org.mdpnp.rtiapi.data.DeviceDataMonitor deviceMonitor) {
        super.set(deviceMonitor);
        deviceMonitor.getNumericModel().iterateAndAddListener(numericListener);
        deviceMonitor.getSampleArrayModel().iterateAndAddListener(sampleArrayListener);
    };
    
    private final InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader> sampleArrayListener = new InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
                SampleInfo sampleInfo) {
            if(ECG_WAVEFORMS_SET.contains(data.metric_id)) {
                WaveformPanel wuws = panelMap.get(data.metric_id);
                if (null == wuws) {
                    SampleArrayWaveformSource saws = new SampleArrayWaveformSource(reader, data);
                    wuws = new WaveformPanelFactory().createWaveformPanel();
                    wuws.setSource(saws);
                    final WaveformPanel _wuws = wuws;
                    final int idx = panelMap.size();
                    panelMap.put(data.metric_id, wuws);
                    ((JavaFXWaveformPane)_wuws).getCanvas().getGraphicsContext2D().setStroke(Color.GREEN);
                    Platform.runLater(new Runnable() {
                        public void run() {
                            Node x = (Node) _wuws;
                            GridPane.setVgrow(x, Priority.ALWAYS);
                            GridPane.setHgrow(x, Priority.ALWAYS);
                            waves.add(x, 0, idx);
                        }
                    });
                    wuws.start();
                }
                
            }
        }

        @Override
        public void instanceNotAlive(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray keyHolder,
                SampleInfo sampleInfo) {
        }

        @Override
        public void instanceSample(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
                SampleInfo sampleInfo) {
            if(sampleInfo.valid_data && ECG_WAVEFORMS_SET.contains(data.metric_id)) {
                date.setTime(data.presentation_time.sec * 1000L + data.presentation_time.nanosec / 1000000L);
                Platform.runLater(new Runnable() {
                    public void run() {
                        time.setText(dateFormat.format(date));
                    }
                });
            }
        } 
    };    
}
