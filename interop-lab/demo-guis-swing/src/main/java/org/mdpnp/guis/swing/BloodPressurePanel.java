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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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
public class BloodPressurePanel extends DevicePanel {

    private JLabel systolicLabel, diastolicLabel, pulseLabel;
    private JLabel systolic, diastolic, pulse;
    private JPanel systolicPanel, diastolicPanel, pulsePanel;
    private JLabel time;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private JLabel nextInflation;
    private final Date date = new Date();

    // private JButton inflate = new JButton("Inflate");

    protected void buildComponents() {
        systolicLabel = new JLabel("mmHg");

        systolicPanel = new JPanel();
        systolicPanel.setOpaque(false);
        systolicPanel.setLayout(new BorderLayout());
        systolicPanel.add(systolic = new JLabel("---"), BorderLayout.CENTER);
        systolic.setHorizontalAlignment(JLabel.RIGHT);
        systolic.setBorder(new EmptyBorder(5, 5, 5, 5));
        systolicPanel.add(systolicLabel, BorderLayout.EAST);

        diastolicLabel = new JLabel("mmHg");

        diastolicPanel = new JPanel();
        diastolicPanel.setOpaque(false);
        diastolicPanel.setLayout(new BorderLayout());
        diastolicPanel.add(diastolic = new JLabel("---"), BorderLayout.CENTER);
        diastolic.setBorder(new EmptyBorder(5, 5, 5, 5));
        diastolic.setHorizontalAlignment(JLabel.RIGHT);
        diastolicPanel.add(diastolicLabel, BorderLayout.EAST);

        pulseLabel = new JLabel("BPM");

        pulsePanel = new JPanel();
        pulsePanel.setOpaque(false);
        pulsePanel.setLayout(new BorderLayout());
        pulsePanel.add(pulse = new JLabel("---"), BorderLayout.CENTER);
        pulse.setBorder(new EmptyBorder(5, 5, 5, 5));
        pulse.setHorizontalAlignment(JLabel.RIGHT);
        pulsePanel.add(pulseLabel, BorderLayout.EAST);

        nextInflation = new JLabel("...");
        nextInflation.setHorizontalAlignment(JLabel.CENTER);

        JPanel upper = new JPanel();
        upper.setOpaque(false);
        upper.setLayout(new GridLayout(4, 1));

        upper.add(systolicPanel);
        upper.add(diastolicPanel);
        upper.add(pulsePanel);
        upper.add(nextInflation);
        setLayout(new BorderLayout());
        add(upper, BorderLayout.CENTER);

        add(time = new JLabel(""), BorderLayout.SOUTH);
        time.setHorizontalAlignment(JLabel.RIGHT);

        // inflate.addActionListener(new ActionListener() {
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // MutableTextUpdate mtu = new
        // MutableTextUpdateImpl(NoninvasiveBloodPressure.REQUEST_NIBP, guid);
        // mtu.setSource("*");
        // mtu.setTarget(BloodPressurePanel.this.source);
        //
        // gateway.update(BloodPressurePanel.this, mtu);
        // }
        // });
        setForeground(Color.magenta);
        setBackground(Color.black);
        setOpaque(true);
    }

    protected static float maxFontSize(JLabel label) {
        Font labelFont = label.getFont();
        String labelText = label.getText();

        int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
        int stringHeight = label.getFontMetrics(labelFont).getHeight();
        int componentWidth = label.getWidth();
        int componentHeight = label.getHeight();

        // Find out how much the font can grow in width.
        double widthRatio = (double) componentWidth / (double) stringWidth;
        double heightRatio = 1.0 * componentHeight / stringHeight;

        double smallerRatio = Math.min(widthRatio, heightRatio) - 0.5f;

        return (float) (labelFont.getSize2D() * smallerRatio);
    }

    protected static void resizeFontToFill(JLabel... label) {
        float fontSize = Float.MAX_VALUE;

        for (JLabel l : label) {
            fontSize = Math.min(fontSize, maxFontSize(l));
        }

        for (JLabel l : label) {
            l.setFont(l.getFont().deriveFont(fontSize));
        }
    }

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        if (e.getID() == ComponentEvent.COMPONENT_RESIZED) {
            resizeFontToFill(systolic, diastolic, pulse);
        }
        super.processComponentEvent(e);
    }

    public BloodPressurePanel() {
        buildComponents();
        enableEvents(ComponentEvent.COMPONENT_RESIZED);
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
            // log.debug("State:"+state);

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
            date.setTime(1000L * data.presentation_time.sec + data.presentation_time.nanosec / 1000000L);
            time.setText(dateFormat.format(date));

        }
        
    };


}
