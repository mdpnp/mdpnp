package org.mdpnp.clinicalscenarios.server.scenario;

import com.googlecode.objectify.annotation.Embed;

@SuppressWarnings("serial")
@Embed
public class BenefitsAndRisksValue implements java.io.Serializable {
    private String benefits;
    private String risks;
    public String getBenefits() {
        return benefits;
    }
    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }
    public String getRisks() {
        return risks;
    }
    public void setRisks(String risks) {
        this.risks = risks;
    }
    
}
