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
package org.mdpnp.apps.testapp.vital;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;

/**
 * @author Jeff Plourde
 *
 */
public enum VitalSign {
    HeartRate("Heart Rate", "bpm", new String[] { 
            rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, 
            rosetta.MDC_PULS_RATE_NON_INV.VALUE,
            rosetta.MDC_BLD_PULS_RATE_INV.VALUE,
            rosetta.MDC_ECG_CARD_BEAT_RATE.VALUE, 
            rosetta.MDC_ECG_HEART_RATE.VALUE, 
            rosetta.MDC_PULS_RATE.VALUE
             }, 40.0, 140.0, 20.0, 160.0, 0.0, 250.0, 5000L, 5000L, Color.green),
    SpO2PulseRate("SpO2 Pulse Rate", "bpm", new String[] {
            rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE
    }, 40.0, 140.0, 20.0, 160.0, 0.0, 250.0, 5000L, 5000L, Color.green),
    ECGHeartRate("ECG Heart Rate", "bpm", new String[] {
            rosetta.MDC_ECG_CARD_BEAT_RATE.VALUE,
            rosetta.MDC_ECG_HEART_RATE.VALUE
    }, 40.0, 140.0, 20.0, 160.0, 0.0, 250.0, 5000L, 5000L, Color.green),
    SpO2("SpO\u2082", "%", new String[] { rosetta.MDC_PULS_OXIM_SAT_O2.VALUE }, 95.0, 100.0, 85.0, 100.0, 50.0, 100.0, 5000L, 5000L, Color.pink),
    RespiratoryRate("Respiration Rate", "bpm", new String[] { rosetta.MDC_RESP_RATE.VALUE, rosetta.MDC_TTHOR_RESP_RATE.VALUE, rosetta.MDC_CO2_RESP_RATE.VALUE,
                    rosetta.MDC_VENT_RESP_RATE.VALUE, rosetta.MDC_AWAY_RESP_RATE.VALUE }, 10.0, 25.0, 4.0, 35.0, 0.0, 40.0, 5000L, 5000L,
                    Color.yellow), 
    EndTidalCO2("etCO\u2082", "mmHg", new String[] { rosetta.MDC_AWAY_CO2_ET.VALUE }, 20.0, 45.0, 10.0, 75.0, 0.0, 120.0,
                5000L, 5000L, Color.yellow), 
    Temperature("Temp", "\u00B0C", new String[] { rosetta.MDC_TEMP_BLD.VALUE }, 35.0, 39.0, 32.0, 42.0, 20.0,
                60.0, 5000L, 5000L, Color.black),
    InvSystolic("Invasive Systolic", "mmHg", new String[] { 
            rosetta.MDC_PRESS_BLD_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_AORT_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ART_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE,
            rosetta.MDC_PRESS_INTRA_CRAN_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ART_FEMORAL_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ART_PULM_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ART_UMB_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_LEFT_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_RIGHT_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_PULM_CAP_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_VEN_UMB_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_VENT_LEFT_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_VENT_RIGHT_SYS.VALUE,
    }, 90.0, 140.0, 60.0, 180.0, 0, 250.0, 5000L, 5000L, Color.red),
    InvDiastolic("Invasive Diastolic", "mmHg", new String[] {
            rosetta.MDC_PRESS_BLD_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_AORT_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_ART_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE,
            rosetta.MDC_PRESS_INTRA_CRAN_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_ART_FEMORAL_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_ART_PULM_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_ART_UMB_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_LEFT_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_RIGHT_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_PULM_CAP_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_VEN_UMB_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_VENT_LEFT_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_VENT_RIGHT_DIA.VALUE,
    }, 60.0, 100.0, 40.0, 120.0, 0, 200.0, 5000L, 5000L, Color.red),
    InvMean("Invasive Mean", "mmHg", new String[] {
            rosetta.MDC_PRESS_BLD_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_AORT_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_ART_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_ART_ABP_MEAN.VALUE,
            rosetta.MDC_PRESS_INTRA_CRAN_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_ART_FEMORAL_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_ART_PULM_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_ART_UMB_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_LEFT_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_RIGHT_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_PULM_CAP_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_VEN_UMB_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_VENT_LEFT_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_VENT_RIGHT_MEAN.VALUE,
    }, 60.0, 100.0, 40.0, 120.0, 0.0, 200.0, 5000L, 5000L, Color.red),
    NIBPSystolic("NIBP Systolic", "mmHg", new String[] {
            rosetta.MDC_PRESS_CUFF_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_NONINV_SYS.VALUE
    }, 90.0, 140.0, 60.0, 180.0, 0, 250.0, 5000L, 5000L, Color.red),
    NIBPDiastolic("NIBP Diastolic", "mmHg", new String[] {
            rosetta.MDC_PRESS_CUFF_DIA.VALUE,
            rosetta.MDC_PRESS_BLD_NONINV_DIA.VALUE            
    }, 60.0, 100.0, 40.0, 120.0, 0, 200.0, 5000L, 5000L, Color.red),
    NIBPMean("NIBP Mean", "mmHg", new String[] {
            rosetta.MDC_PRESS_CUFF_MEAN.VALUE,
            rosetta.MDC_PRESS_BLD_NONINV_MEAN.VALUE     
    }, 60.0, 100.0, 40.0, 120.0, 0.0, 200.0, 5000L, 5000L, Color.red),
    /* A rather dubious abuse of the design here to allow diastolic and systolic on the same chart */
    BothBP("Dia and Sys BP", "mmHg", new String[] {
    		rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE,
    		rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE
    }, 10.0, 300.0, 10.0, 300.0, 10.0, 300.0, 5000L, 5000L, Color.red
    ),

    Test("Test", "", new String[] {}, 60.0, 100.0, 40.0, 120.0, 10.0, 200.0, 5000L, 5000L, Color.red),
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

    //
    // To be a 1-to-1 lookup we can only handle more general case.
    // So we drop vitals that are subsets of more generic ones, like SpO2PulseRate vs HeartRate
    //
    static Map<String, VitalSign> buildVitalSignLookupTable() {

        Map<String, VitalSign> tbl = new HashMap<>();

        VitalSign [] all = VitalSign.values();
        for(VitalSign v : all) {
            for(String s : v.metric_ids) {
                VitalSign o = tbl.get(s);
                if(o == null || o.metric_ids.length<v.metric_ids.length) {
                    tbl.put(s, v);
                }
            }
        }

        return tbl;
    }

    private static final Map<String, VitalSign> REVERSE_LOOKUP = buildVitalSignLookupTable();

    public static VitalSign lookupByMetricId(String mid) {
        VitalSign vs = REVERSE_LOOKUP.get(mid);
        return vs;
    }

    public final String label, units;
    private final String[] metric_ids;
    public final Double startingLow, startingHigh, criticalLow, criticalHigh;
    public final Long valueMsWarningLow, valueMsWarningHigh;
    public final double minimum, maximum;
    public final Color color;
}
