package org.mdpnp.clinicalscenarios.client.scenario;

import org.mdpnp.clinicalscenarios.server.scenario.BenefitsAndRisksValue;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=BenefitsAndRisksValue.class)
public interface BenefitsAndRisksProxy extends ValueProxy {

    String getBenefits();

    void setBenefits(String benefits);

    String getRisks();

    void setRisks(String risks);
}
