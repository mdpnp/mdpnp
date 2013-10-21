package org.mdpnp.clinicalscenarios.client.scenario.comparator;

import java.util.Comparator;

import org.mdpnp.clinicalscenarios.client.scenario.ScenarioProxy;

public class ScenarioAcksComparator implements Comparator<ScenarioProxy>{
	private boolean reverserOrder;
	
	/**
	 * Toggle comparison order
	 */
	public void switchOrder(){
		reverserOrder = !reverserOrder;
	}

	 //compare by Status ascending (lexicographically)
	public int compare(ScenarioProxy scn1, ScenarioProxy scn2) {
		if(reverserOrder)
			return (-1 * doCompare(scn1, scn2));
		else
			return doCompare(scn1, scn2);
	}
	
	  private  int doCompare(ScenarioProxy scn1, ScenarioProxy scn2) {
		  int scn1_Acks = scn1.getAcknowledgers()==null? 0 : scn1.getAcknowledgers().getAcknowledgersIDs().size();
		  int scn2_Acks = scn2.getAcknowledgers()==null? 0 : scn2.getAcknowledgers().getAcknowledgersIDs().size();
		  	  
		  if(scn1_Acks < scn2_Acks )
			  return -1;
		  else if(scn1_Acks == scn2_Acks)
			  return 0;
		  else return 1;
		  
	  }
	
	
}
