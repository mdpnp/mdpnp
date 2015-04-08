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
public class ElectroCardioGramPanel extends AbstractWaveAndParamsPanel {
    private final static String[] ECG_WAVEFORMS = new String[] { ice.MDC_ECG_LEAD_I.VALUE, ice.MDC_ECG_LEAD_II.VALUE, ice.MDC_ECG_LEAD_III.VALUE,
            ice.MDC_ECG_LEAD_V1.VALUE };

    private final static String[][] PARAMS = new String[][] { { rosetta.MDC_ECG_HEART_RATE.VALUE }, { rosetta.MDC_TTHOR_RESP_RATE.VALUE }, };

    private final static String[] PARAM_LABELS = new String[] { "Heart Rate", "RespiratoryRate" };

    private final static String[] PARAM_UNITS = new String[] { "BPM", "BPM" };

    private final static String[] ECG_LABELS = new String[] { "Lead I", "Lead II", "Lead III", "Lead V1" };

    @Override
    public String getStyleClassName() {
        return "electro-cardiogram-panel";
    }

    @Override
    public int getParameterCount() {
        return 2;
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
        return ECG_LABELS;
    }

    @Override
    public String[] getWaveformMetricIds() {
        return ECG_WAVEFORMS;
    }

    @Override
    public Paint getWaveformPaint() {
        return Color.GREEN;
    }

    public static boolean supported(Set<String> identifiers) {
        for (String w : ECG_WAVEFORMS) {
            if (identifiers.contains(w)) {
                return true;
            }
        }
        return false;
    }

}
