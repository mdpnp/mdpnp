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

import java.util.HashSet;
import java.util.Set;

import org.mdpnp.apps.fxbeans.NumericFx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * @author Jeff Plourde
 *
 */
public class PulseOximeterPanel extends AbstractWaveAndParamsPanel {
    private final static String[] PLETH_WAVEFORMS = new String[] { rosetta.MDC_PULS_OXIM_PLETH.VALUE };

    private final static String[][] PARAMS = new String[][] { 
    	{ rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE }, 
    	{ rosetta.MDC_PULS_OXIM_SAT_O2.VALUE },
    	/*
    	
    	*/
    };
    
    private final static String[] PARAMS_FOR_HEADER = new String[] {
    	ice.SP02_SOFT_CAN_GET_AVERAGING_RATE.VALUE,
    	ice.SP02_AVERAGING_RATE.VALUE,
    	ice.SP02_OPER_CAN_SET_AVERAGING_RATE.VALUE,
    	ice.SP02_SOFT_CAN_SET_AVERAGING_RATE.VALUE
    };
    
    private final static String[] LABEL_TEXT_FOR_HEADER = new String[] {
    	"Software can get averaging rate : ",
    	"Current averaging rate (s) : ",
    	"Operator can set averaging rate : ",
    	"Software can set averaging rate : "
    };
    
    private String[][] conversions= new String[][] {
    	{ "No", "Yes" },	//0=No, 1=Yes for software can get avg
    	{},					//No conversion for average rate
    	{ "No", "Yes" },	//0=No, 1=Yes for operator can set avg
    	{ "No", "Yes" },	//0=No, 1=Yes for software can set avg
    	
    };
    
//    private ObservableStringValue averageLabelText=new )
    private SimpleStringProperty averageLabelText=new SimpleStringProperty(LABEL_TEXT_FOR_HEADER[1]);
    
    
    /**
     * Metrics that we've already put in the header - so we don't constantly repeat them.
     */
    private final Set<String> metricsAlreadyInHeader=new HashSet<>();
    
    /**
     * Where to put our custom labels to go in the header.
     */
    private GridPane headerLabelPane;
    
    /**
     * The next row to add to in headerLabelPane
     */
    private int nextRow=0;
    
    /**
     * Value of the average rate in the previous sample.
     */
    private int previousAverageRate;
    
    
    public PulseOximeterPanel() {
    	super();
    }

    private final static String[] PARAM_LABELS = new String[] { "Pulse", "SpO\u2082" /*, "Get Avg?", "Avg", "Oper set", "Soft set" */};

    private final static String[] PARAM_UNITS = new String[] { "BPM", "%" /*, "", "", "", ""*/};

    private final static String[] PLETH_LABELS = new String[] { "Plethysmogram" };

    @Override
    public String getStyleClassName() {
        return "pulse-oximeter-panel";
    }

    @Override
    public int getParameterCount() {
        return 2;	//Why doesn't this just return the length of the PARAM_LABELS?
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
        for (String[] p: PARAMS) {
            if(identifiers.contains(p[0])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void addToHeader(NumericFx data) {
    	for(int i=0;i<PARAMS_FOR_HEADER.length;i++) {
    		String header=PARAMS_FOR_HEADER[i];
    		if(header.equals(data.getMetric_id()) && !metricsAlreadyInHeader.contains(header)) {
    			System.err.println("Need to add "+data.getMetric_id()+" to header");
    			if(null == headerLabelPane) {
    				headerLabelPane=new GridPane();
    				externalPane.getChildren().add(headerLabelPane);	
    			}
    			if(data.getMetric_id().equals(PARAMS_FOR_HEADER[1])) {
    				//Special handling for average value, as this can change.  Should we just make them all observables?
    				averageLabelText.setValue(LABEL_TEXT_FOR_HEADER[1]+(int)data.getValue());
    				Label l=new Label();
    				l.textProperty().bind(averageLabelText);
    				headerLabelPane.add(l, 0, nextRow++);
    			} else {
	    			StringBuilder sb=new StringBuilder(LABEL_TEXT_FOR_HEADER[i]);
	    			if(conversions[i].length>0) {
	    				sb.append(conversions[i][(int)data.getValue()]);
	    			} else {
	    				//No conversion
	    				sb.append((int)data.getValue());
	    			}
	    			headerLabelPane.add(new Label(sb.toString()), 0, nextRow++);
    			}
    			metricsAlreadyInHeader.add(header);
    		}
    		if(data.getMetric_id().equals(PARAMS_FOR_HEADER[1])) {
    			//Set the observable property to update the label with the current rate
    			int val=(int)data.getValue();
    			averageLabelText.setValue(LABEL_TEXT_FOR_HEADER[1]+ (val==0 ? "Unknown" : val));
    		}
    	}
    }

}
