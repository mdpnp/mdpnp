package org.mdpnp.apps.safetylockapplication;

import java.util.EventObject;

import org.mdpnp.apps.safetylockapplication.Resources.Algorithm;

public class SetAlarmEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	public Algorithm plethAlg = Algorithm.UNSPECIFIED;
	public int minDHrPr = -1;
	public int co2Maximum = -1;
	public int o2Minimum = -1;
	public int respRateMinimum = -1;
	
	public int maxO2RateOfChange = -1;
	public int maxDHrPrRateOfChange = -1;
	public int maxCo2RateOfChange = -1;
	public int maxRespRateRateOfChange = -1;
	
	public SetAlarmEvent(Object event) {
		super(event);
		
	}
}
