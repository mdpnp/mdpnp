package org.mdpnp.clinicalscenarios.client.scenario.comparator;

import java.util.Comparator;
import java.util.Date;

import org.mdpnp.clinicalscenarios.client.scenario.ScenarioProxy;

public class ScenarioDateComparator implements Comparator<ScenarioProxy>{
	
	public enum TypeOfDate {CreationDate, ModificationDate};
	private TypeOfDate typeOdDate;
	private int reverserOrder = 1;
	
	public ScenarioDateComparator(TypeOfDate typeOdDate){
		this.typeOdDate = typeOdDate;
	}
	
	/**
	 * Toggle comparison order
	 */
	public void switchOrder(){
		reverserOrder *= -1;
	}

	 //compare by Status ascending (lexicographically)
	public int compare(ScenarioProxy scn1, ScenarioProxy scn2) {
		if(typeOdDate==TypeOfDate.CreationDate)
			return reverserOrder * doCompareCreation(scn1, scn2);
		else
			return reverserOrder * doCompareModification(scn1, scn2);
	}
	
	  private  int doCompareCreation(ScenarioProxy scn1, ScenarioProxy scn2) {
		  Date scn1_creationDate = scn1.getCreationDate();
		  Date scn2_creationDate= scn2.getCreationDate();
		  
		  return scn1_creationDate.compareTo(scn2_creationDate);		  
	  }
	  
	  private  int doCompareModification(ScenarioProxy scn1, ScenarioProxy scn2) {
		  Date scn1_modificationDate = scn1.getModificationDate();
		  Date scn2_modificationDate= scn2.getModificationDate();
		  
		  return scn1_modificationDate.compareTo(scn2_modificationDate);		  
	  }
}
