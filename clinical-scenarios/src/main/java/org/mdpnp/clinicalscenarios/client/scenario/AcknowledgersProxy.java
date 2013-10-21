package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.Set;

import org.mdpnp.clinicalscenarios.server.scenario.Acknowledgers;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=Acknowledgers.class)
public interface AcknowledgersProxy extends ValueProxy {
	Set<String> getAcknowledgersIDs();
	void setAcknowledgersIDs(Set<String> acknowledgersIDs);
	
	Integer getSize();
}
