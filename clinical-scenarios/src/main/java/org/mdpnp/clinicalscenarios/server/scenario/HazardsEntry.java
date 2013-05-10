package org.mdpnp.clinicalscenarios.server.scenario;

import com.googlecode.objectify.annotation.Embed;

@SuppressWarnings("serial")
@Embed
public class HazardsEntry implements java.io.Serializable {
    private String description;
    private String factors;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getFactors() {
        return factors;
    }
    public void setFactors(String factors) {
        this.factors = factors;
    }
}
