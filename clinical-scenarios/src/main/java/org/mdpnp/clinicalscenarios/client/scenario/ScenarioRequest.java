package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName("org.mdpnp.clinicalscenarios.server.scenario.ScenarioEntity")
public interface ScenarioRequest extends RequestContext {
	Request<List<ScenarioProxy>> findAllScenarios();
	Request<ScenarioProxy> findById(Long id);
	Request<List<Long>> findAllIds();
	InstanceRequest<ScenarioProxy, ScenarioProxy> persist();
	InstanceRequest<ScenarioProxy, Void> remove();
	Request<List<ScenarioProxy>> searchByKeywords(String keywords);
	Request<ScenarioProxy> create();
}
