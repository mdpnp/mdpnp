package org.mdpnp.apps.testapp;

import java.util.HashMap;
import java.util.Map;

/**
 * This class acts as a registry of human readable names, units etc. for metrics.
 * Human readable names for series, units etc. are dotted around in various places.
 * Notably, they appear in the device panels.  Since we want to use them in other places
 * such as the Chart application, we can add them here.
 * This class just deals in simple String objects - classes that want ObservableStringValue
 * etc. should construct their own from the String objects here. 
 * @author Simon
 *
 */
public class HumanReadable {
	
	public static final Map<String, String> MetricLabels;
	
	static {
		MetricLabels=new HashMap<>();
		MetricLabels.put(rosetta.MDC_RESP_RATE.VALUE, "Resp Rate");
		MetricLabels.put(rosetta.MDC_TTHOR_RESP_RATE.VALUE, "Respiratory Rate");
		MetricLabels.put(rosetta.MDC_CO2_RESP_RATE.VALUE, "etCO2");
		MetricLabels.put(rosetta.MDC_ECG_HEART_RATE.VALUE, "Heart Rate");
		MetricLabels.put(rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, "Pulse");
		MetricLabels.put(rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, "SpO\u2082");
		
		/*
		 * Some new values for ventilator application.  Note that not all of these keys are using rosetta or ice
		 * fields.  Some come from PB directly and are defined in the resource file pb840.fields .   
		 * 
		 * 
		 */
		MetricLabels.put("PB_SETTING_PEEP", "PEEP");
		MetricLabels.put("PB_SETTING_RESPIRATORY_RATE", "RR");
		MetricLabels.put("PB_SETTING_APNEA_OXYGEN_PERCENT", "O\u2082");
		
		/*
		 * These are units...
		 */
		MetricLabels.put("MDC_DIM_RESP_PER_MIN", "bpm");
		
	}
	
	

}
