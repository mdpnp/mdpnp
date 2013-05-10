package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.List;

import org.mdpnp.clinicalscenarios.server.scenario.Environments;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=Environments.class)
public interface EnvironmentsProxy extends ValueProxy {
    List<String> getCliniciansInvolved();
    void setCliniciansInvolved(List<String> cliniciansInvolved);
    List<String> getClinicalEnvironments();
    void setClinicalEnvironments(List<String> clinicalEnvironments);
}
