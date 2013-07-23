package org.mdpnp.devices.hospira.symbiq;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;

public class DemoSymbiq extends AbstractSimulatedConnectedDevice {

	public DemoSymbiq(int domainId, EventLoop eventLoop) {
		super(domainId, eventLoop);
		AbstractSimulatedDevice.randomUDI(deviceIdentity);
		deviceIdentity.manufacturer = "Hospira";
		deviceIdentity.model = "Symbiq";
		deviceIdentity.serial_number = "xxx";
		deviceIdentityHandle = deviceIdentityWriter.register_instance(deviceIdentity);
		deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
	}
	@Override
	protected String iconResourceName() {
		return "symbiq.png";
	}
	
}
