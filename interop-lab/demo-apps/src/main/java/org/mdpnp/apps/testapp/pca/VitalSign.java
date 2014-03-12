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
package org.mdpnp.apps.testapp.pca;

import java.awt.Color;

import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;

/**
 * @author Jeff Plourde
 *
 */
public enum VitalSign {
    HeartRate("Heart Rate", "bpm", new String[] { rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, rosetta.MDC_PULS_RATE_NON_INV.VALUE,
            rosetta.MDC_BLD_PULS_RATE_INV.VALUE, rosetta.MDC_ECG_CARD_BEAT_RATE.VALUE }, 40f, 140f, 20f, 160f, 0f, 250f, 5000L, 5000L, Color.green), SpO2(
            "SpO\u2082", "%", new String[] { rosetta.MDC_PULS_OXIM_SAT_O2.VALUE }, 95f, null, 85f, null, 50f, 100f, 5000L, 5000L, Color.pink), RespiratoryRate(
            "Resp Rate", "bpm", new String[] { rosetta.MDC_RESP_RATE.VALUE }, 10f, 18f, 4f, 35f, 0f, 40f, 5000L, 5000L, Color.yellow), EndTidalCO2(
            "etCO\u2082", "mmHg", new String[] { rosetta.MDC_AWAY_CO2_EXP.VALUE }, 20f, 45f, 10f, 75f, 0f, 120f, 5000L, 5000L, Color.yellow), Temperature(
            "Temp", "\u00B0C", new String[] { rosetta.MDC_TEMP_BLD.VALUE }, 35f, 39f, 32f, 42f, 20f, 60f, 5000L, 5000L, Color.black);
    ;

    VitalSign(String label, String units, String[] metric_ids, Float startingLow, Float startingHigh, Float criticalLow, Float criticalHigh,
            float minimum, float maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color) {
        this.label = label;
        this.units = units;
        this.metric_ids = metric_ids;
        this.startingLow = startingLow;
        this.startingHigh = startingHigh;
        this.minimum = minimum;
        this.maximum = maximum;
        this.criticalLow = criticalLow;
        this.criticalHigh = criticalHigh;
        this.valueMsWarningLow = valueMsWarningLow;
        this.valueMsWarningHigh = valueMsWarningHigh;
        this.color = color;
    }

    public Vital addToModel(VitalModel vitalModel) {
        return vitalModel.addVital(label, units, metric_ids, startingLow, startingHigh, criticalLow, criticalHigh, minimum, maximum,
                valueMsWarningLow, valueMsWarningHigh, color);
    }

    private final String label, units;;
    private final String[] metric_ids;
    private final Float startingLow, startingHigh, criticalLow, criticalHigh;
    private final Long valueMsWarningLow, valueMsWarningHigh;
    private final float minimum, maximum;
    private final Color color;
}
