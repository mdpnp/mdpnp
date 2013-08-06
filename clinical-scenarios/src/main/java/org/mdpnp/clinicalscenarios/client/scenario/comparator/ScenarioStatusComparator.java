package org.mdpnp.clinicalscenarios.client.scenario.comparator;

import java.util.Comparator;

import org.mdpnp.clinicalscenarios.client.scenario.ScenarioPanel;
import org.mdpnp.clinicalscenarios.client.scenario.ScenarioProxy;

/**
 * Custom comparator for Scenarios by Status<p>
 * @author dalonso@mdpnp.org <p>
 *
 */
public class ScenarioStatusComparator implements Comparator<ScenarioProxy> {
	
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
		  if(scn1.getStatus()==null) return 1;//XXX we shouldn't need this two lines because every Scn MUST have a Satus
		  if(scn2.getStatus()==null) return -1;
		  
	        if(scn1.getStatus().equalsIgnoreCase(scn2.getStatus())){	       
	            return 0;            
	        }else if (scn1.getStatus().equals(ScenarioPanel.SCN_STATUS_UNSUBMITTED))
	        	return -1;
	        else if(scn1.getStatus().equals(ScenarioPanel.SCN_STATUS_SUBMITTED)){
	        	if(scn2.getStatus().equals(ScenarioPanel.SCN_STATUS_UNSUBMITTED)) return 1;
	        	if(scn2.getStatus().equals(ScenarioPanel.SCN_STATUS_SUBMITTED)) return 0;
	        	else return -1;	        	
	        }else if(scn1.getStatus().equals(ScenarioPanel.SCN_STATUS_APPROVED)){
	        	if(scn2.getStatus().equals(ScenarioPanel.SCN_STATUS_APPROVED)) return 0;
	        	if(scn2.getStatus().equals(ScenarioPanel.SCN_STATUS_REJECTED)) return -1;
	        	else return 1;
	        }else if(scn1.getStatus().equals(ScenarioPanel.SCN_STATUS_REJECTED)){
	        	if(scn2.getStatus().equals(ScenarioPanel.SCN_STATUS_REJECTED)) return 0;
	        	else return 1;
	        }
	        //if everything fails
	        return scn1.getStatus().compareToIgnoreCase(scn2.getStatus());
	  }
}
