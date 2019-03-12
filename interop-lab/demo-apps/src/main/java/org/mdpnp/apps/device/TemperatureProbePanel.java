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

import org.mdpnp.apps.fxbeans.NumericFx;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * @author Jeff Plourde
 *
 */
public class TemperatureProbePanel extends AbstractWaveAndParamsPanel {
    private final static String[] TEMP_WAVEFORMS = new String[] {  };

    private final static String[][] PARAMS = new String[][] {
            { rosetta.MDC_TEMP_BLD.VALUE }
    };

    private final static String[] PARAM_LABELS = new String[] { "Temp." };

    //00B0 is UTF-16 symbol for degrees
    private final static String[] PARAM_UNITS = new String[] { "\u00B0C" };

    private final static String[] TEMP_LABELS = new String[] { };

    @Override
    public String getStyleClassName() {
        return "temperature-probe-panel";
    }

    @Override
    public int getParameterCount() {
        return 1;
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
        return TEMP_LABELS;
    }

    @Override
    public String[] getWaveformMetricIds() {
        return TEMP_WAVEFORMS;
    }

    @Override
    public Paint getWaveformPaint() {
        return Color.RED;
    }

    public static boolean supported(Set<String> identifiers) {
        for (String[] w : PARAMS) {
            for(String n : w) {
                if (identifiers.contains(n)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void add(NumericFx data) {
        super.add(data,"%.1f");
    }

}
