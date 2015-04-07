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
package org.mdpnp.apps.device;

import java.util.Set;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class VentilatorPanel extends DevicePanel {
    private WaveformPanel flowPanel, pressurePanel, co2Panel;
    private final Label time = new Label(" "), respiratoryRate = new Label(" "), endTidalCO2 = new Label(" ");
//    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        deviceMonitor.getNumericModel().addListener(numericListener);
        deviceMonitor.getNumericModel().forEach((t)->add(t));
        
        deviceMonitor.getSampleArrayModel().addListener(sampleArrayListener);
        deviceMonitor.getSampleArrayModel().forEach((t)->add(t));
    }
    
    protected void add(NumericFx data) {
        if (rosetta.MDC_CO2_RESP_RATE.VALUE.equals(data.getMetric_id()) || rosetta.MDC_AWAY_RESP_RATE.VALUE.equals(data.getMetric_id())) {
            respiratoryRate.textProperty().bind(data.valueProperty().asString("%.0f"));
        } else if (rosetta.MDC_AWAY_CO2_ET.VALUE.equals(data.getMetric_id())) {
            endTidalCO2.textProperty().bind(data.valueProperty().asString("%.0f"));
        }
    }
    
    protected void remove(NumericFx data) {
        if (rosetta.MDC_CO2_RESP_RATE.VALUE.equals(data.getMetric_id()) || rosetta.MDC_AWAY_RESP_RATE.VALUE.equals(data.getMetric_id())) {
            respiratoryRate.textProperty().unbind();
        } else if (rosetta.MDC_AWAY_CO2_ET.VALUE.equals(data.getMetric_id())) {
            endTidalCO2.textProperty().unbind();
        }
    }
    
    protected void add(SampleArrayFx data) {
        if(!time.textProperty().isBound()) {
            time.textProperty().bind(data.presentation_timeProperty().asString());
        }
        if (rosetta.MDC_FLOW_AWAY.VALUE.equals(data.getMetric_id())) {
            flowPanel.setSource(new SampleArrayWaveformSource(deviceMonitor.getSampleArrayList().getReader(), data.getHandle()));
        } else if (rosetta.MDC_PRESS_AWAY.VALUE.equals(data.getMetric_id())) {
            pressurePanel.setSource(new SampleArrayWaveformSource(deviceMonitor.getSampleArrayList().getReader(), data.getHandle()));   
        } else if (rosetta.MDC_AWAY_CO2.VALUE.equals(data.getMetric_id())) {
            co2Panel.setSource(new SampleArrayWaveformSource(deviceMonitor.getSampleArrayList().getReader(), data.getHandle()));
        }
    }
    
    protected void remove(SampleArrayFx data) {
        time.textProperty().unbind();
        if (rosetta.MDC_FLOW_AWAY.VALUE.equals(data.getMetric_id())) {
            flowPanel.setSource(null);
        } else if (rosetta.MDC_PRESS_AWAY.VALUE.equals(data.getMetric_id())) {
            pressurePanel.setSource(null);   
        } else if (rosetta.MDC_AWAY_CO2.VALUE.equals(data.getMetric_id())) {
            co2Panel.setSource(null);
        }
    }
    
    private final OnListChange<NumericFx> numericListener = new OnListChange<NumericFx>(
            (t)->add(t), null, (t)->remove(t));
    
    private final OnListChange<SampleArrayFx> sampleArrayListener = new OnListChange<SampleArrayFx>(
            (t)->add(t), null, (t)->remove(t));
    
    }
