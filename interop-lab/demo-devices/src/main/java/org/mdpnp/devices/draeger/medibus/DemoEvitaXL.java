package org.mdpnp.devices.draeger.medibus;

import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.messaging.Gateway;

public class DemoEvitaXL extends AbstractDraegerVent {

	public DemoEvitaXL(Gateway gateway) {
		super(gateway);
	}
	public DemoEvitaXL(Gateway gateway, SerialSocket socket) {
		super(gateway, socket);
	}
	
	@Override
	public SerialProvider getSerialProvider() {
		SerialProvider serialProvider =  super.getSerialProvider();
		serialProvider.setDefaultSerialSettings(19200, DataBits.Eight, Parity.None, StopBits.One);
		return serialProvider;
	}
	
	@Override
	protected String iconResourceName() {
		return "evitaxl.png";
	}
}
