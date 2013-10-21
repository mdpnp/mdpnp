package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.Set;

import org.mdpnp.clinicalscenarios.server.scenario.AssociatedTags;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=AssociatedTags.class)
public interface AssociatedTagsProxy extends ValueProxy {

	Set<String> getAssociatedTagNames();//Ticket-157
	void setAssociatedTagNames(Set<String> associatedTagNames);
}
