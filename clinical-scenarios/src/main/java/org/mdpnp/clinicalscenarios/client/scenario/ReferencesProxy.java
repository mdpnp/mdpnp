package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.List;

import org.mdpnp.clinicalscenarios.server.scenario.References;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=References.class)
public interface ReferencesProxy extends ValueProxy {
	List<String> getLinkedRefenrences();
	void setLinkedRefenrences(List<String> linkedRefenrences);

}
