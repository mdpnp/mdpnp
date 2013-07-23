package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.List;

import org.mdpnp.clinicalscenarios.server.scenario.Hazards;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=Hazards.class)
public interface HazardsProxy extends ValueProxy {
    List<HazardsEntryProxy> getEntries();
}
