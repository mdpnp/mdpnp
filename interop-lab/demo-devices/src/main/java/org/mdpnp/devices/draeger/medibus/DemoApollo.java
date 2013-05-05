package org.mdpnp.devices.draeger.medibus;

import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.messaging.Gateway;

public class DemoApollo extends AbstractDraegerVent {

	public DemoApollo(Gateway gateway) {
		super(gateway);
	}
	
	public DemoApollo(Gateway gateway,  SerialSocket socket) {
		super(gateway, socket);
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
