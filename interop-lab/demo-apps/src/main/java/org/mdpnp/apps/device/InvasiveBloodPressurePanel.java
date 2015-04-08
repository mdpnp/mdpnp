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
public class InvasiveBloodPressurePanel extends AbstractWaveAndParamsPanel {
    private final static String[] IBP_WAVEFORMS = new String[] { rosetta.MDC_PRESS_BLD.VALUE, rosetta.MDC_PRESS_BLD_ART.VALUE,
            rosetta.MDC_PRESS_INTRA_CRAN.VALUE, rosetta.MDC_PRESS_BLD_AORT.VALUE, rosetta.MDC_PRESS_BLD_ART_ABP.VALUE,
            rosetta.MDC_PRESS_BLD_ART_FEMORAL.VALUE, rosetta.MDC_PRESS_BLD_ART_PULM.VALUE, rosetta.MDC_PRESS_BLD_ART_UMB.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_LEFT.VALUE, rosetta.MDC_PRESS_BLD_ATR_RIGHT.VALUE };

    private final static String[][] PARAMS = new String[][] {
            { rosetta.MDC_PRESS_BLD_SYS.VALUE, rosetta.MDC_PRESS_BLD_ART_SYS.VALUE, rosetta.MDC_PRESS_INTRA_CRAN_SYS.VALUE,
                    rosetta.MDC_PRESS_BLD_AORT_SYS.VALUE, rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE, rosetta.MDC_PRESS_BLD_ART_FEMORAL_SYS.VALUE,
                    rosetta.MDC_PRESS_BLD_ART_PULM_SYS.VALUE, rosetta.MDC_PRESS_BLD_ART_UMB_SYS.VALUE, rosetta.MDC_PRESS_BLD_ATR_LEFT_SYS.VALUE,
                    rosetta.MDC_PRESS_BLD_ATR_RIGHT_SYS.VALUE },
            { rosetta.MDC_PRESS_BLD_DIA.VALUE, rosetta.MDC_PRESS_BLD_ART_DIA.VALUE, rosetta.MDC_PRESS_INTRA_CRAN_DIA.VALUE,
                    rosetta.MDC_PRESS_BLD_AORT_DIA.VALUE, rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE, rosetta.MDC_PRESS_BLD_ART_FEMORAL_DIA.VALUE,
                    rosetta.MDC_PRESS_BLD_ART_PULM_DIA.VALUE, rosetta.MDC_PRESS_BLD_ART_UMB_DIA.VALUE, rosetta.MDC_PRESS_BLD_ATR_LEFT_DIA.VALUE,
                    rosetta.MDC_PRESS_BLD_ATR_RIGHT_DIA.VALUE },
            { rosetta.MDC_PRESS_BLD_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ART_MEAN.VALUE, rosetta.MDC_PRESS_INTRA_CRAN_MEAN.VALUE,
                    rosetta.MDC_PRESS_BLD_AORT_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ART_ABP_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ART_FEMORAL_MEAN.VALUE,
                    rosetta.MDC_PRESS_BLD_ART_PULM_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ART_UMB_MEAN.VALUE, rosetta.MDC_PRESS_BLD_ATR_LEFT_MEAN.VALUE,
                    rosetta.MDC_PRESS_BLD_ATR_RIGHT_MEAN.VALUE } };

    private final static String[] PARAM_LABELS = new String[] { "Sys", "Dia", "Mean" };

    private final static String[] PARAM_UNITS = new String[] { "mmHg", "mmHg", "mmHg" };

    private final static String[] IBP_LABELS = new String[] { "Invasive Pressure", 
        "Arterial Pressure", "Intra-Cranial Pressure", "Aortic Pressure", "Arterial Blood Pressure", "Femoral Arterial Pressure", "Pulmonary Arterial Pressure",
            "Umbilical Arterial Pressure", "Left Atrial Pressure", "Right Atrial Pressure" };

    @Override
    public String getStyleClassName() {
        return "invasive-blood-pressure-panel";
    }

    @Override
    public int getParameterCount() {
        return 3;
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
        return IBP_LABELS;
    }

    @Override
    public String[] getWaveformMetricIds() {
        return IBP_WAVEFORMS;
    }

    @Override
    public Paint getWaveformPaint() {
        return Color.RED;
    }

    public static boolean supported(Set<String> identifiers) {
        for (String w : IBP_WAVEFORMS) {
            if (identifiers.contains(w)) {
                return true;
            }
        }
        return false;
    }

}
