package org.mdpnp.clinicalscenarios.client.scenario;

import org.mdpnp.clinicalscenarios.server.scenario.BackgroundValue;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=BackgroundValue.class)
public interface BackgroundProxy extends ValueProxy {
    String getCurrentState();
    void setCurrentState(String currentState);
    String getProposedState();
    void setProposedState(String proposedState);
}
