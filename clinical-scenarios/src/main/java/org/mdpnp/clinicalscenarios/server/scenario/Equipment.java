package org.mdpnp.clinicalscenarios.server.scenario;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Embed;

@SuppressWarnings("serial")
@Embed
public class Equipment implements java.io.Serializable {
    private List<EquipmentEntry> entries = new ArrayList<EquipmentEntry>();
    public List<EquipmentEntry> getEntries() {
        return entries;
    }
    public void setEntries(List<EquipmentEntry> entries) {
        this.entries = entries;
    }
}
