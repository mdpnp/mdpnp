package org.mdpnp.apps.testapp.pca;

import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;

public enum VitalSign {
  HeartRate("Heart Rate", "bpm", new int[] { ice.Physio._MDC_PULS_OXIM_PULS_RATE, ice.Physio._MDC_PULS_RATE, ice.Physio._MDC_PULS_RATE_NON_INV, ice.Physio._MDC_BLD_PULS_RATE_INV }, 40f, 140f, 20f, 160f, 0f, 250f, 5000L, 5000L),
  SpO2("SpO\u2082", "%", new int[] { ice.Physio._MDC_PULS_OXIM_SAT_O2 }, 90f, null, 80f, null, 50f, 100f, 5000L, 5000L),
  RespiratoryRate("Resp Rate", "bpm", new int[] { ice.Physio._MDC_RESP_RATE }, 10f, 16f, 2f, 35f, 0f, 40f, 5000L, 5000L),
  EndTidalCO2("etCO\u2082", "mmHg", new int[] { ice.Physio._MDC_AWAY_CO2_EXP }, 20f, 100f, 10f, 110f, 0f, 120f, 5000L, 5000L),
  Temperature("Temp", "\u00B0C", new int[] {ice.Physio._MDC_TEMP_BLD}, 35f, 39f, 32f, 42f, 20f, 60f, 5000L, 5000L);
  ;

  VitalSign(String label, String units, int[] names, Float startingLow, Float startingHigh, Float criticalLow, Float criticalHigh, float minimum, float maximum, Long valueMsWarningLow, Long valueMsWarningHigh) {
      this.label = label;
      this.units = units;
      this.names = names;
      this.startingLow = startingLow;
      this.startingHigh = startingHigh;
      this.minimum = minimum;
      this.maximum = maximum;
      this.criticalLow = criticalLow;
      this.criticalHigh = criticalHigh;
      this.valueMsWarningLow = valueMsWarningLow;
      this.valueMsWarningHigh = valueMsWarningHigh;
  }

  public Vital addToModel(VitalModel vitalModel) {
      return vitalModel.addVital(label, units, names, startingLow, startingHigh, criticalLow, criticalHigh, minimum, maximum, valueMsWarningLow, valueMsWarningHigh);
  }

  private final String label, units;;
  private final int[] names;
  private final Float startingLow, startingHigh, criticalLow, criticalHigh;
  private final Long valueMsWarningLow, valueMsWarningHigh;
  private final float minimum, maximum;
}