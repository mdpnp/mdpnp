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
import java.util.Date;
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
import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;

/**
 * @author Jeff Plourde
 *
 */
public class VentilatorPanel extends DevicePanel {
    private WaveformPanel flowPanel, pressurePanel, co2Panel;
    private final Label time = new Label(" "), respiratoryRate = new Label(" "), endTidalCO2 = new Label(" ");
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void destroy() {
        flowPanel.stop();
        pressurePanel.stop();
        co2Panel.stop();
        flowPanel.setSource(null);
        pressurePanel.setSource(null);
        co2Panel.setSource(null);
        deviceMonitor.getNumericModel().removeListener(numericListener);
        deviceMonitor.getSampleArrayModel().removeListener(sampleArrayListener);

        super.destroy();
    }

    protected void buildComponents() {
        WaveformPanelFactory fact = new WaveformPanelFactory();
        flowPanel = fact.createWaveformPanel();
        pressurePanel = fact.createWaveformPanel();
        co2Panel = fact.createWaveformPanel();

        ((JavaFXWaveformPane)flowPanel).getCanvas().getGraphicsContext2D().setStroke(Color.WHITE);
        ((JavaFXWaveformPane)pressurePanel).getCanvas().getGraphicsContext2D().setStroke(Color.WHITE);
        ((JavaFXWaveformPane)co2Panel).getCanvas().getGraphicsContext2D().setStroke(Color.WHITE);
        
        
        GridPane waves = new GridPane();
        Node x = label("Flow", (Node) flowPanel);
        GridPane.setVgrow(x, Priority.ALWAYS);
        GridPane.setHgrow(x, Priority.ALWAYS);
        waves.add(x, 0, 0);
        
        x = label("Pressure", (Node) pressurePanel);
        GridPane.setVgrow(x, Priority.ALWAYS);
        GridPane.setHgrow(x, Priority.ALWAYS);
        waves.add(x, 0, 1);
        
        x = label("CO\u2082", (Node) co2Panel);
        GridPane.setVgrow(x, Priority.ALWAYS);
        GridPane.setHgrow(x, Priority.ALWAYS);
        waves.add(x, 0, 2);

        setCenter(waves);

        setBottom(labelLeft("Last Sample: ", time));

        GridPane numerics = new GridPane();
        BorderPane t;
        numerics.add(t = label("etCO\u2082", endTidalCO2), 0, 0);
        t.setRight(new Label(" "));
        numerics.add(t = label("RespiratoryRate", respiratoryRate), 0, 1);
        t.setRight(new Label("BPM"));
        setRight(numerics);

        flowPanel.start();
        pressurePanel.start();
        co2Panel.start();

    }

    public VentilatorPanel() {
        getStyleClass().add("ventilator-panel");
        buildComponents();

    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(VentilatorPanel.class);

    public static boolean supported(Set<String> identifiers) {
        return identifiers.contains(rosetta.MDC_PRESS_AWAY.VALUE) || identifiers.contains(rosetta.MDC_AWAY_CO2.VALUE);
    }

    @Override
    public void set(DeviceDataMonitor deviceMonitor) {
        super.set(deviceMonitor);
        deviceMonitor.getNumericModel().iterateAndAddListener(numericListener);
        deviceMonitor.getSampleArrayModel().iterateAndAddListener(sampleArrayListener);
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
            final String str = Integer.toString((int) data.value);
            
            if (rosetta.MDC_CO2_RESP_RATE.VALUE.equals(data.metric_id) || rosetta.MDC_AWAY_RESP_RATE.VALUE.equals(data.metric_id)) {
                Platform.runLater( () -> { respiratoryRate.setText(str); }); 
            } else if (rosetta.MDC_AWAY_CO2_ET.VALUE.equals(data.metric_id)) {
                Platform.runLater( () -> { endTidalCO2.setText(str); });
            }
        }
        
    };
    
    private final InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader> sampleArrayListener = new InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
                SampleInfo sampleInfo) {
            if (rosetta.MDC_FLOW_AWAY.VALUE.equals(data.metric_id)&&sampleInfo.valid_data&&0!=(InstanceStateKind.ALIVE_INSTANCE_STATE&sampleInfo.instance_state)) {
                flowPanel.setSource(new SampleArrayWaveformSource(reader, data));
            } else if (rosetta.MDC_PRESS_AWAY.VALUE.equals(data.metric_id)&&sampleInfo.valid_data&&0!=(InstanceStateKind.ALIVE_INSTANCE_STATE&sampleInfo.instance_state)) {
                pressurePanel.setSource(new SampleArrayWaveformSource(reader, data));   
            } else if (rosetta.MDC_AWAY_CO2.VALUE.equals(data.metric_id)&&sampleInfo.valid_data&&0!=(InstanceStateKind.ALIVE_INSTANCE_STATE&sampleInfo.instance_state)) {
                co2Panel.setSource(new SampleArrayWaveformSource(reader, data));
            }


        }

        private final Date date = new Date();
        
        @Override
        public void instanceNotAlive(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray keyHolder,
                SampleInfo sampleInfo) {
            
        }

        @Override
        public void instanceSample(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
                SampleInfo sampleInfo) {
            date.setTime(data.presentation_time.sec * 1000L + data.presentation_time.nanosec / 1000000L);
            Platform.runLater(new Runnable() {
                public void run() {
                    time.setText(dateFormat.format(date));
                }
            });
        }
        
    };
}
