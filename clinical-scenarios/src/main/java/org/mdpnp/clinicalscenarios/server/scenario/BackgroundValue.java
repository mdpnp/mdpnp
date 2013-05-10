package org.mdpnp.clinicalscenarios.server.scenario;

import com.googlecode.objectify.annotation.Embed;

@SuppressWarnings("serial")
@Embed
public class BackgroundValue implements java.io.Serializable {
    private String currentState;
    private String proposedState;
    
    public String getCurrentState() {
        return currentState;
    }
    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }
    public String getProposedState() {
        return proposedState;
    }
    public void setProposedState(String proposedState) {
        this.proposedState = proposedState;
    }
}