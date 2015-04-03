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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import org.mdpnp.guis.waveform.NumericWaveformSource;
import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.TestWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformPane;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.SampleInfo;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

/**
 * @author Jeff Plourde
 *
 */
public class PulseOximeterPanel extends DevicePanel {

    @SuppressWarnings("unused")
    private Label spo2, heartrate, spo2Label, heartrateLabel;
    private Label spo2Low, spo2Up, heartrateLow, heartrateUp;
    private GridPane spo2Bounds, heartrateBounds;
    private BorderPane spo2Panel, heartratePanel;
    private WaveformPanel pulsePanel, plethPanel;
    private Label time;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected void buildComponents() {
        spo2Bounds = new GridPane();
        spo2Bounds.add(spo2Up = new Label("--"), 0, 0);
        spo2Bounds.add(spo2Low = new Label("--"), 0, 1);
        spo2Bounds.add(spo2Label = new Label("%"), 0, 2);
        spo2Up.setVisible(false);
        spo2Low.setVisible(false);

        spo2Panel = new BorderPane();
        spo2Panel.setTop(new Label("SpO\u2082"));
        spo2Panel.setCenter(spo2 = new Label("----"));
        spo2.setAlignment(Pos.CENTER_RIGHT);
        spo2Panel.setRight(spo2Bounds);

        heartrateBounds = new GridPane();
        heartrateBounds.add(heartrateUp = new Label("--"), 0, 0);
        heartrateBounds.add(heartrateLow = new Label("--"), 0, 1);
        heartrateBounds.add(heartrateLabel = new Label("BPM"), 0, 2);
        heartrateUp.setVisible(false);
        heartrateLow.setVisible(false);

        heartratePanel = new BorderPane();
        Label lbl;
        heartratePanel.setTop(lbl = new Label("Pulse Rate"));
        FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(lbl.getFont());
        float w = fm.computeStringWidth("RespiratoryRate");
        lbl.setMinWidth(w);
        lbl.setPrefWidth(w);
        heartratePanel.setCenter(heartrate = new Label("----"));
        heartrate.setTextAlignment(TextAlignment.RIGHT);
        heartrate.setAlignment(Pos.CENTER_RIGHT);
        heartratePanel.setRight(heartrateBounds);

        WaveformPanelFactory fact = new WaveformPanelFactory();

        plethPanel = fact.createWaveformPanel();
        pulsePanel = fact.createWaveformPanel();

        GridPane upper = new GridPane();
        BorderPane x = label("Plethysmogram", (Node) plethPanel);
        GridPane.setVgrow(x, Priority.ALWAYS);
        GridPane.setHgrow(x, Priority.ALWAYS);
        upper.add(x, 0, 0);
        x = label("Pulse Rate", (Node) pulsePanel);
        GridPane.setVgrow(x, Priority.ALWAYS);
        GridPane.setHgrow(x, Priority.ALWAYS);
        upper.add(x, 0, 1);

        GridPane east = new GridPane();
        east.add(spo2Panel, 0, 0);
        east.add(heartratePanel, 0, 1);

        setCenter(upper);
        setRight(east);

        setBottom(labelLeft("Last Sample: ", time = new Label("TIME")));

        ((JavaFXWaveformPane)plethPanel).getCanvas().getGraphicsContext2D().setStroke(Color.CYAN);
        ((JavaFXWaveformPane)pulsePanel).getCanvas().getGraphicsContext2D().setStroke(Color.CYAN);
    }

    public PulseOximeterPanel() {
        getStyleClass().add("pulse-oximeter-panel");
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
//                date.setTime(1000L * data.presentation_time.sec + data.presentation_time.nanosec / 1000000L);
//                time.setText(dateFormat.format(date));
            }
        }

        @Override
        public void instanceNotAlive(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric keyHolder,
                SampleInfo sampleInfo) {
        }

        @Override
        public void instanceSample(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
            Platform.runLater(new Runnable() {
                public void run() {
                    setInt(data, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, spo2, null);
                    setInt(data, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, heartrate, null);
                    date.setTime(1000L * data.presentation_time.sec + data.presentation_time.nanosec / 1000000L);
                    time.setText(dateFormat.format(date));
                }
            });
        }
        
    };
    
    private final InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader> sampleArrayListener = new InstanceModelListener<ice.SampleArray, ice.SampleArrayDataReader>() {

        @Override
        public void instanceAlive(ReaderInstanceModel<SampleArray, SampleArrayDataReader> model, SampleArrayDataReader reader, SampleArray data,
                SampleInfo sampleInfo) {
            if (rosetta.MDC_PULS_OXIM_PLETH.VALUE.equals(data.metric_id)) {
                if(null == plethWave) {
                    plethWave = new SampleArrayWaveformSource(deviceMonitor.getSampleArrayModel().getReader(), data);
                    plethPanel.setSource(plethWave);
                }
                date.setTime(1000L * data.presentation_time.sec + data.presentation_time.nanosec / 1000000L);
                final String dt = dateFormat.format(date);
                Platform.runLater( () -> { time.setText(dt); } );
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
    
    public static class MainApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            PulseOximeterPanel p = new PulseOximeterPanel();
            p.plethPanel.setSource(new TestWaveformSource());
            primaryStage.setScene(new Scene(p));
            primaryStage.show();
        }
        
    }
    
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
