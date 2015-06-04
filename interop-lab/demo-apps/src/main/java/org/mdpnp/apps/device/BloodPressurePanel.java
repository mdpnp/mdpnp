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

import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
//    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Label nextInflation;

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
        
        nextInflation.textProperty().bind(
                Bindings.when(state.isEqualTo(State.Inflating)).then("Inflating...").otherwise(
                Bindings.when(state.isEqualTo(State.Deflating)).then("Deflating...").otherwise(
                Bindings.when(state.isEqualTo(State.Waiting)).then(nextInflationN.divide(1000.0).asString("%.0f").concat(" Seconds")).otherwise(""))));
                        
        systolic.textProperty().bind(Bindings.when(state.isEqualTo(State.Inflating).or(state.isEqualTo(State.Deflating))).then(inflationN.asString("%.0f")).otherwise(systolicN.asString("%.0f")));
        diastolic.textProperty().bind(Bindings.when(state.isEqualTo(State.Inflating).or(state.isEqualTo(State.Deflating))).then("").otherwise(diastolicN.asString("%.0f")));
        pulse.textProperty().bind(Bindings.when(state.isEqualTo(State.Inflating).or(state.isEqualTo(State.Deflating))).then("").otherwise(pulseN.asString("%.0f")));
        
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
        deviceMonitor.getNumericModel().addListener(numericListener);
        deviceMonitor.getNumericModel().forEach((t)->add(t));
    }
    
    protected ObjectProperty<State> state = new SimpleObjectProperty<>(this, "state", State.Uninited);

    private FloatProperty systolicN = new SimpleFloatProperty(this, "systolicN", 0f);
    private FloatProperty diastolicN = new SimpleFloatProperty(this, "diastolicN", 0f);
    private FloatProperty pulseN = new SimpleFloatProperty(this, "pulseN", 0f);
    private FloatProperty inflationN = new SimpleFloatProperty(this, "inflationN", 0f);
    private FloatProperty nextInflationN = new SimpleFloatProperty(this, "nextInflationN", 0f);

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(BloodPressurePanel.class);
    
    protected void remove(NumericFx data) {
        time.textProperty().unbind();
        if (rosetta.MDC_PRESS_CUFF.VALUE.equals(data.getMetric_id())) {
            state.unbind();
        } else if (rosetta.MDC_PRESS_CUFF_SYS.VALUE.equals(data.getMetric_id())) {
            systolicN.unbind();
        } else if (rosetta.MDC_PRESS_CUFF_DIA.VALUE.equals(data.getMetric_id())) {
            diastolicN.unbind();
        } else if (rosetta.MDC_PULS_RATE_NON_INV.VALUE.equals(data.getMetric_id())) {
            pulseN.unbind();
        } else if (ice.MDC_PRESS_CUFF_NEXT_INFLATION.VALUE.equals(data.getMetric_id())) {
            nextInflationN.unbind();
        } else if (ice.MDC_PRESS_CUFF_INFLATION.VALUE.equals(data.getMetric_id())) {
            inflationN.unbind();
        }
    }
    
    protected void add(NumericFx data) {
        time.textProperty().bind(data.presentation_timeProperty().asString());
        if (rosetta.MDC_PRESS_CUFF.VALUE.equals(data.getMetric_id())) {
            state.bind(Bindings.when(data.valueProperty().isEqualTo(ice.MDC_EVT_STAT_NBP_DEFL_AND_MEAS_BP.VALUE, 0.1f)).then(State.Deflating).otherwise(Bindings.when(data.valueProperty().isEqualTo(ice.MDC_EVT_STAT_NBP_INFL_TO_MAX_CUFF_PRESS.VALUE)).then(State.Inflating).otherwise(Bindings.when(data.valueProperty().isEqualTo(ice.MDC_EVT_STAT_OFF.VALUE)).then(State.Waiting).otherwise(State.Uninited))));
        } else if (rosetta.MDC_PRESS_CUFF_SYS.VALUE.equals(data.getMetric_id())) {
            systolicN.bind(data.valueProperty());
        } else if (rosetta.MDC_PRESS_CUFF_DIA.VALUE.equals(data.getMetric_id())) {
            diastolicN.bind(data.valueProperty());
        } else if (rosetta.MDC_PULS_RATE_NON_INV.VALUE.equals(data.getMetric_id())) {
            pulseN.bind(data.valueProperty());
        } else if (ice.MDC_PRESS_CUFF_NEXT_INFLATION.VALUE.equals(data.getMetric_id())) {
            nextInflationN.bind(data.valueProperty());
        } else if (ice.MDC_PRESS_CUFF_INFLATION.VALUE.equals(data.getMetric_id())) {
            inflationN.bind(data.valueProperty());
        }
    }
    
    private final OnListChange<NumericFx> numericListener = new OnListChange<NumericFx>((t)->add(t), null, (t)->remove(t));
}
