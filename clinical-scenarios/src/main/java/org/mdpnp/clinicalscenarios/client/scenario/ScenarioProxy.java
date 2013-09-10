package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.Date;

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
	
	BackgroundProxy getBackground();
	HazardsProxy getHazards();
	EquipmentProxy getEquipment();
	ProposedSolutionProxy getProposedSolution();
	BenefitsAndRisksProxy getBenefitsAndRisks();
	EnvironmentsProxy getEnvironments();
	ReferencesProxy  getReferences() ;
	
	//
	public String getSubmitter();
	public String getStatus();
	public void setSubmitter(String submitter);
	public void setStatus(String status);
	
	
}
