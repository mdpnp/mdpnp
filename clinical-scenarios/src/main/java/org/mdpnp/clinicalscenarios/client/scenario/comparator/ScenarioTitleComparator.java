package org.mdpnp.clinicalscenarios.client.scenario.comparator;

import java.util.Comparator;
import org.mdpnp.clinicalscenarios.client.scenario.ScenarioProxy;

/**
 * Custom comparator for Scenarios by title <P>
 * @author dalonso@mdpnp.org <p>
 * 
 */
public class ScenarioTitleComparator implements Comparator<ScenarioProxy> {

	 //compare by Title ascending (lexicographically)
	  public int compare(ScenarioProxy scn1, ScenarioProxy scn2) {
		  //in case any of the Scn had no tilte:
		  if(scn1.getTitle()==null || scn1.getTitle().trim().equals("")) return 1;
		  if(scn2.getTitle()==null || scn2.getTitle().trim().equals("")) return -1;
		  
	      if(scn1.getTitle().equalsIgnoreCase(scn2.getTitle())){	       
	          return 0;            
	      }
	      return scn1.getTitle().compareToIgnoreCase(scn2.getTitle());
	  }
}
