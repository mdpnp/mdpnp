package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName("org.mdpnp.clinicalscenarios.server.scenario.ScenarioEntity")
public interface ScenarioRequest extends RequestContext {
	Request<List<ScenarioProxy>> findAllScenarios();
	Request<ScenarioProxy> findById(Long id);
	Request<List<ScenarioProxy>>  searchByCreationDateRange(Date dateFrom, Date dateUntil);
	Request<List<Long>> findAllIds();
	InstanceRequest<ScenarioProxy, ScenarioProxy> persist();
	InstanceRequest<ScenarioProxy, ScenarioProxy> submit();
	InstanceRequest<ScenarioProxy, ScenarioProxy> lock(String username);
	InstanceRequest<ScenarioProxy, ScenarioProxy> unlock();
	InstanceRequest<ScenarioProxy, Void> remove();
	Request<List<ScenarioProxy>> searchByKeywords(String keywords);
	Request<ScenarioProxy> create();
	
	Request<List<ScenarioProxy>> searchByStatus(String status);
	Request<List<ScenarioProxy>> searchByStatus(Set<String> nStatus);
	Request<List<ScenarioProxy>> searchScnBySubmitter(String submitter);
	Request<List<ScenarioProxy>> searchScnBySubmitter(String submitter, Date dateFrom, Date dateUntil);
	InstanceRequest<ScenarioProxy, ScenarioProxy> persistWithNotification(String toWho, String subject, String messageText);
	Request<List<ScenarioProxy>> searchByFilter_OrBehavior(String sBackground, String sProposed, String sProcess, String sAlgorithm, String sBenefits, String sRisks, String title);
//	Request<List<ScenarioProxy>> searchByFilter_AndBehavior(String sBackground, String sProposed, String sProcess, String sAlgorithm, String sBenefits, String sRisks, String title);
}
