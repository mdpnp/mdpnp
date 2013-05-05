package org.mdpnp.devices.hospira.symbiq;

import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.messaging.Gateway;

public class DemoSymbiq extends AbstractSimulatedConnectedDevice {

	public DemoSymbiq(Gateway gateway) {
		super(gateway);
		nameUpdate.setValue("Symbiq");
		guidUpdate.setValue("xxx");
	}
	@Override
	protected String iconResourceName() {
		return "symbiq.png";
	}
	
}
