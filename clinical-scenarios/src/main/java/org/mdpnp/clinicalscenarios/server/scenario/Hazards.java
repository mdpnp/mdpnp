package org.mdpnp.clinicalscenarios.server.scenario;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Embed;

@SuppressWarnings("serial")
@Embed
public class Hazards implements java.io.Serializable {
    private List<HazardsEntry> entries = new ArrayList<HazardsEntry>();

    public List<HazardsEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<HazardsEntry> entries) {
        this.entries = entries;
    }
    
}
