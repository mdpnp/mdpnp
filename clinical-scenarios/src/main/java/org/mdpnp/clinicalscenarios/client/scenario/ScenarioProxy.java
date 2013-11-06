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
	void setModificationDate(Date d);
	Date getModificationDate();
	
	BackgroundProxy getBackground();
	HazardsProxy getHazards();
	EquipmentProxy getEquipment();
	ProposedSolutionProxy getProposedSolution();
	BenefitsAndRisksProxy getBenefitsAndRisks();
	EnvironmentsProxy getEnvironments();
	ReferencesProxy  getReferences() ;
	AcknowledgersProxy getAcknowledgers();//TICKET-163
	AssociatedTagsProxy getAssociatedTags();//TICKET-157
	FeedbackProxy getFeedback();//TICKET-197
	
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
