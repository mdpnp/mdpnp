package org.mdpnp.clinicalscenarios.client.scenario.comparator;

import java.util.Comparator;

import org.mdpnp.clinicalscenarios.client.scenario.ScenarioProxy;

/**
 * Custom comparator for Scenarios by Submitter<p>
 * @author dalonso@mdpnp.org <p>
 * 
 */
public class ScenarioSubmitterComparator implements Comparator<ScenarioProxy> {

	 //compare by Submitter ascending (lexicographically)
	  public int compare(ScenarioProxy scn1, ScenarioProxy scn2) {
		  if(scn1.getSubmitter()==null) return 1;//XXX we shouldn't need this two lines of code if every Scn has its submitter
		  if(scn2.getSubmitter()==null) return -1;
		  
	      if(scn1.getSubmitter().equalsIgnoreCase(scn2.getSubmitter())){	       
	          return 0;            
	      }
	      return scn1.getSubmitter().compareToIgnoreCase(scn2.getSubmitter());
	  }

}
