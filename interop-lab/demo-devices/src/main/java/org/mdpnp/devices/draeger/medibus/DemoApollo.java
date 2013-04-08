package org.mdpnp.devices.draeger.medibus;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.serial.SerialProvider;
import org.mdpnp.comms.serial.SerialSocket;
import org.mdpnp.comms.serial.SerialSocket.DataBits;
import org.mdpnp.comms.serial.SerialSocket.Parity;
import org.mdpnp.comms.serial.SerialSocket.StopBits;

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
