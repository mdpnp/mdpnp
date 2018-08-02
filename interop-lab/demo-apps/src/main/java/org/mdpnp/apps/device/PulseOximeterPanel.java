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

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * @author Jeff Plourde
 *
 */
public class PulseOximeterPanel extends AbstractWaveAndParamsPanel {
    private final static String[] PLETH_WAVEFORMS = new String[] { rosetta.MDC_PULS_OXIM_PLETH.VALUE };

//    private final static String[][] PARAMS = new String[][] { { rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE }, { rosetta.MDC_PULS_OXIM_SAT_O2.VALUE } };
    private final static String[][] PARAMS = new String[][] { 
    	{ rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE }, 
    	{ rosetta.MDC_PULS_OXIM_SAT_O2.VALUE },
    	{ ice.SP02_SOFT_CAN_GET_AVERAGING_RATE.VALUE },
    	{ ice.SP02_AVERAGING_RATE.VALUE },
    	{ ice.SP02_OPER_CAN_SET_AVERAGING_RATE.VALUE },
    	{ ice.SP02_SOFT_CAN_SET_AVERAGING_RATE.VALUE }
    };

    private final static String[] PARAM_LABELS = new String[] { "Pulse", "SpO\u2082", "Get Avg?", "Avg", "Oper set", "Soft set" };

    private final static String[] PARAM_UNITS = new String[] { "BPM", "%" , "", "", "", ""};

    private final static String[] PLETH_LABELS = new String[] { "Plethysmogram" };

    @Override
    public String getStyleClassName() {
        return "pulse-oximeter-panel";
    }

    @Override
    public int getParameterCount() {
        return 6;
    }

    @Override
    public String getParameterLabel(int i) {
        return PARAM_LABELS[i];
    }

    @Override
    public String[] getParameterMetricIds(int i) {
        return PARAMS[i];
    }

    @Override
    public String getParameterUnits(int i) {
        return PARAM_UNITS[i];
    }

    @Override
    public String[] getWaveformLabels() {
        return PLETH_LABELS;
    }

    @Override
    public String[] getWaveformMetricIds() {
        return PLETH_WAVEFORMS;
    }

    @Override
    public Paint getWaveformPaint() {
        return Color.CYAN;
    }

    public static boolean supported(Set<String> identifiers) {
        for (String w : PLETH_WAVEFORMS) {
            if (identifiers.contains(w)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void customiseLabels() {
    	System.err.println("customiseLabel called in PulseOximiterPanel");
    	System.err.println("there are "+params.length+" parameter labels here");
    	/*
    	 * We can refer directly to our labels by index here, because we control
    	 * what order they appear in from our own PARAMS array here.
    	 */
    	Label canGetLabel=params[2];
    	if(canGetLabel.textProperty().isBound()) {
    		//Let's get the bound value
    		String value=canGetLabel.textProperty().getValue();
    		if(value.equals("0")) {
    			canGetLabel.textProperty().unbind();
    			canGetLabel.setText("No");
    			Label actualAverage=params[3];
    			actualAverage.textProperty().unbind();
    			actualAverage.setVisible(false);
    			paramLabelBorders[3].setVisible(false);
    		} else {
    			canGetLabel.textProperty().unbind();
    			canGetLabel.setText("Yes");
    		}
    	}
    	Label operCanSetLabel=params[4];
    	if(operCanSetLabel.textProperty().isBound()) {
    		String value=operCanSetLabel.textProperty().getValue();
    		if(value.equals("0")) {
    			operCanSetLabel.textProperty().unbind();
    			operCanSetLabel.setText("No");
    		} else {
    			operCanSetLabel.textProperty().unbind();
    			operCanSetLabel.setText("Yes");
    		}
    	} else {
    		System.err.println("operCanSetLabel is not bound...");
    	}
    	Label softCanSetLabel=params[5];
    	if(softCanSetLabel.textProperty().isBound()) {
    		String value=softCanSetLabel.textProperty().getValue();
    		if(value.equals("0")) {
    			softCanSetLabel.textProperty().unbind();
    			softCanSetLabel.setText("No");
    		} else {
    			softCanSetLabel.textProperty().unbind();
    			softCanSetLabel.setText("Yes");
    		}
    	}
    	
    }
    

}
