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

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * @author Jeff Plourde
 *
 */
public class VentilatorPanel extends AbstractWaveAndParamsPanel {
    private final static String[] RR_WAVEFORMS = new String[] { rosetta.MDC_FLOW_AWAY.VALUE, rosetta.MDC_PRESS_AWAY.VALUE,
            rosetta.MDC_AWAY_CO2.VALUE, rosetta.MDC_IMPED_TTHOR.VALUE, rosetta.MDC_VOL_AWAY_TIDAL.VALUE };
    
//    private final static String[] RR_WAVEFORMS = new String[] { rosetta.MDC_VOL_AWAY_TIDAL.VALUE };


    private final static String[][] PARAMS = new String[][] {
            { rosetta.MDC_RESP_RATE.VALUE, rosetta.MDC_CO2_RESP_RATE.VALUE, rosetta.MDC_TTHOR_RESP_RATE.VALUE, rosetta.MDC_AWAY_RESP_RATE.VALUE },	//resp rate
            { rosetta.MDC_AWAY_CO2_ET.VALUE },	//etc02
            { rosetta.MDC_PRESS_AWAY_INSP_PEAK.VALUE },	//insp peak
            { rosetta.MDC_PRESS_RESP_PLAT.VALUE },	//plateau pressure
            { "ICE_PEEP" },	//peep
            { "ICE_FIO2" },	//fio2
            { rosetta.MDC_VENT_VOL_LEAK.VALUE },
            { "NKV_550_OP_MODE" }
    };
    private final static String[] PARAM_LABELS = new String[] { "Resp Rate", "etCO2", "Ppeak", "Pplat", "PEEP", "FiO2", "Leak", "Op Mode" };
    private final static String[] PARAM_UNITS = new String[] { "BPM", "mmHg", "mmHg", "cmH2O", "cmH2O", "%", "%" ,""};

    private final static String[] RR_LABELS = new String[] { "Flow", "Pressure", "CO2", "Impedance", "Volume" };
    
    @Override
    public String getStyleClassName() {
        return "ventilator-panel";
    }

    @Override
    public int getParameterCount() {
        return PARAMS.length;
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
        return RR_LABELS;
    }

    @Override
    public String[] getWaveformMetricIds() {
        return RR_WAVEFORMS;
    }

    @Override
    public Paint getWaveformPaint() {
        return Color.WHITE;
    }
    
    @Override
    public Paint getWaveformPaint(String metricId) {
    	switch (metricId) {
		case rosetta.MDC_PRESS_AWAY.VALUE:
			return Color.DEEPSKYBLUE;
		case rosetta.MDC_FLOW_AWAY.VALUE:
			return Color.WHITE;
		case rosetta.MDC_VOL_AWAY_TIDAL.VALUE:
			return Color.YELLOW;
		default:
			break;
		}
    	return getWaveformPaint();
    }

    public static boolean supported(Set<String> identifiers) {
        for (String w : RR_WAVEFORMS) {
            if (identifiers.contains(w)) {
                return true;
            }
        }
        return false;
    }
}