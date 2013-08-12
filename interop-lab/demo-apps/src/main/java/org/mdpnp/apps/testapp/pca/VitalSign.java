package org.mdpnp.apps.testapp.pca;

import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;

public enum VitalSign {
  HeartRate("Heart Rate", "bpm", new int[] { ice.MDC_PULS_OXIM_PULS_RATE.VALUE, ice.MDC_PULS_RATE.VALUE /*, ice.MDC_PULS_RATE_NON_INV.VALUE, ice.MDC_BLD_PULS_RATE_INV.VALUE, ice.MDC_PULS_RATE.VALUE*/ }, 40f, 140f, 20f, 160f, 0f, 250f),
  SpO2("SpO\u2082", "%", new int[] { ice.MDC_PULS_OXIM_SAT_O2.VALUE }, 95f, null, 80f, null, 50f, 100f),
  RespiratoryRate("Respiratory Rate", "bpm", new int[] { ice.MDC_RESP_RATE.VALUE }, 8f, 16f, 2f, 35f, 0f, 40f),
  EndTidalCO2("etCO\u2082", "mmHg", new int[] { ice.MDC_AWAY_CO2_EXP.VALUE }, 20f, 100f, 10f, 110f, 0f, 120f),
  Temperature("Temp", "\u00B0C", new int[] {ice.MDC_TEMP_BLD.VALUE}, 35f, 39f, 32f, 42f, 20f, 60f);
  ;

  VitalSign(String label, String units, int[] names, Float startingLow, Float startingHigh, Float criticalLow, Float criticalHigh, float minimum, float maximum) {
      this.label = label;
      this.units = units;
      this.names = names;
      this.startingLow = startingLow;
      this.startingHigh = startingHigh;
      this.minimum = minimum;
      this.maximum = maximum;
      this.criticalLow = criticalLow;
      this.criticalHigh = criticalHigh;
  }

  public Vital addToModel(VitalModel vitalModel) {
      return vitalModel.addVital(label, units, names, startingLow, startingHigh, criticalLow, criticalHigh, minimum, maximum);
  }

  private final String label, units;;
  private final int[] names;
  private final Float startingLow, startingHigh, criticalLow, criticalHigh;
  private final float minimum, maximum;
}