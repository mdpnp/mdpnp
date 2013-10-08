package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.Date;
import java.util.Set;

import org.mdpnp.clinicalscenarios.server.scenario.ScenarioEntity;
import org.mdpnp.clinicalscenarios.server.scenario.ScenarioLocator;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value=ScenarioEntity.class,locator=ScenarioLocator.class)
public interface ScenarioProxy extends EntityProxy {
	Long getId();
	String getTitle();
	void setTitle(String title);
	void setCreationDate(Date d);
	Date getCreationDate();
	void setModificationDate(Date d);
	Date getModificationDate();
	void setAcknowledgers(Set<String> acknowledgers); 
	Set<String> getAcknowledgers();
	
	BackgroundProxy getBackground();
	HazardsProxy getHazards();
	EquipmentProxy getEquipment();
	ProposedSolutionProxy getProposedSolution();
	BenefitsAndRisksProxy getBenefitsAndRisks();
	EnvironmentsProxy getEnvironments();
	ReferencesProxy  getReferences() ;
	
	//
	public String getSubmitter();
	public void setSubmitter(String submitter);
	public String getStatus();
	public void setStatus(String status);
	public String getLastActionTaken();
	public void setLastActionTaken(String lastActionTaken);
	public String getLastActionUser();
	public void setLastActionUser(String lastActionUser);
	public String getLockOwner();
	public void setLockOwner(String lockOwner);
	
	
}
