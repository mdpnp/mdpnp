package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.List;

import org.mdpnp.clinicalscenarios.server.scenario.Equipment;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=Equipment.class)
public interface EquipmentProxy extends ValueProxy {
    List<EquipmentEntryProxy> getEntries();
}
