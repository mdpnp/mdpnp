package org.mdpnp.devices.draeger.medibus;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.serial.SerialProvider;
import org.mdpnp.comms.serial.SerialSocket;
import org.mdpnp.comms.serial.SerialSocket.DataBits;
import org.mdpnp.comms.serial.SerialSocket.Parity;
import org.mdpnp.comms.serial.SerialSocket.StopBits;

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
