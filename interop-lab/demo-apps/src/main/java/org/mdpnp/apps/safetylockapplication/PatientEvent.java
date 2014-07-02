package org.mdpnp.apps.safetylockapplication;

import java.util.ArrayList;
import java.util.EventObject;

import org.mdpnp.apps.safetylockapplication.Resources.Algorithm;

public class PatientEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	public int pulseRate;
	public int heartRate;
	public int o2Saturation;
	public int co2Saturation;
	public int respiratoryRate;
	public ArrayList<Number> plethysmographSet;
	
	public boolean plethIsBad;
	public Algorithm plethAlgorithm;
	
//	public boolean O2RocBad;
//	public boolean Co2RocBad;
//	public boolean RespRocBad;
//	public boolean HrprRocBad;
	
	public PatientEvent(Object o, int pulse, int heart, int oxygen, int co2, int resp, boolean pleth, Algorithm alg, ArrayList<Number> set) {
		super(o);
		
		pulseRate = pulse;
		heartRate = heart;
		o2Saturation = oxygen;
		co2Saturation = co2;
		respiratoryRate = resp;
		plethysmographSet = new ArrayList<Number>(set);
		plethIsBad = pleth;
		plethAlgorithm = alg;
	}

}
