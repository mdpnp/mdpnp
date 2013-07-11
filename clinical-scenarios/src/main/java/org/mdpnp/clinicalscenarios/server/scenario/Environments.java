package org.mdpnp.clinicalscenarios.server.scenario;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Embed;

@SuppressWarnings("serial")
@Embed
public class Environments implements java.io.Serializable {
    private List<String> cliniciansInvolved = new ArrayList<String>();
    private List<String> clinicalEnvironments = new ArrayList<String>();
   
    public List<String> getCliniciansInvolved() {
        return cliniciansInvolved;
    }
    public void setCliniciansInvolved(List<String> cliniciansInvolved) {
        this.cliniciansInvolved = cliniciansInvolved;
    }
    
    public List<String> getClinicalEnvironments() {
        return clinicalEnvironments;
    }
    public void setClinicalEnvironments(List<String> clinicalEnvironments) {
        this.clinicalEnvironments = clinicalEnvironments;
    }
    
    
}
