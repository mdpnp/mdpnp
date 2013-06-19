/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.swing;

import ice.Numeric;
import ice.SampleArray;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.SampleInfo;

@SuppressWarnings("serial")
public class BloodPressurePanel extends DevicePanel {
	
	private JLabel systolicLabel, diastolicLabel, pulseLabel;
	private JLabel systolic, diastolic, pulse;
	private JPanel systolicPanel, diastolicPanel, pulsePanel;
	private JLabel time;
	private JLabel nextInflation;
//	private JButton inflate = new JButton("Inflate"); 
	
	protected void buildComponents() {
		systolicLabel = new JLabel("mmHg");
				
		systolicPanel = new JPanel();
		systolicPanel.setOpaque(false);
		systolicPanel.setLayout(new BorderLayout());
		systolicPanel.add(systolic = new JLabel("---"), BorderLayout.CENTER);
		systolic.setHorizontalAlignment(JLabel.RIGHT);
		systolic.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		systolicPanel.add(systolicLabel, BorderLayout.EAST);
		
		diastolicLabel = new JLabel("mmHg");
		
		diastolicPanel = new JPanel();
		diastolicPanel.setOpaque(false);
		diastolicPanel.setLayout(new BorderLayout());
		diastolicPanel.add(diastolic = new JLabel("---"), BorderLayout.CENTER);
		diastolic.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		diastolic.setHorizontalAlignment(JLabel.RIGHT);
		diastolicPanel.add(diastolicLabel, BorderLayout.EAST);

		pulseLabel = new JLabel("BPM");
		
		pulsePanel = new JPanel();
		pulsePanel.setOpaque(false);
		pulsePanel.setLayout(new BorderLayout());
		pulsePanel.add(pulse = new JLabel("---"), BorderLayout.CENTER);
		pulse.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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
		
//		inflate.addActionListener(new ActionListener() {
//		    @Override
//		    public void actionPerformed(ActionEvent e) {
//		        MutableTextUpdate mtu = new MutableTextUpdateImpl(NoninvasiveBloodPressure.REQUEST_NIBP, guid);
//		        mtu.setSource("*");
//		        mtu.setTarget(BloodPressurePanel.this.source);
//		        
//		        gateway.update(BloodPressurePanel.this, mtu);
//		    }
//		});
		
	}
	
	
	protected static float maxFontSize(JLabel label) {
		Font labelFont = label.getFont();
		String labelText = label.getText();

		int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
		int stringHeight = label.getFontMetrics(labelFont).getHeight();
		int componentWidth = label.getWidth();
		int componentHeight = label.getHeight();

		// Find out how much the font can grow in width.
		double widthRatio = (double)componentWidth / (double)stringWidth;
		double heightRatio = 1.0 * componentHeight / stringHeight;

		double smallerRatio = Math.min(widthRatio, heightRatio) - 0.5f;
		
		return (float) (labelFont.getSize2D() * smallerRatio);
	}
	
	protected static void resizeFontToFill(JLabel... label) {
		float fontSize = Float.MAX_VALUE;
		
		for(JLabel l : label) {
			fontSize = Math.min(fontSize, maxFontSize(l));
		}
		
		for(JLabel l : label) {
			l.setFont(l.getFont().deriveFont(fontSize));
		}
	}
	
	
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		if(e.getID() == ComponentEvent.COMPONENT_RESIZED) {
			resizeFontToFill(systolic, diastolic, pulse);
		}
		super.processComponentEvent(e);
	}
	
	public BloodPressurePanel() {
		buildComponents();
		enableEvents(ComponentEvent.COMPONENT_RESIZED);
	}

	   
    public static boolean supported(Set<Integer> names) {
        return names.contains(ice.MDC_PRESS_CUFF.VALUE);
    }

    // TODO manage state better
    /* jplourde June 19, 2013
     * The "entire" blood pressure state is constructed incrementally
     * through calls to numeric(Numeric,SampleInfo)
     * 
     * I need the "entire" blood pressure state to render a meaningful GUI
     * 
     * so the end result is storing state here.
     * 
     * An alternative would be to give this component visibility "down" "into"
     * the associated DataReader to get relevant instances on-demand and the
     * DataReader can manage state per the QoS parameters.
     * 
     *
     */
    
    enum State {
        Inflating,
        Deflating,
        Waiting,
        Uninited
    }
    
    private State state = State.Uninited;
    
    private final Numeric systolicN = new Numeric();
    private final Numeric diastolicN = new Numeric();
    private final Numeric pulseN = new Numeric();
    private final Numeric inflationN = new Numeric();
    private final Numeric nextInflationN = new Numeric();
    
    private static final Logger log = LoggerFactory.getLogger(BloodPressurePanel.class);
    @Override
    public void numeric(Numeric numeric, SampleInfo sampleInfo) {
        log.debug("N:"+numeric);
        switch(numeric.name) {
        case ice.MDC_PRESS_CUFF.VALUE:
            switch((int)numeric.value) {
            case ice.MDC_EVT_STAT_NBP_DEFL_AND_MEAS_BP.VALUE:
                state = State.Deflating;
                break;
            case ice.MDC_EVT_STAT_NBP_INFL_TO_MAX_CUFF_PRESS.VALUE:
                this.state = State.Inflating;
                break;
            case ice.MDC_EVT_STAT_OFF.VALUE:
                this.state = State.Waiting;
                break;
            }
            break;
        case ice.MDC_PRESS_CUFF_SYS.VALUE:
            systolicN.copy_from(numeric);
            break;
        case ice.MDC_PRESS_CUFF_DIA.VALUE:
            diastolicN.copy_from(numeric);
            break;
        case ice.MDC_PULS_RATE_NON_INV.VALUE:
            pulseN.copy_from(numeric);
            break;
        case ice.MDC_PRESS_CUFF_NEXT_INFLATION.VALUE:
            nextInflationN.copy_from(numeric);
            break;
        case ice.MDC_PRESS_CUFF_INFLATION.VALUE:
            inflationN.copy_from(numeric);
            break;
        }
        log.debug("State:"+state);
        
        switch(state) {
        case Inflating:
            nextInflation.setText("Inflating...");
            systolic.setText(Integer.toString((int)inflationN.value));
            diastolic.setText("");
            pulse.setText("");
            break;
        case Deflating:
            nextInflation.setText("Deflating...");
            systolic.setText(Integer.toString((int)inflationN.value));
            diastolic.setText("");
            pulse.setText("");
            break;
        case Waiting:
            long  seconds = ((long)nextInflationN.value % 60000L / 1000L);
            this.nextInflation.setText((int)Math.floor(1.0 * nextInflationN.value / 60000.0) + ":" + (seconds<10?"0":"") + seconds + " MIN");
            systolic.setText(Integer.toString((int)systolicN.value));
            diastolic.setText(Integer.toString((int)diastolicN.value));
            pulse.setText(Integer.toString((int)pulseN.value));
            break;
        case Uninited:
            nextInflation.setText("");
            systolic.setText("");
            diastolic.setText("");
            pulse.setText("");
            break;
        }
    }


    @Override
    public void sampleArray(SampleArray sampleArray, SampleInfo sampleInfo) {
        
    }

}
