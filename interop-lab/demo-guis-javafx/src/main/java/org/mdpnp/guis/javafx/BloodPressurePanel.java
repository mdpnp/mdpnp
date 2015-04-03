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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

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
public class BloodPressurePanel extends DevicePanel {

    private Label systolicLabel, diastolicLabel, pulseLabel;
    private Label systolic, diastolic, pulse;
    private BorderPane systolicPanel, diastolicPanel, pulsePanel;
    private Label time;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Label nextInflation;
    private final Date date = new Date();

    protected void buildComponents() {
        systolicLabel = new Label("mmHg");

        systolicPanel = new BorderPane();
        systolicPanel.setCenter(systolic = new Label("---"));
        systolic.setAlignment(Pos.CENTER_LEFT);
        systolicPanel.setRight(systolicLabel);

        diastolicLabel = new Label("mmHg");

        diastolicPanel = new BorderPane();
        diastolicPanel.setCenter(diastolic = new Label("---"));
        diastolic.setAlignment(Pos.CENTER_RIGHT);
        diastolicPanel.setRight(diastolicLabel);

        pulseLabel = new Label("BPM");

        pulsePanel = new BorderPane();
        pulsePanel.setCenter(pulse = new Label("---"));
        pulse.setAlignment(Pos.CENTER_RIGHT);
        pulsePanel.setRight(pulseLabel);

        nextInflation = new Label("...");
        nextInflation.setAlignment(Pos.CENTER);

        GridPane upper = new GridPane();

        upper.add(systolicPanel, 0, 0);
        upper.add(diastolicPanel, 0, 1);
        upper.add(pulsePanel, 0, 2);
        upper.add(nextInflation, 0, 3);
        setCenter(upper);

        setBottom(time = new Label(""));
        time.setAlignment(Pos.CENTER_RIGHT);
    }

    protected static float maxFontSize(Label label) {
        Font labelFont = label.getFont();
        String labelText = label.getText();

        FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(labelFont);
        float stringWidth = fm.computeStringWidth(labelText);
        float stringHeight = fm.getLineHeight();
        double componentWidth = label.getWidth();
        double componentHeight = label.getHeight();

        // Find out how much the font can grow in width.
        double widthRatio = componentWidth / stringWidth;
        double heightRatio = 1.0 * componentHeight / stringHeight;

        double smallerRatio = Math.min(widthRatio, heightRatio) - 0.5f;

        return (float) (labelFont.getSize() * smallerRatio);
    }

    public BloodPressurePanel() {
        getStyleClass().add("blood-pressure-panel");
        buildComponents();
    }

    public static boolean supported(Set<String> names) {
        return names.contains(rosetta.MDC_PRESS_CUFF.VALUE);
    }
    
    @Override
    public void destroy() {
        super.destroy();
        deviceMonitor.getNumericModel().removeListener(numericListener);
    }

    // TODO manage state better
    /*
     * jplourde June 19, 2013 The "entire" blood pressure state is constructed
     * incrementally through calls to numeric(Numeric,SampleInfo)
     * 
     * I need the "entire" blood pressure state to render a meaningful GUI
     * 
     * so the end result is storing state here.
     * 
     * An alternative would be to give this component visibility "down" "into"
     * the associated DataReader to get relevant instances on-demand and the
     * DataReader can manage state per the QoS parameters.
     */

    enum State {
        Inflating, Deflating, Waiting, Uninited
    }

    @Override
    public void set(DeviceDataMonitor deviceMonitor) {
        super.set(deviceMonitor);
        deviceMonitor.getNumericModel().iterateAndAddListener(numericListener);
    }
    
    protected State state = State.Uninited;

    private final Numeric systolicN = new Numeric();
    private final Numeric diastolicN = new Numeric();
    private final Numeric pulseN = new Numeric();
    private final Numeric inflationN = new Numeric();
    private final Numeric nextInflationN = new Numeric();

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(BloodPressurePanel.class);
    
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
            if (rosetta.MDC_PRESS_CUFF.VALUE.equals(data.metric_id)) {
                switch ((int) data.value) {
                case ice.MDC_EVT_STAT_NBP_DEFL_AND_MEAS_BP.VALUE:
                    BloodPressurePanel.this.state = State.Deflating;
                    break;
                case ice.MDC_EVT_STAT_NBP_INFL_TO_MAX_CUFF_PRESS.VALUE:
                    BloodPressurePanel.this.state = State.Inflating;
                    break;
                case ice.MDC_EVT_STAT_OFF.VALUE:
                    BloodPressurePanel.this.state = State.Waiting;
                    break;
                }
            } else if (rosetta.MDC_PRESS_CUFF_SYS.VALUE.equals(data.metric_id)) {
                systolicN.copy_from(data);
            } else if (rosetta.MDC_PRESS_CUFF_DIA.VALUE.equals(data.metric_id)) {
                diastolicN.copy_from(data);
            } else if (rosetta.MDC_PULS_RATE_NON_INV.VALUE.equals(data.metric_id)) {
                pulseN.copy_from(data);
            } else if (ice.MDC_PRESS_CUFF_NEXT_INFLATION.VALUE.equals(data.metric_id)) {
                nextInflationN.copy_from(data);
            } else if (ice.MDC_PRESS_CUFF_INFLATION.VALUE.equals(data.metric_id)) {
                inflationN.copy_from(data);
            }
            
            date.setTime(1000L * data.presentation_time.sec + data.presentation_time.nanosec / 1000000L);
            final String dt = dateFormat.format(date);

            Platform.runLater( () -> {
                switch (BloodPressurePanel.this.state) {
                case Inflating:
                    nextInflation.setText("Inflating...");
                    systolic.setText(Integer.toString((int) inflationN.value));
                    diastolic.setText("");
                    pulse.setText("");
                    break;
                case Deflating:
                    nextInflation.setText("Deflating...");
                    systolic.setText(Integer.toString((int) inflationN.value));
                    diastolic.setText("");
                    pulse.setText("");
                    break;
                case Waiting:
                    long seconds = ((long) nextInflationN.value % 60000L / 1000L);
                    BloodPressurePanel.this.nextInflation.setText((int) Math.floor(1.0 * nextInflationN.value / 60000.0) + ":" + (seconds < 10 ? "0" : "") + seconds
                            + " MIN");
                    systolic.setText(Integer.toString((int) systolicN.value));
                    diastolic.setText(Integer.toString((int) diastolicN.value));
                    pulse.setText(Integer.toString((int) pulseN.value));
                    break;
                case Uninited:
                    nextInflation.setText("");
                    systolic.setText("");
                    diastolic.setText("");
                    pulse.setText("");
                    break;
                }
                time.setText(dt);
            });
        }
    };
}
