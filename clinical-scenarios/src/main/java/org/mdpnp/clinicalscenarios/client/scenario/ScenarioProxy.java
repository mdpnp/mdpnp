package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.List;

import org.mdpnp.clinicalscenarios.server.scenario.ScenarioEntity;
import org.mdpnp.clinicalscenarios.server.scenario.ScenarioLocator;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value=ScenarioEntity.class,locator=ScenarioLocator.class)
public interface ScenarioProxy extends EntityProxy {
	Long getId();
	String getTitle();
	void setTitle(String title);
	
	BackgroundProxy getBackground();
//	HazardsProxy getHazards();
//	EnvironmentsProxy getEnvironments();
	EquipmentProxy getEquipment();
	ProposedSolutionProxy getProposedSolution();
	BenefitsAndRisksProxy getBenefitsAndRisks();
//	List<EquipmentEntryProxy> getEquipmentList();//XXX delete
	
	//
	public String getSubmitter();
	public String getStatus();
	public void setSubmitter(String submitter);
	public void setStatus(String status);
	
	
}
