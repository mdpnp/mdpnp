package org.mdpnp.devices.draeger.medibus;

import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;

public class DemoApollo extends AbstractDraegerVent {

	public DemoApollo(int domainId) {
		super(domainId);
	}
	
	public DemoApollo(int domainId,  SerialSocket socket) {
		super(domainId, socket);
	}
	
	@Override
	public SerialProvider getSerialProvider() {
		SerialProvider serialProvider =  super.getSerialProvider();
		serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.Even, StopBits.One);
		return serialProvider;
	}
	
	@Override
	protected String iconResourceName() {
		return "apollo.png";
	}

	
}
