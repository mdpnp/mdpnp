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
    HeartRate("Heart Rate", "bpm", new String[] { 
            rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, rosetta.MDC_PULS_RATE_NON_INV.VALUE,
            rosetta.MDC_BLD_PULS_RATE_INV.VALUE, rosetta.MDC_ECG_CARD_BEAT_RATE.VALUE, 
            rosetta.MDC_ECG_HEART_RATE.VALUE, rosetta.MDC_PULS_RATE.VALUE,
            rosetta.MDC_BLD_PULS_RATE_INV.VALUE}, 
            40.0, 140.0, 20.0, 160.0, 0.0, 250.0, 5000L, 5000L, Color.green), 
            
    SpO2(
            "SpO\u2082", "%", new String[] { rosetta.MDC_PULS_OXIM_SAT_O2.VALUE }, 95.0, 100.0, 85.0, 100.0, 50.0, 100.0, 5000L, 5000L, Color.pink), RespiratoryRate(
            "Resp Rate", "bpm", new String[] { 
                    rosetta.MDC_RESP_RATE.VALUE, 
                    rosetta.MDC_TTHOR_RESP_RATE.VALUE, 
                    rosetta.MDC_CO2_RESP_RATE.VALUE, 
                    rosetta.MDC_VENT_RESP_RATE.VALUE, 
                    rosetta.MDC_AWAY_RESP_RATE.VALUE }, 
                    10.0, 25.0, 4.0, 35.0, 0.0, 40.0, 5000L, 5000L, Color.yellow), EndTidalCO2(
            "etCO\u2082", "mmHg", new String[] { rosetta.MDC_AWAY_CO2_ET.VALUE }, 20.0, 45.0, 10.0, 75.0, 0.0, 120.0, 5000L, 5000L, Color.yellow), Temperature(
            "Temp", "\u00B0C", new String[] { rosetta.MDC_TEMP_BLD.VALUE }, 35.0, 39.0, 32.0, 42.0, 20.0, 60.0, 5000L, 5000L, Color.black);
    ;

    VitalSign(String label, String units, String[] metric_ids, Double startingLow, Double startingHigh, Double criticalLow, Double criticalHigh,
            double minimum, double maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color) {
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
    private final Double startingLow, startingHigh, criticalLow, criticalHigh;
    private final Long valueMsWarningLow, valueMsWarningHigh;
    private final double minimum, maximum;
    private final Color color;
}
