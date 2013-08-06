package org.mdpnp.apps.testapp.pca;

import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;

public enum VitalSign {
  HeartRate("Heart Rate", "bpm", new int[] { ice.MDC_PULS_OXIM_PULS_RATE.VALUE/*, ice.MDC_PULS_RATE_NON_INV.VALUE, ice.MDC_BLD_PULS_RATE_INV.VALUE, ice.MDC_PULS_RATE.VALUE*/ }, 40, 120, 0, 250),
  SpO2("SpO\u2082", "%", new int[] { ice.MDC_PULS_OXIM_SAT_O2.VALUE }, 95, 100, 60, 100),
  RespiratoryRate("Respiratory Rate", "bpm", new int[] { ice.MDC_RESP_RATE.VALUE }, 8, 16, 0, 40),
  EndTidalCO2("etCO\u2082", "mmHg", new int[] { ice.MDC_AWAY_CO2_EXP.VALUE }, 20, 50, 0, 100),
  Temperature("Temp", "\u00B0C", new int[] {ice.MDC_TEMP_BLD.VALUE}, 35, 39, 20, 60);
  ;

  VitalSign(String label, String units, int[] names, float startingLow, float startingHigh, float minimum, float maximum) {
      this.label = label;
      this.units = units;
      this.names = names;
      this.startingLow = startingLow;
      this.startingHigh = startingHigh;
      this.minimum = minimum;
      this.maximum = maximum;
  }

  public Vital addToModel(VitalModel vitalModel) {
      return vitalModel.addVital(label, units, names, startingLow, startingHigh, minimum, maximum);
  }

  private final String label, units;;
  private final int[] names;
  private final float startingLow, startingHigh, minimum, maximum;
}