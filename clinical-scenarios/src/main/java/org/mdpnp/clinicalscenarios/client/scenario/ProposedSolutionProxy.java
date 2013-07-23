package org.mdpnp.clinicalscenarios.client.scenario;

import org.mdpnp.clinicalscenarios.server.scenario.ProposedSolutionValue;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=ProposedSolutionValue.class)
public interface ProposedSolutionProxy extends ValueProxy {

    String getProcess();

    void setProcess(String process);

    String getAlgorithm();

    void setAlgorithm(String algorithm);
}
