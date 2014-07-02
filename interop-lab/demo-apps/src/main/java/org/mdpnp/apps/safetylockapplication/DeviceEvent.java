package org.mdpnp.apps.safetylockapplication;

import java.util.ArrayList;

import org.mdpnp.apps.safetylockapplication.Resources.Algorithm;

public class DeviceEvent {

	public int pulseRate;
	public int heartRate;
	public int o2Saturation;
	public int co2Saturation;
	public int respiratoryRate;
	public ArrayList<Number> plethysmographSet;
	public boolean plethIsBad;
	public Algorithm plethAlgorithm;
	public String pumpMessage;
	
	public DeviceEvent(PatientEvent e, String message)
	{
		pulseRate = e.pulseRate;
		heartRate = e.heartRate;
		o2Saturation = e.o2Saturation;
		co2Saturation = e.co2Saturation;
		respiratoryRate = e.respiratoryRate;
		plethysmographSet = new ArrayList<Number>(e.plethysmographSet);
		plethIsBad = e.plethIsBad;
		plethAlgorithm = e.plethAlgorithm;
		pumpMessage = message;
	}

	public DeviceEvent() {
		
	}
}
