package org.mdpnp.clinicalscenarios.client.scenario;

import org.mdpnp.clinicalscenarios.server.scenario.HazardsEntry;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=HazardsEntry.class)
public interface HazardsEntryProxy extends ValueProxy {
    String getDescription();
    void setDescription(String description);
    String getFactors();
    void setFactors(String factors);
    
	String getExpected();
	void setExpected(String expected);
	String getSeverity();
	void setSeverity(String severity);
}
